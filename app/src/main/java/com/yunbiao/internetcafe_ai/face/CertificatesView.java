package com.yunbiao.internetcafe_ai.face;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.util.ImageUtils;
import com.arcsoft.imageutil.ArcSoftImageFormat;
import com.arcsoft.imageutil.ArcSoftImageUtil;
import com.arcsoft.imageutil.ArcSoftImageUtilError;
import com.yunbiao.internetcafe_ai.cache.CacheUtil;
import com.yunbiao.internetcafe_ai.common.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CertificatesView extends RelativeLayout {
    private static final String TAG = "FaceView";

    private Integer rgbCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;//CAMERA_FACING_FRONT
    private Integer irCameraID = Camera.CameraInfo.CAMERA_FACING_BACK;//CAMERA_FACING_BACK

    public static float SIMILAR_THRESHOLD = 0.75F;
    private boolean isRGBInitialized = false;
    private boolean isIRInitialized = false;
    private boolean isIRLiveness = true;

    private SurfaceView rgbPreviewView;//预览
    private FaceRectView faceRectView;//画框
    private CameraHelper rgbCameraHelper;
    private DrawHelper drawHelper;
    private Camera.Size previewSize;

    private SurfaceView irPreviewView;//红外预览
    private CameraHelper irCameraHelper;
    private Camera.Size previewSizeIr;

    private FaceEngine imgDetectEngine;//图片检测
    private FaceEngine videoDetectEngine;//视频检测
    private FaceEngine frEngine;//特征提取
    private FaceEngine compareEngine;//对比
    private FaceEngine flEngine;//RGB活体

    private ExecutorService flExecutor;//活体检测线程
    private LinkedBlockingQueue<Runnable> flThreadQueue;

    private FaceCallback callback;

    public CertificatesView(Context context) {
        super(context);
        initView();
    }

    public CertificatesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CertificatesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setCallback(FaceCallback callback) {
        this.callback = callback;
    }

    public void setSimilarThreshold() {
        int similarInt = CacheUtil.instance().getInt(Constants.Key.SIMILAR, Constants.Default.SIMILAR);
        Log.e(TAG, "initView: 即将设置阈值为：" + ((float) similarInt / 100));
        SIMILAR_THRESHOLD = ((float) similarInt / 100);
    }

    private void initView() {
        isIRLiveness = CacheUtil.instance().getBoolean(Constants.Key.IS_IR,Constants.Default.IS_IR);

        setSimilarThreshold();

        setBackgroundColor(Color.TRANSPARENT);

        rgbPreviewView = new SurfaceView(getContext());
        //在布局结束后才做初始化操作
        rgbPreviewView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        LayoutParams rgbLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rgbLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(rgbPreviewView, rgbLayoutParams);

        if(isIRLiveness){
            irPreviewView = new SurfaceView(getContext());
            irPreviewView.setZOrderMediaOverlay(true);
            irPreviewView.getViewTreeObserver().addOnGlobalLayoutListener(irOnGlobalLayoutListener);
            LayoutParams irLayoutParams = new LayoutParams(200, 200);
            irLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT | RelativeLayout.ALIGN_PARENT_TOP);
            addView(irPreviewView,irLayoutParams);
        }

        faceRectView = new FaceRectView(getContext());
        LayoutParams rectLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rectLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(faceRectView, rectLayoutParams);


        /**
         * fl 线程队列大小
         */
        int flQueueSize = 5;
        flThreadQueue = new LinkedBlockingQueue<Runnable>(flQueueSize);
        flExecutor = new ThreadPoolExecutor(1, flQueueSize, 0, TimeUnit.MILLISECONDS, flThreadQueue);
    }

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            rgbPreviewView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            initEngine();
            initRGBCamera();

            isRGBInitialized = true;

            if (callback != null) {
                callback.onReady();
            }
        }
    };

    private ViewTreeObserver.OnGlobalLayoutListener irOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            irPreviewView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            Log.e(TAG, "onGlobalLayout: IR预览已加载完成，准备开启摄像头");

            initIRCamera();

            isIRInitialized = true;

        }
    };

    /**
     * 初始化引擎
     */
    private void initEngine() {
        videoDetectEngine = new FaceEngine();
        videoDetectEngine.init(getContext(), DetectMode.ASF_DETECT_MODE_VIDEO, DetectFaceOrientPriority.ASF_OP_ALL_OUT,
                16, 1, FaceEngine.ASF_FACE_DETECT);

        imgDetectEngine = new FaceEngine();
        imgDetectEngine.init(getContext(), DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_ALL_OUT,
                16, 1, FaceEngine.ASF_FACE_DETECT);

        frEngine = new FaceEngine();
        frEngine.init(getContext(), DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_ALL_OUT,
                16, 1, FaceEngine.ASF_FACE_RECOGNITION);

        compareEngine = new FaceEngine();
        compareEngine.init(getContext(), DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_0_ONLY, 16, 1, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT);

        flEngine = new FaceEngine();
        flEngine.init(getContext(), DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_ALL_OUT,
                16, 1, isIRLiveness ? FaceEngine.ASF_IR_LIVENESS : FaceEngine.ASF_LIVENESS);
    }

    private void initRGBCamera() {
        int angle = CacheUtil.instance().getInt(Constants.Key.RGB_CAMERA_ANGLE, Constants.Default.RGB_CAMERA_ANGLE);
        rgbCameraHelper = new CameraHelper.Builder()
                .previewViewSize(new Point(rgbPreviewView.getMeasuredWidth(), rgbPreviewView.getMeasuredHeight()))
                .rotation(angle)
                .specificCameraId(rgbCameraID)
                .isMirror(true)
                .previewOn(rgbPreviewView)
                .cameraListener(cameraListener)
                .build();
        rgbCameraHelper.init();
        rgbCameraHelper.start();
    }

    private void initIRCamera(){
        int angle = CacheUtil.instance().getInt(Constants.Key.IR_CAMERA_ANGLE, Constants.Default.IR_CAMERA_ANGLE);
        irCameraHelper = new CameraHelper.Builder()
                .previewViewSize(new Point(rgbPreviewView.getMeasuredWidth(), rgbPreviewView.getMeasuredHeight()))
                .rotation(angle)
                .specificCameraId(irCameraID)
                .isMirror(true)
                .previewOn(irPreviewView)
                .cameraListener(irCameraListener)
                .build();
        irCameraHelper.init();
        irCameraHelper.start();
    }

    private byte[] rgbData;
    private List<FaceInfo> faceInfoList;
    private CameraListener cameraListener = new CameraListener() {

        @Override
        public void onPreview(final byte[] nv21, Camera camera) {
            rgbData = nv21;
            if (faceRectView != null) {
                faceRectView.clearFaceInfo();
            }

            faceInfoList = new ArrayList<>();
            videoDetectEngine.detectFaces(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList);

            int liveness = -1;
            FaceInfo faceInfo = null;
            if (faceInfoList.size() > 0) {
                faceInfo = faceInfoList.get(0);

                Integer integer = livenessMap.get(faceInfo.getFaceId());
                if(integer == null || integer != LivenessInfo.ALIVE){
                    requestFaceLiveness(rgbData,irData,faceInfo,previewSize.width,previewSize.height,isIRLiveness);
                }
                if(integer != null){
                    liveness = integer;
                }

                clearLivenessMap(faceInfo);
            }

            if (callback != null) {
                callback.onFaceDetection(faceInfo != null, faceInfo,liveness);
            }

            if (faceInfo != null) {
                Rect rect = faceInfo.getRect();
                drawHelper.draw(faceRectView, new DrawInfo(drawHelper.adjustRect(rect), 0, 0, 0, Color.YELLOW, ""));
            }
        }

        @Override
        public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
            boolean isVMirror = CacheUtil.instance().getBoolean(Constants.Key.RECT_H_MIRROR,Constants.Default.RECT_H_MIRROR);
            boolean isHMirror = CacheUtil.instance().getBoolean(Constants.Key.RECT_V_MIRROR,Constants.Default.RECT_V_MIRROR);

            previewSize = camera.getParameters().getPreviewSize();
            drawHelper = new DrawHelper(previewSize.width, previewSize.height,
                    rgbPreviewView.getWidth(), rgbPreviewView.getHeight(),
                    displayOrientation, cameraId,
                    isMirror, isHMirror, isVMirror);
            Log.i(TAG, "onCameraOpened: " + drawHelper.toString());
            Log.i(TAG, "CameraDisplayOrientation: " + drawHelper.getCameraDisplayOrientation());
        }

        @Override
        public void onCameraClosed() {
            Log.i(TAG, "onCameraClosed: ");
        }

        @Override
        public void onCameraError(Exception e) {
            Log.i(TAG, "onCameraError: " + e.getMessage());
        }

        @Override
        public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
            if (drawHelper != null) {
                drawHelper.setCameraDisplayOrientation(displayOrientation);
            }
            Log.i(TAG, "onCameraConfigurationChanged: " + cameraID + "  " + displayOrientation);
        }
    };

    private byte[] irData;
    private CameraListener irCameraListener = new CameraListener() {
        @Override
        public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
            Log.e(TAG, "onCameraOpened: IR摄像头开启成功");
            previewSizeIr = camera.getParameters().getPreviewSize();
        }

        @Override
        public void onPreview(byte[] data, Camera camera) {
            irData = data;
        }

        @Override
        public void onCameraClosed() {

        }

        @Override
        public void onCameraError(Exception e) {

        }

        @Override
        public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {

        }
    };

    private void clearLivenessMap(FaceInfo faceInfo){
        if(faceInfo == null && livenessMap.size() > 0){
            livenessMap.clear();
            return;
        }

        Iterator<Integer> iterator = livenessMap.keySet().iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            if(next.intValue() != faceInfo.getFaceId()){
                iterator.remove();
            }
        }
    }

    public void requestFaceLiveness(byte[] rgbData,byte[] irData,FaceInfo faceInfo,int width, int height,boolean isIR) {
        if (flEngine != null && flThreadQueue.remainingCapacity() > 0) {
            flExecutor.execute(new FaceLivenessDetectRunnable(rgbData, irData, faceInfo, width, height,isIR));
        }
    }

    private ConcurrentHashMap<Integer, Integer> livenessMap = new ConcurrentHashMap<>();
    class FaceLivenessDetectRunnable implements Runnable{
        private byte[] rgbData;
        private byte[] irData;
        private FaceInfo faceInfo;
        private int width;
        private int height;
        private boolean isIR;

        public FaceLivenessDetectRunnable(byte[] rgbData,byte[] irData,FaceInfo faceInfo,int width,int height,boolean isIR) {
            this.rgbData = rgbData;
            this.irData = irData;
            this.faceInfo = faceInfo;
            this.width = width;
            this.height = height;
            this.isIR = isIR;
        }

        @Override
        public void run() {
            int flCode;
            if(isIR){
                flCode = flEngine.processIr(irData,width, height, FaceEngine.CP_PAF_NV21, Arrays.asList(faceInfo), FaceEngine.ASF_IR_LIVENESS);
            } else {
                flCode = flEngine.process(rgbData,width, height, FaceEngine.CP_PAF_NV21, Arrays.asList(faceInfo), FaceEngine.ASF_LIVENESS);
            }

            List<LivenessInfo> livenessInfoList = new ArrayList<>();
            if(flCode == ErrorInfo.MOK){
                if(isIR){
                    flCode = flEngine.getIrLiveness(livenessInfoList);
                } else {
                    flCode = flEngine.getLiveness(livenessInfoList);
                }
            }
            if (flCode == ErrorInfo.MOK && livenessInfoList.size() > 0) {
                int liveness = livenessInfoList.get(0).getLiveness();
                int faceId = faceInfo.getFaceId();
                livenessMap.put(faceId,liveness);
            } else {
                Log.e(TAG, "onPreview: 提取活体结果失败：" + flCode);
            }
        }
    }

    /***
     * 提取卡片特征
     * @param bmp
     * @return
     */
    public synchronized FaceFeature inputIdCard(Bitmap bmp) {
        if (bmp == null) {
            return null;
        }
        //裁剪图片为合适的尺寸
        Bitmap bitmap = ArcSoftImageUtil.getAlignedBitmap(bmp, true);
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.e(TAG, "inputIdCard: 裁剪后：" + width + "---" + height);

        //创建等同于bitmap大小的byte[]
        byte[] bgr24 = ArcSoftImageUtil.createImageData(width, height, ArcSoftImageFormat.BGR24);
        int translateResult = ArcSoftImageUtil.bitmapToImageData(bitmap, bgr24, ArcSoftImageFormat.BGR24);
        if (translateResult != ArcSoftImageUtilError.CODE_SUCCESS) {
            return null;
        }
        List<FaceInfo> faceInfoList = new ArrayList<>();
        int detectResult = imgDetectEngine.detectFaces(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList);
        if (detectResult != ErrorInfo.MOK && faceInfoList.size() > 0) {
            return null;
        }

        FaceFeature faceFeature = new FaceFeature();
        if(faceInfoList.size() <= 0){
            return null;
        }
        int i = frEngine.extractFaceFeature(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList.get(0), faceFeature);
        if (i != ErrorInfo.MOK) {
            return null;
        }

        return faceFeature;
    }

    /***
     * 提取人脸特征
     * @return
     */
    public FaceFeature getFaceFeature() {
        if(faceInfoList != null && faceInfoList.size() > 0){
            synchronized (this){
                if(faceInfoList != null && faceInfoList.size() > 0){
                    try{
                        FaceInfo faceInfo = faceInfoList.get(0);
                        //提取特征
                        FaceFeature faceFeature = new FaceFeature();
                        frEngine.extractFaceFeature(rgbData, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfo, faceFeature);
                        return faceFeature;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 对比特征
     * @param faceFeature
     * @param faceFeature1
     * @return
     */
    public FaceSimilar compare(FaceFeature faceFeature, FaceFeature faceFeature1) {
        FaceSimilar faceSimilar = new FaceSimilar();
        compareEngine.compareFaceFeature(faceFeature, faceFeature1, faceSimilar);
        return faceSimilar;
    }

    /**
     * 提取人脸图像
     * @param faceInfo
     * @return
     */
    public Bitmap getFaceBitmap(FaceInfo faceInfo) {
        if (faceInfo != null && rgbData != null) {
            Bitmap bitmap = NV21ToBitmap.nv21ToBitmap2(rgbData.clone(), rgbCameraHelper.getWidth(), rgbCameraHelper.getHeight());
            Rect bestRect = getBestRect(rgbCameraHelper.getWidth(), rgbCameraHelper.getHeight(), faceInfo.getRect());
            int width = bestRect.width();
            int height = bestRect.height();
            bitmap = Bitmap.createBitmap(bitmap, bestRect.left, bestRect.top, width, height);
            int angle = CacheUtil.instance().getInt(Constants.Key.RGB_CAMERA_ANGLE,Constants.Default.RGB_CAMERA_ANGLE);
            int pictureRotation = CacheUtil.instance().getInt(Constants.Key.PICTURE_ANGLE, Constants.Default.PICTURE_ANGLE);
            if(pictureRotation != -1 ){//不为跟随相机
                return ImageUtils.rotateBitmap(bitmap, pictureRotation);
            } else if(angle != 0){
                return ImageUtils.rotateBitmap(bitmap, angle);
            } else {
                return bitmap;
            }
        }
        return getCurrCameraFrame();
    }

    /***
     * 截全屏
     * @return
     */
    public Bitmap getCurrCameraFrame() {
        if (rgbData != null) {
            try {
                Bitmap bitmap = NV21ToBitmap.nv21ToBitmap2(rgbData.clone(), rgbCameraHelper.getWidth(), rgbCameraHelper.getHeight());
                int angle = CacheUtil.instance().getInt(Constants.Key.RGB_CAMERA_ANGLE, Constants.Default.RGB_CAMERA_ANGLE);
                int pictureRotation = CacheUtil.instance().getInt(Constants.Key.PICTURE_ANGLE, Constants.Default.PICTURE_ANGLE);
                if(pictureRotation != -1 ){//不为跟随相机
                    return ImageUtils.rotateBitmap(bitmap, pictureRotation);
                } else if(angle != 0){
                    return ImageUtils.rotateBitmap(bitmap, angle);
                } else {
                    return bitmap;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 将图像中需要截取的Rect向外扩张一倍，若扩张一倍会溢出，则扩张到边界，若Rect已溢出，则收缩到边界
     *
     * @param width   图像宽度
     * @param height  图像高度
     * @param srcRect 原Rect
     * @return 调整后的Rect
     */
    public static Rect getBestRect(int width, int height, Rect srcRect) {
        if (srcRect == null) {
            return null;
        }
        Rect rect = new Rect(srcRect);

        // 原rect边界已溢出宽高的情况
        int maxOverFlow = Math.max(-rect.left, Math.max(-rect.top, Math.max(rect.right - width, rect.bottom - height)));
        if (maxOverFlow >= 0) {
            rect.inset(maxOverFlow, maxOverFlow);
            return rect;
        }

        // 原rect边界未溢出宽高的情况
        int padding = rect.height() / 2;

        // 若以此padding扩张rect会溢出，取最大padding为四个边距的最小值
        if (!(rect.left - padding > 0 && rect.right + padding < width && rect.top - padding > 0 && rect.bottom + padding < height)) {
            padding = Math.min(Math.min(Math.min(rect.left, width - rect.right), height - rect.bottom), rect.top);
        }
        rect.inset(-padding, -padding);
        return rect;
    }

    public void resume() {
        if (rgbCameraHelper != null && isRGBInitialized) {
            Log.e(TAG, "resume: helper不为null，并且已经初始化");
            rgbCameraHelper.start();
        } else {
            Log.e(TAG, "resume: 还未初始化");
        }

        if(irCameraHelper != null && isIRInitialized){
            Log.e(TAG, "resume: IR_helper不为null，并且已经初始化");
            irCameraHelper.start();
        } else {
            Log.e(TAG, "resume: IR_helper还未初始化");
        }
    }

    public void pause() {
        if (rgbCameraHelper != null && isRGBInitialized) {
            rgbCameraHelper.stop();
        }

        if(irCameraHelper != null && isIRInitialized){
            irCameraHelper.stop();
        }
    }

    public void destory() {
        unInitEngine();
        if (rgbCameraHelper != null) {
            rgbCameraHelper.release();
        }
        if(irCameraHelper != null){
            irCameraHelper.release();
        }
    }

    /**
     * 销毁引擎，faceHelper中可能会有特征提取耗时操作仍在执行，加锁防止crash
     */
    private void unInitEngine() {
        if(videoDetectEngine != null) videoDetectEngine.unInit();

        if(imgDetectEngine != null) imgDetectEngine.unInit();

        if(frEngine != null) frEngine.unInit();

        if(compareEngine != null) compareEngine.unInit();

        if(flEngine != null) flEngine.unInit();
    }

    public interface FaceCallback {
        void onReady();

        void onFaceDetection(boolean hasFace, FaceInfo faceInfo, int liveness);
    }
}
