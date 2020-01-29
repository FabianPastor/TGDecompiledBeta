package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.EditText;

public class EditTextOutline extends EditText {
    private Bitmap mCache;
    private final Canvas mCanvas = new Canvas();
    private final TextPaint mPaint = new TextPaint();
    private int mStrokeColor = 0;
    private float mStrokeWidth;
    private boolean mUpdateCachedBitmap = true;

    public EditTextOutline(Context context) {
        super(context);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    /* access modifiers changed from: protected */
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        super.onTextChanged(charSequence, i, i2, i3);
        this.mUpdateCachedBitmap = true;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i <= 0 || i2 <= 0) {
            this.mCache = null;
            return;
        }
        this.mUpdateCachedBitmap = true;
        this.mCache = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
    }

    public void setStrokeColor(int i) {
        this.mStrokeColor = i;
        this.mUpdateCachedBitmap = true;
        invalidate();
    }

    public void setStrokeWidth(float f) {
        this.mStrokeWidth = f;
        this.mUpdateCachedBitmap = true;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (!(this.mCache == null || this.mStrokeColor == 0)) {
            if (this.mUpdateCachedBitmap) {
                int measuredWidth = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
                int measuredHeight = getMeasuredHeight();
                String obj = getText().toString();
                this.mCanvas.setBitmap(this.mCache);
                this.mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                float f = this.mStrokeWidth;
                if (f <= 0.0f) {
                    f = (float) Math.ceil((double) (getTextSize() / 11.5f));
                }
                this.mPaint.setStrokeWidth(f);
                this.mPaint.setColor(this.mStrokeColor);
                this.mPaint.setTextSize(getTextSize());
                this.mPaint.setTypeface(getTypeface());
                this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                StaticLayout staticLayout = new StaticLayout(obj, this.mPaint, measuredWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, true);
                this.mCanvas.save();
                this.mCanvas.translate((float) getPaddingLeft(), (((float) (((measuredHeight - getPaddingTop()) - getPaddingBottom()) - staticLayout.getHeight())) / 2.0f) + ((float) getPaddingTop()));
                staticLayout.draw(this.mCanvas);
                this.mCanvas.restore();
                this.mUpdateCachedBitmap = false;
            }
            canvas.drawBitmap(this.mCache, 0.0f, 0.0f, this.mPaint);
        }
        super.onDraw(canvas);
    }
}
