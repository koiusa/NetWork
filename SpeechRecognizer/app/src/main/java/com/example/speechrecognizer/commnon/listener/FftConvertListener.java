package com.example.speechrecognizer.commnon.listener;

import java.nio.ByteBuffer;

public interface FftConvertListener {
    int getSamplingRate();
    void onCompleted(ByteBuffer fftData);
}
