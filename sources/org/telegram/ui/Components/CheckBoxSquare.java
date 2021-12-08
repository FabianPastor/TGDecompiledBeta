package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import androidx.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class CheckBoxSquare extends View {
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

    public CheckBoxSquare(Context context, boolean z) {
        this(context, z, (Theme.ResourcesProvider) null);
    }

    public CheckBoxSquare(Context context, boolean z, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        if (Theme.checkboxSquare_backgroundPaint == null) {
            Theme.createCommonResources(context);
        }
        boolean z2 = this.isAlert;
        this.key1 = z2 ? "dialogCheckboxSquareUnchecked" : "checkboxSquareUnchecked";
        this.key2 = z2 ? "dialogCheckboxSquareBackground" : "checkboxSquareBackground";
        this.key3 = z2 ? "dialogCheckboxSquareCheck" : "checkboxSquareCheck";
        this.rectF = new RectF();
        this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), Bitmap.Config.ARGB_4444);
        this.drawCanvas = new Canvas(this.drawBitmap);
        this.isAlert = z;
    }

    public void setColors(String str, String str2, String str3) {
        this.key1 = str;
        this.key2 = str2;
        this.key3 = str3;
    }

    @Keep
    public void setProgress(float f) {
        if (this.progress != f) {
            this.progress = f;
            invalidate();
        }
    }

    @Keep
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
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
    }

    public void setChecked(boolean z, boolean z2) {
        if (z != this.isChecked) {
            this.isChecked = z;
            if (!this.attachedToWindow || !z2) {
                cancelCheckAnimator();
                setProgress(z ? 1.0f : 0.0f);
                return;
            }
            animateToCheckedState(z);
        }
    }

    public void setDisabled(boolean z) {
        this.isDisabled = z;
        invalidate();
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f;
        float f2;
        if (getVisibility() == 0) {
            int themedColor = getThemedColor(this.key1);
            int themedColor2 = getThemedColor(this.key2);
            float f3 = this.progress;
            if (f3 <= 0.5f) {
                f = f3 / 0.5f;
                Theme.checkboxSquare_backgroundPaint.setColor(Color.rgb(Color.red(themedColor) + ((int) (((float) (Color.red(themedColor2) - Color.red(themedColor))) * f)), Color.green(themedColor) + ((int) (((float) (Color.green(themedColor2) - Color.green(themedColor))) * f)), Color.blue(themedColor) + ((int) (((float) (Color.blue(themedColor2) - Color.blue(themedColor))) * f))));
                f2 = f;
            } else {
                Theme.checkboxSquare_backgroundPaint.setColor(themedColor2);
                f2 = 2.0f - (f3 / 0.5f);
                f = 1.0f;
            }
            if (this.isDisabled) {
                Theme.checkboxSquare_backgroundPaint.setColor(getThemedColor(this.isAlert ? "dialogCheckboxSquareDisabled" : "checkboxSquareDisabled"));
            }
            float dp = ((float) AndroidUtilities.dp(1.0f)) * f2;
            this.rectF.set(dp, dp, ((float) AndroidUtilities.dp(18.0f)) - dp, ((float) AndroidUtilities.dp(18.0f)) - dp);
            this.drawBitmap.eraseColor(0);
            this.drawCanvas.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), Theme.checkboxSquare_backgroundPaint);
            if (f != 1.0f) {
                float min = Math.min((float) AndroidUtilities.dp(7.0f), (((float) AndroidUtilities.dp(7.0f)) * f) + dp);
                this.rectF.set(((float) AndroidUtilities.dp(2.0f)) + min, ((float) AndroidUtilities.dp(2.0f)) + min, ((float) AndroidUtilities.dp(16.0f)) - min, ((float) AndroidUtilities.dp(16.0f)) - min);
                this.drawCanvas.drawRect(this.rectF, Theme.checkboxSquare_eraserPaint);
            }
            if (this.progress > 0.5f) {
                Theme.checkboxSquare_checkPaint.setColor(getThemedColor(this.key3));
                float f4 = 1.0f - f2;
                this.drawCanvas.drawLine((float) AndroidUtilities.dp(7.0f), (float) ((int) AndroidUtilities.dpf2(13.0f)), (float) ((int) (((float) AndroidUtilities.dp(7.0f)) - (((float) AndroidUtilities.dp(3.0f)) * f4))), (float) ((int) (AndroidUtilities.dpf2(13.0f) - (((float) AndroidUtilities.dp(3.0f)) * f4))), Theme.checkboxSquare_checkPaint);
                this.drawCanvas.drawLine((float) ((int) AndroidUtilities.dpf2(7.0f)), (float) ((int) AndroidUtilities.dpf2(13.0f)), (float) ((int) (AndroidUtilities.dpf2(7.0f) + (((float) AndroidUtilities.dp(7.0f)) * f4))), (float) ((int) (AndroidUtilities.dpf2(13.0f) - (((float) AndroidUtilities.dp(7.0f)) * f4))), Theme.checkboxSquare_checkPaint);
            }
            canvas.drawBitmap(this.drawBitmap, 0.0f, 0.0f, (Paint) null);
        }
    }

    /* access modifiers changed from: protected */
    public int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
