package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class GroupCreateCheckBox extends View {
    private static Paint eraser = null;
    private static Paint eraser2 = null;
    private static final float progressBounceDiff = 0.2f;
    private boolean attachedToWindow;
    private Paint backgroundInnerPaint;
    private String backgroundKey = "checkboxCheck";
    private Paint backgroundPaint;
    private Canvas bitmapCanvas;
    private ObjectAnimator checkAnimator;
    private String checkKey = "checkboxCheck";
    private Paint checkPaint;
    private float checkScale = 1.0f;
    private Bitmap drawBitmap;
    private String innerKey = "checkbox";
    private int innerRadDiff;
    private boolean isCheckAnimation = true;
    private boolean isChecked;
    private float progress;

    public GroupCreateCheckBox(Context context) {
        super(context);
        if (eraser == null) {
            Paint paint = new Paint(1);
            eraser = paint;
            paint.setColor(0);
            eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            Paint paint2 = new Paint(1);
            eraser2 = paint2;
            paint2.setColor(0);
            eraser2.setStyle(Paint.Style.STROKE);
            eraser2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        this.backgroundPaint = new Paint(1);
        this.backgroundInnerPaint = new Paint(1);
        Paint paint3 = new Paint(1);
        this.checkPaint = paint3;
        paint3.setStyle(Paint.Style.STROKE);
        this.innerRadDiff = AndroidUtilities.dp(2.0f);
        this.checkPaint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
        eraser2.setStrokeWidth((float) AndroidUtilities.dp(28.0f));
        this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), Bitmap.Config.ARGB_4444);
        this.bitmapCanvas = new Canvas(this.drawBitmap);
        updateColors();
    }

    public void setColorKeysOverrides(String check, String inner, String back) {
        this.checkKey = check;
        this.innerKey = inner;
        this.backgroundKey = back;
        updateColors();
    }

    public void updateColors() {
        this.backgroundInnerPaint.setColor(Theme.getColor(this.innerKey));
        this.backgroundPaint.setColor(Theme.getColor(this.backgroundKey));
        this.checkPaint.setColor(Theme.getColor(this.checkKey));
        invalidate();
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

    public void setCheckScale(float value) {
        this.checkScale = value;
    }

    private void cancelCheckAnimator() {
        ObjectAnimator objectAnimator = this.checkAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
    }

    private void animateToCheckedState(boolean newCheckedState) {
        this.isCheckAnimation = newCheckedState;
        float[] fArr = new float[1];
        fArr[0] = newCheckedState ? 1.0f : 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "progress", fArr);
        this.checkAnimator = ofFloat;
        ofFloat.setDuration(300);
        this.checkAnimator.start();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateColors();
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

    public void setInnerRadDiff(int value) {
        this.innerRadDiff = value;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float radDiff;
        Canvas canvas2 = canvas;
        if (getVisibility() == 0 && this.progress != 0.0f) {
            int cx = getMeasuredWidth() / 2;
            int cy = getMeasuredHeight() / 2;
            eraser2.setStrokeWidth((float) AndroidUtilities.dp(30.0f));
            this.drawBitmap.eraseColor(0);
            float f = this.progress;
            float roundProgress = f >= 0.5f ? 1.0f : f / 0.5f;
            float checkProgress = f < 0.5f ? 0.0f : (f - 0.5f) / 0.5f;
            if (!this.isCheckAnimation) {
                f = 1.0f - f;
            }
            float roundProgressCheckState = f;
            if (roundProgressCheckState < 0.2f) {
                radDiff = (((float) AndroidUtilities.dp(2.0f)) * roundProgressCheckState) / 0.2f;
            } else if (roundProgressCheckState < 0.4f) {
                radDiff = ((float) AndroidUtilities.dp(2.0f)) - ((((float) AndroidUtilities.dp(2.0f)) * (roundProgressCheckState - 0.2f)) / 0.2f);
            } else {
                radDiff = 0.0f;
            }
            if (checkProgress != 0.0f) {
                canvas2.drawCircle((float) cx, (float) cy, (((float) (cx - AndroidUtilities.dp(2.0f))) + (((float) AndroidUtilities.dp(2.0f)) * checkProgress)) - radDiff, this.backgroundPaint);
            }
            float innerRad = ((float) (cx - this.innerRadDiff)) - radDiff;
            this.bitmapCanvas.drawCircle((float) cx, (float) cy, innerRad, this.backgroundInnerPaint);
            this.bitmapCanvas.drawCircle((float) cx, (float) cy, (1.0f - roundProgress) * innerRad, eraser);
            canvas2.drawBitmap(this.drawBitmap, 0.0f, 0.0f, (Paint) null);
            float checkSide = ((float) AndroidUtilities.dp(10.0f)) * checkProgress * this.checkScale;
            float smallCheckSide = ((float) AndroidUtilities.dp(5.0f)) * checkProgress * this.checkScale;
            int x = cx - AndroidUtilities.dp(1.0f);
            int y = cy + AndroidUtilities.dp(4.0f);
            float side = (float) Math.sqrt((double) ((smallCheckSide * smallCheckSide) / 2.0f));
            float f2 = side;
            int y2 = y;
            int x2 = x;
            canvas.drawLine((float) x, (float) y, ((float) x) - side, ((float) y) - side, this.checkPaint);
            float side2 = (float) Math.sqrt((double) ((checkSide * checkSide) / 2.0f));
            int x3 = x2 - AndroidUtilities.dp(1.2f);
            int i = x3;
            float f3 = side2;
            canvas.drawLine((float) x3, (float) y2, ((float) x3) + side2, ((float) y2) - side2, this.checkPaint);
        }
    }
}
