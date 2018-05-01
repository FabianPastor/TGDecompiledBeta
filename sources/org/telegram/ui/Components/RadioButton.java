package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Keep;
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
            paint = new Paint(1);
            paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            paint.setStyle(Style.STROKE);
            checkedPaint = new Paint(1);
            eraser = new Paint(1);
            eraser.setColor(0);
            eraser.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        }
        try {
            this.bitmap = Bitmap.createBitmap(AndroidUtilities.dp((float) this.size), AndroidUtilities.dp((float) this.size), Config.ARGB_4444);
            this.bitmapCanvas = new Canvas(this.bitmap);
        } catch (Throwable th) {
            FileLog.m3e(th);
        }
    }

    @Keep
    public void setProgress(float f) {
        if (this.progress != f) {
            this.progress = f;
            invalidate();
        }
    }

    public float getProgress() {
        return this.progress;
    }

    public void setSize(int i) {
        if (this.size != i) {
            this.size = i;
        }
    }

    public void setColor(int i, int i2) {
        this.color = i;
        this.checkedColor = i2;
        invalidate();
    }

    public void setBackgroundColor(int i) {
        this.color = i;
        invalidate();
    }

    public void setCheckedColor(int i) {
        this.checkedColor = i;
        invalidate();
    }

    private void cancelCheckAnimator() {
        if (this.checkAnimator != null) {
            this.checkAnimator.cancel();
        }
    }

    private void animateToCheckedState(boolean z) {
        String str = "progress";
        float[] fArr = new float[1];
        fArr[0] = z ? true : false;
        this.checkAnimator = ObjectAnimator.ofFloat(this, str, fArr);
        this.checkAnimator.setDuration(200);
        this.checkAnimator.start();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
    }

    public void setChecked(boolean z, boolean z2) {
        if (z != this.isChecked) {
            this.isChecked = z;
            if (this.attachedToWindow && z2) {
                animateToCheckedState(z);
            } else {
                cancelCheckAnimator();
                setProgress(z ? true : false);
            }
        }
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    protected void onDraw(Canvas canvas) {
        float f;
        if (this.bitmap == null || this.bitmap.getWidth() != getMeasuredWidth()) {
            if (this.bitmap != null) {
                this.bitmap.recycle();
                this.bitmap = null;
            }
            try {
                this.bitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Config.ARGB_8888);
                this.bitmapCanvas = new Canvas(this.bitmap);
            } catch (Throwable th) {
                FileLog.m3e(th);
            }
        }
        if (this.progress <= 0.5f) {
            paint.setColor(this.color);
            checkedPaint.setColor(this.color);
            f = this.progress / 0.5f;
        } else {
            f = 2.0f - (this.progress / 0.5f);
            int red = Color.red(this.color);
            float f2 = 1.0f - f;
            int red2 = (int) (((float) (Color.red(this.checkedColor) - red)) * f2);
            int green = Color.green(this.color);
            int green2 = (int) (((float) (Color.green(this.checkedColor) - green)) * f2);
            int blue = Color.blue(this.color);
            red = Color.rgb(red + red2, green + green2, blue + ((int) (((float) (Color.blue(this.checkedColor) - blue)) * f2)));
            paint.setColor(red);
            checkedPaint.setColor(red);
        }
        if (this.bitmap != null) {
            this.bitmap.eraseColor(0);
            float f3 = ((float) (this.size / 2)) - ((1.0f + f) * AndroidUtilities.density);
            this.bitmapCanvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), f3, paint);
            if (this.progress <= 0.5f) {
                this.bitmapCanvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), f3 - ((float) AndroidUtilities.dp(1.0f)), checkedPaint);
                this.bitmapCanvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (f3 - ((float) AndroidUtilities.dp(1.0f))) * (1.0f - f), eraser);
            } else {
                this.bitmapCanvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), ((float) (this.size / 4)) + (((f3 - ((float) AndroidUtilities.dp(1.0f))) - ((float) (this.size / 4))) * f), checkedPaint);
            }
            canvas.drawBitmap(this.bitmap, 0.0f, 0.0f, null);
        }
    }
}
