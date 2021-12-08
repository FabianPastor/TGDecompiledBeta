package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
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
    private String key1;
    private String key2;
    private String key3;
    private float progress;
    private RectF rectF;
    private final Theme.ResourcesProvider resourcesProvider;

    public CheckBoxSquare(Context context, boolean alert) {
        this(context, alert, (Theme.ResourcesProvider) null);
    }

    public CheckBoxSquare(Context context, boolean alert, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        if (Theme.checkboxSquare_backgroundPaint == null) {
            Theme.createCommonResources(context);
        }
        boolean z = this.isAlert;
        this.key1 = z ? "dialogCheckboxSquareUnchecked" : "checkboxSquareUnchecked";
        this.key2 = z ? "dialogCheckboxSquareBackground" : "checkboxSquareBackground";
        this.key3 = z ? "dialogCheckboxSquareCheck" : "checkboxSquareCheck";
        this.rectF = new RectF();
        this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), Bitmap.Config.ARGB_4444);
        this.drawCanvas = new Canvas(this.drawBitmap);
        this.isAlert = alert;
    }

    public void setColors(String unchecked, String checked, String check) {
        this.key1 = unchecked;
        this.key2 = checked;
        this.key3 = check;
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
        ofFloat.setDuration(300);
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

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
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

    public void setDisabled(boolean disabled) {
        this.isDisabled = disabled;
        invalidate();
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float checkProgress;
        float bounceProgress;
        if (getVisibility() == 0) {
            int uncheckedColor = getThemedColor(this.key1);
            int color = getThemedColor(this.key2);
            float bounceProgress2 = this.progress;
            if (bounceProgress2 <= 0.5f) {
                bounceProgress = bounceProgress2 / 0.5f;
                checkProgress = bounceProgress;
                Theme.checkboxSquare_backgroundPaint.setColor(Color.rgb(Color.red(uncheckedColor) + ((int) (((float) (Color.red(color) - Color.red(uncheckedColor))) * checkProgress)), Color.green(uncheckedColor) + ((int) (((float) (Color.green(color) - Color.green(uncheckedColor))) * checkProgress)), Color.blue(uncheckedColor) + ((int) (((float) (Color.blue(color) - Color.blue(uncheckedColor))) * checkProgress))));
            } else {
                bounceProgress = 2.0f - (bounceProgress2 / 0.5f);
                checkProgress = 1.0f;
                Theme.checkboxSquare_backgroundPaint.setColor(color);
            }
            if (this.isDisabled) {
                Theme.checkboxSquare_backgroundPaint.setColor(getThemedColor(this.isAlert ? "dialogCheckboxSquareDisabled" : "checkboxSquareDisabled"));
            }
            float bounce = ((float) AndroidUtilities.dp(1.0f)) * bounceProgress;
            this.rectF.set(bounce, bounce, ((float) AndroidUtilities.dp(18.0f)) - bounce, ((float) AndroidUtilities.dp(18.0f)) - bounce);
            this.drawBitmap.eraseColor(0);
            this.drawCanvas.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), Theme.checkboxSquare_backgroundPaint);
            if (checkProgress != 1.0f) {
                float rad = Math.min((float) AndroidUtilities.dp(7.0f), (((float) AndroidUtilities.dp(7.0f)) * checkProgress) + bounce);
                this.rectF.set(((float) AndroidUtilities.dp(2.0f)) + rad, ((float) AndroidUtilities.dp(2.0f)) + rad, ((float) AndroidUtilities.dp(16.0f)) - rad, ((float) AndroidUtilities.dp(16.0f)) - rad);
                this.drawCanvas.drawRect(this.rectF, Theme.checkboxSquare_eraserPaint);
            }
            if (this.progress > 0.5f) {
                Theme.checkboxSquare_checkPaint.setColor(getThemedColor(this.key3));
                this.drawCanvas.drawLine((float) AndroidUtilities.dp(7.0f), (float) ((int) AndroidUtilities.dpf2(13.0f)), (float) ((int) (((float) AndroidUtilities.dp(7.0f)) - (((float) AndroidUtilities.dp(3.0f)) * (1.0f - bounceProgress)))), (float) ((int) (AndroidUtilities.dpf2(13.0f) - (((float) AndroidUtilities.dp(3.0f)) * (1.0f - bounceProgress)))), Theme.checkboxSquare_checkPaint);
                this.drawCanvas.drawLine((float) ((int) AndroidUtilities.dpf2(7.0f)), (float) ((int) AndroidUtilities.dpf2(13.0f)), (float) ((int) (AndroidUtilities.dpf2(7.0f) + (((float) AndroidUtilities.dp(7.0f)) * (1.0f - bounceProgress)))), (float) ((int) (AndroidUtilities.dpf2(13.0f) - (((float) AndroidUtilities.dp(7.0f)) * (1.0f - bounceProgress)))), Theme.checkboxSquare_checkPaint);
            }
            canvas.drawBitmap(this.drawBitmap, 0.0f, 0.0f, (Paint) null);
        }
    }

    /* access modifiers changed from: protected */
    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
