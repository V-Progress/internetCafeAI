package com.yunbiao.internetcafe_ai.utils;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;

import com.yunbiao.internetcafe_ai.APP;
import com.yunbiao.internetcafe_ai.R;

import java.util.HashMap;
import java.util.Locale;

public class SpeechUtil {

    private static SpeechUtil speechUtil;
    private final SoundPool mSoundPool;
    private int mOkRing;
    private TextToSpeech textToSpeech;
    private boolean isTTSInited = false;
    private boolean isSpeechSupport = false;
    private float mSpeed = 2.0f;
    private static HashMap<String, String> textToSpeechMap = new HashMap<>();

    public static SpeechUtil getInstance(){
        if(speechUtil == null){
            synchronized (SpeechUtil.class){
                if(speechUtil == null){
                    speechUtil = new SpeechUtil();
                }
            }
        }
        return speechUtil;
    }

    public void init(){
        try{
            textToSpeech = new TextToSpeech(APP.getAppContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    isTTSInited = status == TextToSpeech.SUCCESS;
                    if(isTTSInited){
                        Locale locale = APP.getAppContext().getResources().getConfiguration().locale;
                        int support = textToSpeech.setLanguage(locale);
                        isSpeechSupport = !(support == TextToSpeech.LANG_NOT_SUPPORTED || support == TextToSpeech.LANG_MISSING_DATA);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void playNormal(final String message) {
        playNormalAddCallback(TextToSpeech.QUEUE_ADD, message, null);
    }

    public void playNormal(final String message, Runnable runnable) {
        playNormalAddCallback(TextToSpeech.QUEUE_FLUSH, message, runnable);
    }

    public void stopNormal() {
        if (!isTTSInited || !isSpeechSupport) {
            return;
        }
        if (!textToSpeech.isSpeaking()) {
            return;
        }
        textToSpeech.stop();
    }

    private String mMessage;
    private void playNormalAddCallback(int queueMode, final String message, final Runnable runnable) {
        if (!isTTSInited || !isSpeechSupport) {
            return;
        }

        if (TextUtils.isEmpty(message)) {
            return;
        }

        if (textToSpeech.isSpeaking() && TextUtils.equals(message, mMessage)) {
            return;
        } else {
            mMessage = message;
        }
        textToSpeechMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, message);
        textToSpeechMap.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
        textToSpeech.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
            @Override
            public void onUtteranceCompleted(String utteranceId) {
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
        textToSpeech.setSpeechRate(mSpeed);
        textToSpeech.speak(message, queueMode, textToSpeechMap);
    }

    private SpeechUtil(){
        AudioAttributes.Builder ab = new AudioAttributes.Builder();
        ab.setLegacyStreamType(AudioManager.STREAM_ALARM);
        ab.setUsage(AudioAttributes.USAGE_ALARM);
        mSoundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(ab.build()).build();

        mOkRing = mSoundPool.load(APP.getAppContext(), R.raw.ok_ring,1);
    }

    private void playRing(int id){
        if(mSoundPool == null || id <= 0)
            return;
        mSoundPool.play(id, 1, 1, 1, 0, 1);
    }

    public void playOkRing(){
        playRing(mOkRing);
    }
}
