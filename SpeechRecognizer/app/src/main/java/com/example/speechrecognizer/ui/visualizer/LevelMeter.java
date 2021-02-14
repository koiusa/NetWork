package com.example.speechrecognizer.ui.visualizer;

import android.content.Context;
import android.content.QuickViewConstants;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * TODO: document your custom view class.
 */
public class LevelMeter extends View {
    private Paint dataBrush = null;
    private LinearGradient dataShader = null;
    private Queue<Float> volume = null;
    private final int averageRange = 10;
    // ピーク値
    private static double PEAK_VALUE = (float) (256 * Math.sqrt(2));
    public LevelMeter(Context context) {
        super(context);
        init(null, 0);
    }

    public LevelMeter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public LevelMeter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        volume = new ArrayDeque<Float>();
    }

    public void update(float bytes) {
        volume.add(bytes);
        invalidate();
    }

    private int getSpectreStartColor(){
        return ContextCompat.getColor(getContext(), android.R.color.holo_orange_light);
    }
    private int getSpectreEndColor(){
        return ContextCompat.getColor(getContext(), android.R.color.white);
    }

    private float averageVolume()
    {
        float total = 0;
        for (int i = 0; i < averageRange; i++){
            total += volume.peek();
            if (volume.size() > averageRange){
                volume.poll();
            }
        }
        return total / averageRange;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;
        dataBrush = new Paint();
        dataBrush.setStrokeWidth(1f);
        dataBrush.setAntiAlias(true);
        dataShader = new LinearGradient(0, getBottom(), contentWidth, 0, getSpectreStartColor(), getSpectreEndColor(), Shader.TileMode.CLAMP);
        dataBrush.setShader(dataShader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        if (dataBrush != null) {
            if (volume.size() >= averageRange) {
                RectF bandRects = new RectF();
                bandRects.bottom = getBottom();
                bandRects.left = paddingLeft;
                bandRects.right = contentWidth / 32;
                float level = getBottom() - (float) (Math.log10(Math.abs(averageVolume())) * PEAK_VALUE);
                bandRects.top = level > 0 ? level : 0;
                canvas.drawRect(bandRects, dataBrush);
            }
        }
    }
}