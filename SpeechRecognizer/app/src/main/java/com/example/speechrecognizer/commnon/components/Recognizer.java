package com.example.speechrecognizer.commnon.components;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import com.example.speechrecognizer.commnon.MessageBrowser;
import com.example.speechrecognizer.commnon.Util;
import com.example.speechrecognizer.commnon.listener.FftConvertListener;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Locale;

public class Recognizer implements RecognitionListener, FftConvertListener {
    private final String TAG = Util.getClassName() ;

    //音量表示
    public interface OnLevelChangeListener {
        void onLevelChanged(float rmsdB);
    }
    private OnLevelChangeListener _levelChangeListener = null;
    public void onLevelChangeListener(OnLevelChangeListener levelChangeListener){
        _levelChangeListener = levelChangeListener;
    }

    //FTT表示
    public interface OnDataCaptureListener {
        void onFftDataCapture(byte[] fft);
    }
    private OnDataCaptureListener _dataCaptureListener = null;
    public void onDataCaptureListener(OnDataCaptureListener dataCaptureListener){
        _dataCaptureListener = dataCaptureListener;
    }

    //結果表示
    public interface OnResultsListener {
        void onResults(String results);
    }
    private OnResultsListener _resultsListener = null;
    public void OnResultsListener(OnResultsListener resultsListener){
        _resultsListener = resultsListener;
    }

    // サンプリングレート
    int SAMPLING_RATE = 44100;

    private Activity _activity = null;
    private MessageBrowser messageBrowser = null;
    private FftConverter fftConverter = null;

    private SpeechRecognizer speechRecognizer = null;
    private Intent recognizerIntent = null;

    private boolean started = false;
    public Recognizer(Activity activity){
        _activity = activity;
        messageBrowser = new MessageBrowser(activity);
        fftConverter = new FftConverter(this);
    }

    private void setupSpeechRecognizer(){
        Log.d(TAG, Util.getMethodName());
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(_activity);
            speechRecognizer.setRecognitionListener(this);
        }
    }

    private void setupRecognizerIntent(){
        Log.d(TAG, Util.getMethodName());
        if (recognizerIntent == null) {
            recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().getLanguage());
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, _activity.getPackageName());
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false);
        }
    }

    private void restartListeningService() {
        Log.d(TAG, Util.getMethodName());
        stopListening();
        startListening();
    }

    private void destroySpeechRecognizer(){
        Log.d(TAG, Util.getMethodName());
        if (speechRecognizer != null) speechRecognizer.destroy();
        speechRecognizer = null;
        recognizerIntent = null;
        started = false;
    }

    private String getResultsRecognition(Bundle results){
        Log.d(TAG, Util.getMethodName());
        ArrayList<String> valuse = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        StringBuilder stringBuilder = new StringBuilder();
        for (String val : valuse){
            Log.d(TAG,"value [" + val + "]"  );
            stringBuilder.append(val);
        }
        return stringBuilder.toString();
    }

    public boolean isStarted(){
        return started;
    }

    public void startListening(){
        Log.d(TAG, Util.getMethodName());
        try {
            setupSpeechRecognizer();
            setupRecognizerIntent();
            speechRecognizer.startListening(recognizerIntent);
            started = true;
         }catch (Exception ex) {
            Toast.makeText(_activity.getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            _activity.finish();
        }
    }

    public void stopListening(){
        Log.d(TAG, Util.getMethodName());
        destroySpeechRecognizer();
        //speechRecognizer.stopListening();
        messageBrowser.resetPosition();
    }

    //region FftConvertListener
    @Override
    public int getSamplingRate(){
        return SAMPLING_RATE;
    }

    @Override
    public void onCompleted(ByteBuffer fftData) {
        if (_dataCaptureListener != null) {
            _dataCaptureListener.onFftDataCapture(fftData.array());
        }
    }
    //endregion

    //region RecognitionListener
    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG, Util.getMethodName());
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, Util.getMethodName());
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.d(TAG, Util.getMethodName() +"[" + rmsdB + "]");
        fftConverter.enQueue(rmsdB);
        if (_levelChangeListener != null) {
            _levelChangeListener.onLevelChanged(rmsdB);
        }
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d(TAG, Util.getMethodName());
        final ByteBuffer byteBuffer = ByteBuffer.allocate(buffer.length);
        byteBuffer.put(buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.d(TAG, Util.getMethodName());
    }

    @Override
    public void onError(int error) {
        Log.d(TAG, Util.getMethodName() + "[" + error + "]");
        String reason = "";
        switch (error) {
            // Audio recording error
            case SpeechRecognizer.ERROR_AUDIO:
                reason = "ERROR_AUDIO";
                break;
            // Other client side errors
            case SpeechRecognizer.ERROR_CLIENT:
                reason = "ERROR_CLIENT";
                break;
            // Insufficient permissions
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                reason = "ERROR_INSUFFICIENT_PERMISSIONS";
                break;
            // 	Other network related errors
            case SpeechRecognizer.ERROR_NETWORK:
                reason = "ERROR_NETWORK";
                /* ネットワーク接続をチェックする処理をここに入れる */
                break;
            // Network operation timed out
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                reason = "ERROR_NETWORK_TIMEOUT";
                break;
            // No recognition result matched
            case SpeechRecognizer.ERROR_NO_MATCH:
                reason = "ERROR_NO_MATCH";
                break;
            // RecognitionService busy
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                reason = "ERROR_RECOGNIZER_BUSY";
                break;
            // Server sends error status
            case SpeechRecognizer.ERROR_SERVER:
                reason = "ERROR_SERVER";
                /* ネットワーク接続をチェックをする処理をここに入れる */
                break;
            // No speech input
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                reason = "ERROR_SPEECH_TIMEOUT";
                break;
            default:

        }
        switch (error) {
            case SpeechRecognizer.ERROR_NO_MATCH:
                break;
            default:
                messageBrowser.show(reason);
                messageBrowser.resetPosition();
                break;
        }
        restartListeningService();
    }

    @Override
    public void onResults(Bundle results) {
        Log.d(TAG, Util.getMethodName());
        String resultsRecognition = getResultsRecognition(results);
        if (!resultsRecognition.isEmpty()) {
            if (_resultsListener != null) {
                _resultsListener.onResults(resultsRecognition);
            }
        }
        restartListeningService();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.d(TAG, Util.getMethodName());
        String resultsRecognition = getResultsRecognition(partialResults);
        if (!resultsRecognition.isEmpty()) {
            messageBrowser.show(resultsRecognition);
        }
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d(TAG, Util.getMethodName());
    }
    //endregion
};
