package com.example.speechrecognizer.commnon.components;

import android.os.Handler;
import android.util.Log;

import com.example.speechrecognizer.commnon.listener.FftConvertListener;

import org.jtransforms.fft.FloatFFT_1D;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.Queue;
public class FftConverter {
    private final Handler handler = new Handler();
    // ビットレート
    int BIT_RATE = 16;
    // FFTのポイント数
    int FFT_SIZE = 256;
    // デシベルベースラインの設定
    double dB_baseline = Math.pow(2, 15) * FFT_SIZE * Math.sqrt(2);

    class VisualizerThread extends Thread {
        public VisualizerThread(float rmsdB){
            rmsBuffer.add(rmsdB);
        }
        @Override
        public void run(){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    final int byteSyze = Float.BYTES;
                    if (rmsBuffer.size() * byteSyze >= FFT_SIZE) {
                        ByteBuffer buf = ByteBuffer.allocate(getBitRate());
                        for (int i = buf.position(); i < buf.capacity() / byteSyze; i++) {
                            buf.order(ByteOrder.LITTLE_ENDIAN).putFloat(rmsBuffer.poll());
                        }
                        buf.flip();
                        //FFT変換対象をコピー
                        float[] data = new float[FFT_SIZE];
                        for (int i = buf.position(); i < buf.capacity() / byteSyze; i++) {
                            data[i] = buf.getFloat();
                        }

                        //FFTクラスの作成と値の引き渡し
                        FloatFFT_1D fft = new FloatFFT_1D(FFT_SIZE);
                        fft.realForward(data);

                        ByteBuffer ret = ByteBuffer.allocate(data.length * byteSyze);
                        for (float val : data){
                            ret.putFloat(val);
                        }
                        logger(ret);
                        fftConvertListener.onCompleted(ret);
                    }
                }
            });
        }
    }
    private FftConvertListener fftConvertListener = null;
    private Queue<Float> rmsBuffer = null;
    public FftConverter(FftConvertListener listener){
        rmsBuffer = new ArrayDeque<Float>();
        fftConvertListener = listener;
    }
    public void enQueue(float rmsdB){
        new VisualizerThread(rmsdB).start();
    }
    public int getBitRate(){
        return BIT_RATE;
    }
    public void logger(ByteBuffer data){
         // デシベルの計算
        float[] dbfs = new float[FFT_SIZE / 2];
        float max_db = -120f;
        int max_i = 0;
        for (int i = 0; i < FFT_SIZE; i += 2) {
            dbfs[i / 2] = (int) (20 * Math.log10(Math.sqrt(Math
                    .pow(data.getFloat(i), 2)
                    + Math.pow(data.getFloat(i + 1), 2)) / dB_baseline));
            if (max_db < dbfs[i / 2]) {
                max_db = dbfs[i / 2];
                max_i = i / 2;
            }
        }

        //音量が最大の周波数と，その音量を表示
        double resolution = ((fftConvertListener.getSamplingRate() / (double) FFT_SIZE));
        Log.d("fft", "周波数：" + resolution * max_i + " [Hz] 音量：" + max_db + " [dB]");
    }
}
