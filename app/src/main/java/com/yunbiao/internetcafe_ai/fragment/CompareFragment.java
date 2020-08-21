package com.yunbiao.internetcafe_ai.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.LivenessInfo;
import com.elvishew.xlog.XLog;
import com.yunbiao.internetcafe_ai.R;
import com.yunbiao.internetcafe_ai.cache.CacheUtil;
import com.yunbiao.internetcafe_ai.common.Constants;
import com.yunbiao.internetcafe_ai.face.Verifier;
import com.yunbiao.internetcafe_ai.face.CertificatesView;
import com.yunbiao.internetcafe_ai.usb.IDCardReader;
import com.yunbiao.internetcafe_ai.usb.IdCardMsg;
import com.yunbiao.internetcafe_ai.utils.DialogUtil;
import com.yunbiao.internetcafe_ai.utils.SpeechUtil;

import java.util.HashMap;


public class CompareFragment extends BaseNetFragment {

    private CertificatesView faceView;
    private ImageView ivFace;
    private ImageView ivIdcard;
    private TextView tvSimilar;
    private TextView tvTips;
    private ImageView ivCompareResult;

    public static CompareFragment newInstance(Bundle bundle) {
        CompareFragment compareFragment = new CompareFragment();
        compareFragment.setArguments(bundle);
        return compareFragment;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_compare;
    }

    @Override
    protected void initView() {
        faceView = fv(R.id.face_view);
        ivFace = fv(R.id.iv_face_face);
        ivIdcard = fv(R.id.iv_idcard_img_face);
        tvSimilar = fv(R.id.tv_similar_face);
        tvTips = fv(R.id.tv_tips_face);
        ivCompareResult = fv(R.id.iv_compare_result_face);
    }

    @Override
    protected void initData() {
        String qrCode = null;
        Bundle arguments = getArguments();
        if(arguments != null){
            qrCode = arguments.getString(ScanFragment.JUMP_PARAM_KEY_QRCODE);
        }

        if(TextUtils.isEmpty(qrCode)){
            XLog.d("二维码为空，表示从首页进入");
        } else {
            XLog.d("二维码不为空，表示从预约跳转");
        }
        faceView.setCallback(faceCallback);

        IDCardReader.getInstance().startReaderThread(getActivity(), readListener);
    }

    private boolean mHasFace = false;
    private int mLiveness = -1;
    private CertificatesView.FaceCallback faceCallback = new CertificatesView.FaceCallback() {
        @Override
        public void onReady() {

        }

        @Override
        public void onFaceDetection(boolean hasFace, FaceInfo faceInfo, int liveness) {
            mHasFace = hasFace;
            mLiveness = liveness;
            if (mHasFace) {
                sendUpdateFaceImageMessage(faceInfo);
            }
        }
    };

    private IdCardMsg mIdCardMsg;
    private IDCardReader.ReadListener readListener = new IDCardReader.ReadListener() {
        @Override
        public void getCardInfo(IdCardMsg msg) {
            XLog.d("刷卡成功：" + msg.toString());
            SpeechUtil.getInstance().playOkRing();
            sendClearUIMessage(0);
            if(msg == null || TextUtils.isEmpty(msg.name) || TextUtils.isEmpty(msg.id_num)){
                sendTipMessage("证件读取失败，请重新贴卡", true,true);
                return;
            }

            byte[] bytes = IDCardReader.getInstance().decodeToBitmap(msg.ptoto);
            msg.image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            mIdCardMsg = msg;

            sendIdCardInfoMessage(mIdCardMsg);

            Verifier.startCompare(verifyCallback);
        }
    };

    private Verifier.VerifyCallback verifyCallback = new Verifier.VerifyCallback() {
        @Override
        public void onStart() {
            sendTipMessage("正在验证，请正视摄像头",true,false);
        }

        @Override
        public boolean hasFace() {
            return mHasFace;
        }

        @Override
        public int livness() {
            return livenessEnabled ? mLiveness : LivenessInfo.ALIVE;
        }

        @Override
        public float getTemperature() {
            return -1f;
        }

        @Override
        public FaceFeature getIdCardFeature() {
            if(mIdCardMsg == null){
                return null;
            }
            return faceView.inputIdCard(mIdCardMsg.image);
        }

        @Override
        public FaceFeature getFaceFeature() {
            return faceView.getFaceFeature();
        }

        @Override
        public FaceSimilar compare(FaceFeature idCardFeature, FaceFeature faceFeature) {
            return faceView.compare(idCardFeature,faceFeature);
        }

        @Override
        public void result(Verifier.CompareResult compareResult) {
            switch (compareResult.getResultCode()) {
                case -1:
                    sendTipMessage("人脸检测超时，请重新贴卡", true,true);
                    break;
                case -3:
                case -4:
                    sendTipMessage("验证失败 ，请重新贴卡", true,true);
                    break;
                case -5:
                    sendTipMessage("活体检测未通过，请重新贴卡",true,true);
                    break;
                case 1:
                case 0:
                    if(compareResult.getResultCode() == 1){
                        //请求后台校验
                        runDelay(new Runnable() {
                            @Override
                            public void run() {
                                requestTest(Constants.Url.REQUEST_CHECK_IDCARD,new HashMap<String, String>(),true);
                            }
                        },1000);
                    }
                    sendResultMessage(compareResult.getResultCode(),(int) (compareResult.getSimilar() * 100));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onError(String mUrl, Exception e) {
        DialogUtil.instance().showFailedTips(getActivity(),"校验失败","证件校验失败，请联系管理员");
    }

    @Override
    protected void onResponse(String mUrl, Object o) {
        onClickRight();
    }

    private void runDelay(Runnable runnable,long delay){
        mUIHandler.postDelayed(runnable,delay);
    }

    private void sendClearUIMessage(long delay){
        mUIHandler.removeMessages(0);
        mUIHandler.sendEmptyMessageDelayed(0,delay);
    }

    //显示人脸
    private long mUpdateFaceImageTime = 0;
    private void sendUpdateFaceImageMessage(FaceInfo faceInfo) {
        long currMillis = System.currentTimeMillis();
        if (mUpdateFaceImageTime == 0 || currMillis - mUpdateFaceImageTime > 1500) {
            mUpdateFaceImageTime = currMillis;
            Bitmap faceBitmap = faceView.getFaceBitmap(faceInfo);
            ivFace.setImageBitmap(faceBitmap);
        }
    }

    private void sendIdCardInfoMessage(IdCardMsg mIdCardMsg){
        Message obtain = Message.obtain();
        obtain.what = 2;
        obtain.obj = mIdCardMsg;
        mUIHandler.sendMessage(obtain);
    }

    private void sendTipMessage(String tip, boolean broad,boolean delayClear){
        Message obtain = Message.obtain();
        obtain.what = 3;
        Bundle bundle = new Bundle();
        bundle.putString("TIP",tip);
        bundle.putBoolean("BROAD",broad);
        bundle.putBoolean("DELAY_CLEAR",delayClear);
        obtain.setData(bundle);
        mUIHandler.sendMessage(obtain);
    }

    private void sendResultMessage(int resultCode, int similar){
        Message obtain = Message.obtain();
        obtain.what = 4;
        obtain.arg1 = resultCode;
        obtain.arg2 = similar;
        mUIHandler.sendMessage(obtain);
    }

    private Handler mUIHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://清除
                    tvTips.setText("");
                    ivIdcard.setImageBitmap(null);
                    tvSimilar.setVisibility(View.GONE);
                    tvSimilar.setText("");
                    ivCompareResult.setImageResource(R.mipmap.compare_icon);
                    break;
                case 1://刷新人脸

                    break;
                case 2://身份证
                    IdCardMsg idCardMsg = (IdCardMsg) msg.obj;
                    ivIdcard.setImageBitmap(idCardMsg.image);
                    break;
                case 3://提示
                    Bundle data = msg.getData();
                    String tip = data.getString("TIP");
                    boolean broad = data.getBoolean("BROAD");
                    boolean delayClear = data.getBoolean("DELAY_CLEAR");
                    tvTips.setText(tip);
                    if(broad){
                        SpeechUtil.getInstance().playNormal(tip);
                    }
                    if(delayClear){
                        sendClearUIMessage(3000);
                    }
                    break;
                case 4://对比结果
                    boolean isLike =  msg.arg1 == 1;
                    int similar = msg.arg2;

                    int imageId = isLike ? R.mipmap.compare_success : R.mipmap.compare_failed;
                    String resultText = isLike ? "认证通过，请收好证件！" : "人证不匹配，请重试";
                    String similarText = "相似度" + similar + "%";
                    ivCompareResult.setImageResource(imageId);
                    tvTips.setText(resultText);
                    tvSimilar.setText(similarText);
                    tvSimilar.setVisibility(View.VISIBLE);

                    SpeechUtil.getInstance().stopNormal();
                    SpeechUtil.getInstance().playNormal(resultText);
                    break;
            }
        }
    };

    @Override
    protected void onClickRight() {
        Log.e(TAG, "onClickRight: 点击了右边的按钮");
        jumpFragment(new CompareResultFragment());
    }

    private boolean livenessEnabled = true;
    @Override
    public void onResume() {
        super.onResume();
        faceView.resume();
        livenessEnabled = CacheUtil.instance().getBoolean(Constants.Key.LIVENESS_ENABLED,Constants.Default.LIVENESS_ENABLED);
    }

    @Override
    public void onPause() {
        super.onPause();
        faceView.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        faceView.destory();

        IDCardReader.getInstance().closeReaderThread();
    }
}
