package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

public class RadioButton extends View {
    private static Paint checkedPaint;
    private static Paint eraser;
    private static Paint paint;
    private boolean attachedToWindow;
    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private ObjectAnimator checkAnimator;
    private int checkedColor;
    private int color;
    private boolean isChecked;
    private float progress;
    private int size = AndroidUtilities.dp(16.0f);

    public RadioButton(Context context) {
        super(context);
        if (paint == null) {
            Paint paint2 = new Paint(1);
            paint = paint2;
            paint2.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            paint.setStyle(Paint.Style.STROKE);
            checkedPaint = new Paint(1);
            Paint paint3 = new Paint(1);
            eraser = paint3;
            paint3.setColor(0);
            eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        try {
            this.bitmap = Bitmap.createBitmap(AndroidUtilities.dp((float) this.size), AndroidUtilities.dp((float) this.size), Bitmap.Config.ARGB_4444);
            this.bitmapCanvas = new Canvas(this.bitmap);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public void setProgress(float value) {
        if (this.progress != value) {
            this.progress = value;
            invalidate();
        }
    }

    public float getProgress() {
        return this.progress;
    }

    public void setSize(int value) {
        if (this.size != value) {
            this.size = value;
        }
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color1, int color2) {
        this.color = color1;
        this.checkedColor = color2;
        invalidate();
    }

    public void setBackgroundColor(int color1) {
        this.color = color1;
        invalidate();
    }

    public void setCheckedColor(int color2) {
        this.checkedColor = color2;
        invalidate();
    }

    private void cancelCheckAnimator() {
        ObjectAnimator objectAnimator = this.checkAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
    }

    private void animateToCheckedState(boolean newCheckedState) {
        float[] fArr = new float[1];
        fArr[0] = newCheckedState ? 1.0f : 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "progress", fArr);
        this.checkAnimator = ofFloat;
        ofFloat.setDuration(200);
        this.checkAnimator.start();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
    }

    public void setChecked(boolean checked, boolean animated) {
        if (checked != this.isChecked) {
            this.isChecked = checked;
            if (!this.attachedToWindow || !animated) {
                cancelCheckAnimator();
                setProgress(checked ? 1.0f : 0.0f);
                return;
            }
            animateToCheckedState(checked);
        }
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float circleProgress;
        Bitmap bitmap2 = this.bitmap;
        if (bitmap2 == null || bitmap2.getWidth() != getMeasuredWidth()) {
            Bitmap bitmap3 = this.bitmap;
            if (bitmap3 != null) {
                bitmap3.recycle();
                this.bitmap = null;
            }
            try {
                this.bitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                this.bitmapCanvas = new Canvas(this.bitmap);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        float f = this.progress;
        if (f <= 0.5f) {
            paint.setColor(this.color);
            checkedPaint.setColor(this.color);
            circleProgress = this.progress / 0.5f;
        } else {
            circleProgress = 2.0f - (f / 0.5f);
            int r1 = Color.red(this.color);
            int g1 = Color.green(this.color);
            int b1 = Color.blue(this.color);
            int c = Color.rgb(r1 + ((int) (((float) (Color.red(this.checkedColor) - r1)) * (1.0f - circleProgress))), g1 + ((int) (((float) (Color.green(this.checkedColor) - g1)) * (1.0f - circleProgress))), b1 + ((int) (((float) (Color.blue(this.checkedColor) - b1)) * (1.0f - circleProgress))));
            paint.setColor(c);
            checkedPaint.setColor(c);
        }
        Bitmap bitmap4 = this.bitmap;
        if (bitmap4 != null) {
            bitmap4.eraseColor(0);
            float rad = ((float) (this.size / 2)) - ((circleProgress + 1.0f) * AndroidUtilities.density);
            this.bitmapCanvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), rad, paint);
            if (this.progress <= 0.5f) {
                this.bitmapCanvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), rad - ((float) AndroidUtilities.dp(1.0f)), checkedPaint);
                this.bitmapCanvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (rad - ((float) AndroidUtilities.dp(1.0f))) * (1.0f - circleProgress), eraser);
            } else {
                this.bitmapCanvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), ((float) (this.size / 4)) + (((rad - ((float) AndroidUtilities.dp(1.0f))) - ((float) (this.size / 4))) * circleProgress), checkedPaint);
            }
            canvas.drawBitmap(this.bitmap, 0.0f, 0.0f, (Paint) null);
        }
    }
}
