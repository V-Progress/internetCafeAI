package com.yunbiao.internetcafe_ai.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbiao.internetcafe_ai.R;


public class FaceFragment extends BaseFragment {

    private FrameLayout faceView;
    private ImageView ivFace;
    private ImageView ivIdcard;
    private TextView tvSimilar;
    private TextView tvTips;
    private ImageView ivCompareResult;

    @Override
    protected int getLayout() {
        return R.layout.fragment_face;
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
        //1.初始化摄像头
        //2.初始化读身份证
        //3.等待读身份证
        //4.读完后再开始采集人脸
        //5.刷完后对比相似度
        //6.对比成功后放开跳转按钮
        //7.跳转时传入身份证号
        test();
    }

    private void test() {
        fv(R.id.btn_face_success).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFaceImage(BitmapFactory.decodeResource(getResources(), R.mipmap.test_face));
            }
        });
        fv(R.id.btn_read_success).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIdCardImage(BitmapFactory.decodeResource(getResources(), R.mipmap.test_read));
            }
        });
        fv(R.id.btn_compare_failed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compare(false);
            }
        });
        fv(R.id.btn_compare_success).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compare(true);
            }
        });
    }

    private void setIdCardImage(Bitmap bitmap) {
        ivIdcard.setImageBitmap(bitmap);
    }

    private void setFaceImage(Bitmap bitmap) {
        ivFace.setImageBitmap(bitmap);
    }

    private void compare(boolean result) {
        ivCompareResult.setImageResource(result ? R.mipmap.compare_success : R.mipmap.compare_failed);
        tvTips.setText(result ? "认证通过，请收好证件！" : "认证不匹配，请收好证件");
        tvSimilar.setText(result ? "相似度95%" : "相似度60%");
        tvSimilar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onClickRight() {
        Log.e(TAG, "onClickRight: 点击了右边的按钮");
    }
}
