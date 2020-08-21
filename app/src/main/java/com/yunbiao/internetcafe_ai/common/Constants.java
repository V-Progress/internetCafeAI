package com.yunbiao.internetcafe_ai.common;

import com.yunbiao.internetcafe_ai.cache.SpUtils;

public class Constants {


    public interface Face{
        String APP_ID = "9LpHCqJKYWQYkZ8kKM1Ei6cJJwPELF9T3m9hRpBqQVWp";
        String SDK_KEY = "EantWDhjZVA9u8HUBroyLKttuDNAQQjfgNbeyqVy1PJy";
//        String APP_ID = "8njzCU8uk7SVoxjKw6M2yZQmCbbxQu9gqcM3QrWnB19J";
//        String SDK_KEY = "7aWWDFJQ5cU3z7b2UC25efUP18B4148ZSxWiA29awWyD";
    }

    public interface Key{
        String LIVENESS_ENABLED = "key_liveness_enabled";
        String IS_IR = "key_is_ir";
        String SIMILAR = "key_similar";
        String RGB_CAMERA_ANGLE = "key_rgb_camera_angle";
        String IR_CAMERA_ANGLE = "key_ir_camera_angle";
        String PICTURE_ANGLE = "key_picture_angle";
        String RECT_H_MIRROR = "key_rect_h_mirror";
        String RECT_V_MIRROR = "key_rect_v_mirror";
    }

    public static class Default{
        public static boolean LIVENESS_ENABLED = true;
        public static boolean IS_IR = true;
        public static int SIMILAR = 75;
        public static int RGB_CAMERA_ANGLE = 0;
        public static int IR_CAMERA_ANGLE = 180;
        public static int PICTURE_ANGLE = 0;
        public static boolean RECT_H_MIRROR = false;
        public static boolean RECT_V_MIRROR = false;
    }

    public static class Url{
        //预约查询
        public static final String QUERY_APPOINTMENT = "QUERY_APPOINTMENT";
        //请求订单
        public static final String REQUEST_ORDER = "REQUEST_ORDER";
        //请求付款码
        public static final String REQUEST_PAY_CODE = "REQUEST_PAY_CODE";
        //查询支付结果
        public static final String QUERY_PAY_RESULT = "QUERY_PAY_RESULT";
        //余额查询
        public static final String QUERY_BALANCE = "QUERY_BALANCE";
        //后台校验证件信息
        public static final String REQUEST_CHECK_IDCARD = "REQUEST_CHECK_IDCARD";
        //刷脸上机
        public static final String REQUEST_FACE_ON = "REQUEST_FACE_ON";
    }
}
