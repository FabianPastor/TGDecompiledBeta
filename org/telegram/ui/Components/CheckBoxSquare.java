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
import android.graphics.RectF;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;

public class CheckBoxSquare extends View {
    private static Paint backgroundPaint = null;
    private static Paint checkPaint = null;
    private static Paint eraser = null;
    private static final float progressBounceDiff = 0.2f;
    private static RectF rectF;
    private boolean attachedToWindow;
    private ObjectAnimator checkAnimator;
    private int color = -12345121;
    private Bitmap drawBitmap;
    private Canvas drawCanvas;
    private boolean isChecked;
    private boolean isDisabled;
    private float progress;

    public CheckBoxSquare(Context context) {
        super(context);
        if (checkPaint == null) {
            checkPaint = new Paint(1);
            checkPaint.setColor(-1);
            checkPaint.setStyle(Style.STROKE);
            checkPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            eraser = new Paint(1);
            eraser.setColor(0);
            eraser.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            backgroundPaint = new Paint(1);
            rectF = new RectF();
        }
        this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), Config.ARGB_4444);
        this.drawCanvas = new Canvas(this.drawBitmap);
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == 0 && this.drawBitmap != null) {
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

    public void setColor(int value) {
        this.color = value;
    }

    private void cancelCheckAnimator() {
        if (this.checkAnimator != null) {
            this.checkAnimator.cancel();
        }
    }

    private void animateToCheckedState(boolean newCheckedState) {
        String str = "progress";
        float[] fArr = new float[1];
        fArr[0] = newCheckedState ? 1.0f : 0.0f;
        this.checkAnimator = ObjectAnimator.ofFloat(this, str, fArr);
        this.checkAnimator.setDuration(300);
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

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setChecked(boolean checked, boolean animated) {
        if (checked != this.isChecked) {
            this.isChecked = checked;
            if (this.attachedToWindow && animated) {
                animateToCheckedState(checked);
                return;
            }
            cancelCheckAnimator();
            setProgress(checked ? 1.0f : 0.0f);
        }
    }

    public void setDisabled(boolean disabled) {
        this.isDisabled = disabled;
        invalidate();
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    protected void onDraw(Canvas canvas) {
        if (getVisibility() == 0) {
            float checkProgress;
            float bounceProgress;
            if (this.progress <= 0.5f) {
                checkProgress = this.progress / 0.5f;
                bounceProgress = checkProgress;
                backgroundPaint.setColor(Color.rgb(((int) (((float) (Color.red(this.color) - 115)) * checkProgress)) + 115, ((int) (((float) (Color.green(this.color) - 115)) * checkProgress)) + 115, ((int) (((float) (Color.blue(this.color) - 115)) * checkProgress)) + 115));
            } else {
                bounceProgress = 2.0f - (this.progress / 0.5f);
                checkProgress = 1.0f;
                backgroundPaint.setColor(this.color);
            }
            if (this.isDisabled) {
                backgroundPaint.setColor(-5197648);
            }
            float bounce = ((float) AndroidUtilities.dp(1.0f)) * bounceProgress;
            rectF.set(bounce, bounce, ((float) AndroidUtilities.dp(18.0f)) - bounce, ((float) AndroidUtilities.dp(18.0f)) - bounce);
            this.drawBitmap.eraseColor(0);
            this.drawCanvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), backgroundPaint);
            if (checkProgress != 1.0f) {
                float rad = Math.min((float) AndroidUtilities.dp(7.0f), (((float) AndroidUtilities.dp(7.0f)) * checkProgress) + bounce);
                rectF.set(((float) AndroidUtilities.dp(2.0f)) + rad, ((float) AndroidUtilities.dp(2.0f)) + rad, ((float) AndroidUtilities.dp(16.0f)) - rad, ((float) AndroidUtilities.dp(16.0f)) - rad);
                this.drawCanvas.drawRect(rectF, eraser);
            }
            if (this.progress > 0.5f) {
                this.drawCanvas.drawLine((float) AndroidUtilities.dp(7.5f), (float) ((int) AndroidUtilities.dpf2(13.5f)), (float) ((int) (((float) AndroidUtilities.dp(7.5f)) - (((float) AndroidUtilities.dp(5.0f)) * (1.0f - bounceProgress)))), (float) ((int) (AndroidUtilities.dpf2(13.5f) - (((float) AndroidUtilities.dp(5.0f)) * (1.0f - bounceProgress)))), checkPaint);
                this.drawCanvas.drawLine((float) ((int) AndroidUtilities.dpf2(6.5f)), (float) ((int) AndroidUtilities.dpf2(13.5f)), (float) ((int) (AndroidUtilities.dpf2(6.5f) + (((float) AndroidUtilities.dp(9.0f)) * (1.0f - bounceProgress)))), (float) ((int) (AndroidUtilities.dpf2(13.5f) - (((float) AndroidUtilities.dp(9.0f)) * (1.0f - bounceProgress)))), checkPaint);
            }
            canvas.drawBitmap(this.drawBitmap, 0.0f, 0.0f, null);
        }
    }
}
