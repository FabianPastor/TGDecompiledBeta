package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.view.View;
import androidx.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class CheckBoxSquare extends View {
    private static final float progressBounceDiff = 0.2f;
    private boolean attachedToWindow;
    private ObjectAnimator checkAnimator;
    private Bitmap drawBitmap;
    private Canvas drawCanvas;
    private boolean isAlert;
    private boolean isChecked;
    private boolean isDisabled;
    private float progress;
    private RectF rectF;

    public CheckBoxSquare(Context context, boolean z) {
        super(context);
        if (Theme.checkboxSquare_backgroundPaint == null) {
            Theme.createCommonResources(context);
        }
        this.rectF = new RectF();
        this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), Config.ARGB_4444);
        this.drawCanvas = new Canvas(this.drawBitmap);
        this.isAlert = z;
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

    private void cancelCheckAnimator() {
        ObjectAnimator objectAnimator = this.checkAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
    }

    private void animateToCheckedState(boolean z) {
        float[] fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.0f;
        this.checkAnimator = ObjectAnimator.ofFloat(this, "progress", fArr);
        this.checkAnimator.setDuration(300);
        this.checkAnimator.start();
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
    }

    public void setChecked(boolean z, boolean z2) {
        if (z != this.isChecked) {
            this.isChecked = z;
            if (this.attachedToWindow && z2) {
                animateToCheckedState(z);
            } else {
                cancelCheckAnimator();
                setProgress(z ? 1.0f : 0.0f);
            }
        }
    }

    public void setDisabled(boolean z) {
        this.isDisabled = z;
        invalidate();
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (getVisibility() == 0) {
            float f;
            int color = Theme.getColor(this.isAlert ? "dialogCheckboxSquareUnchecked" : "checkboxSquareUnchecked");
            int color2 = Theme.getColor(this.isAlert ? "dialogCheckboxSquareBackground" : "checkboxSquareBackground");
            float f2 = this.progress;
            if (f2 <= 0.5f) {
                f2 /= 0.5f;
                Theme.checkboxSquare_backgroundPaint.setColor(Color.rgb(Color.red(color) + ((int) (((float) (Color.red(color2) - Color.red(color))) * f2)), Color.green(color) + ((int) (((float) (Color.green(color2) - Color.green(color))) * f2)), Color.blue(color) + ((int) (((float) (Color.blue(color2) - Color.blue(color))) * f2))));
                f = f2;
            } else {
                f2 = 2.0f - (f2 / 0.5f);
                Theme.checkboxSquare_backgroundPaint.setColor(color2);
                f = f2;
                f2 = 1.0f;
            }
            if (this.isDisabled) {
                Theme.checkboxSquare_backgroundPaint.setColor(Theme.getColor(this.isAlert ? "dialogCheckboxSquareDisabled" : "checkboxSquareDisabled"));
            }
            float dp = ((float) AndroidUtilities.dp(1.0f)) * f;
            this.rectF.set(dp, dp, ((float) AndroidUtilities.dp(18.0f)) - dp, ((float) AndroidUtilities.dp(18.0f)) - dp);
            this.drawBitmap.eraseColor(0);
            this.drawCanvas.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), Theme.checkboxSquare_backgroundPaint);
            if (f2 != 1.0f) {
                dp = Math.min((float) AndroidUtilities.dp(7.0f), (((float) AndroidUtilities.dp(7.0f)) * f2) + dp);
                this.rectF.set(((float) AndroidUtilities.dp(2.0f)) + dp, ((float) AndroidUtilities.dp(2.0f)) + dp, ((float) AndroidUtilities.dp(16.0f)) - dp, ((float) AndroidUtilities.dp(16.0f)) - dp);
                this.drawCanvas.drawRect(this.rectF, Theme.checkboxSquare_eraserPaint);
            }
            if (this.progress > 0.5f) {
                Theme.checkboxSquare_checkPaint.setColor(Theme.getColor(this.isAlert ? "dialogCheckboxSquareCheck" : "checkboxSquareCheck"));
                float f3 = 1.0f - f;
                this.drawCanvas.drawLine((float) AndroidUtilities.dp(7.0f), (float) ((int) AndroidUtilities.dpf2(13.0f)), (float) ((int) (((float) AndroidUtilities.dp(7.0f)) - (((float) AndroidUtilities.dp(3.0f)) * f3))), (float) ((int) (AndroidUtilities.dpf2(13.0f) - (((float) AndroidUtilities.dp(3.0f)) * f3))), Theme.checkboxSquare_checkPaint);
                this.drawCanvas.drawLine((float) ((int) AndroidUtilities.dpf2(7.0f)), (float) ((int) AndroidUtilities.dpf2(13.0f)), (float) ((int) (AndroidUtilities.dpf2(7.0f) + (((float) AndroidUtilities.dp(7.0f)) * f3))), (float) ((int) (AndroidUtilities.dpf2(13.0f) - (((float) AndroidUtilities.dp(7.0f)) * f3))), Theme.checkboxSquare_checkPaint);
            }
            canvas.drawBitmap(this.drawBitmap, 0.0f, 0.0f, null);
        }
    }
}
