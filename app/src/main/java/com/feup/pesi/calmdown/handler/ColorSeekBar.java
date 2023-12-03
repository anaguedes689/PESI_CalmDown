package com.feup.pesi.calmdown.handler;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class ColorSeekBar extends androidx.appcompat.widget.AppCompatSeekBar {

    private Paint colorBarPaint;
    private int colorBarValue = Color.BLACK; // Cor inicial
    private OnColorChangeListener onColorChangeListener;

    public interface OnColorChangeListener {
        void onColorChanged(int color);
    }

    public ColorSeekBar(Context context) {
        super(context);
        init();
    }

    public ColorSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        colorBarPaint = new Paint();
        colorBarPaint.setStyle(Paint.Style.FILL);
        colorBarPaint.setStrokeWidth(5);
        colorBarPaint.setColor(colorBarValue);
    }

    public void setColorBarValue(int color) {
        colorBarValue = color;
        colorBarPaint.setColor(colorBarValue);
        invalidate();

        if (onColorChangeListener != null) {
            onColorChangeListener.onColorChanged(colorBarValue);
        }
    }

    public int getColorBarValue() {
        return colorBarValue;
    }

    public void setOnColorChangeListener(OnColorChangeListener listener) {
        this.onColorChangeListener = listener;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight();

        float position = (float) getProgress() / getMax() * width;

        canvas.drawLine(paddingLeft + position, 0, paddingLeft + position, height, colorBarPaint);
    }
}
