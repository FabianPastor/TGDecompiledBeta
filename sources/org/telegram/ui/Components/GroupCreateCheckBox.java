package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.view.View;
import androidx.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class GroupCreateCheckBox extends View {
    private static Paint eraser = null;
    private static Paint eraser2 = null;
    private static final float progressBounceDiff = 0.2f;
    private boolean attachedToWindow;
    private Paint backgroundInnerPaint;
    private String backgroundKey;
    private Paint backgroundPaint;
    private Canvas bitmapCanvas;
    private ObjectAnimator checkAnimator;
    private String checkKey;
    private Paint checkPaint;
    private float checkScale = 1.0f;
    private Bitmap drawBitmap;
    private String innerKey;
    private int innerRadDiff;
    private boolean isCheckAnimation = true;
    private boolean isChecked;
    private float progress;

    public GroupCreateCheckBox(Context context) {
        super(context);
        String str = "checkboxCheck";
        this.backgroundKey = str;
        this.checkKey = str;
        this.innerKey = "checkbox";
        if (eraser == null) {
            eraser = new Paint(1);
            eraser.setColor(0);
            eraser.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            eraser2 = new Paint(1);
            eraser2.setColor(0);
            eraser2.setStyle(Style.STROKE);
            eraser2.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        }
        this.backgroundPaint = new Paint(1);
        this.backgroundInnerPaint = new Paint(1);
        this.checkPaint = new Paint(1);
        this.checkPaint.setStyle(Style.STROKE);
        this.innerRadDiff = AndroidUtilities.dp(2.0f);
        this.checkPaint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
        eraser2.setStrokeWidth((float) AndroidUtilities.dp(28.0f));
        this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), Config.ARGB_4444);
        this.bitmapCanvas = new Canvas(this.drawBitmap);
        updateColors();
    }

    public void setColorKeysOverrides(String str, String str2, String str3) {
        this.checkKey = str;
        this.innerKey = str2;
        this.backgroundKey = str3;
        updateColors();
    }

    public void updateColors() {
        this.backgroundInnerPaint.setColor(Theme.getColor(this.innerKey));
        this.backgroundPaint.setColor(Theme.getColor(this.backgroundKey));
        this.checkPaint.setColor(Theme.getColor(this.checkKey));
        invalidate();
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

    public void setCheckScale(float f) {
        this.checkScale = f;
    }

    private void cancelCheckAnimator() {
        ObjectAnimator objectAnimator = this.checkAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
    }

    private void animateToCheckedState(boolean z) {
        this.isCheckAnimation = z;
        float[] fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.0f;
        this.checkAnimator = ObjectAnimator.ofFloat(this, "progress", fArr);
        this.checkAnimator.setDuration(300);
        this.checkAnimator.start();
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateColors();
        this.attachedToWindow = true;
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
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
                setProgress(z ? 1.0f : 0.0f);
            }
        }
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setInnerRadDiff(int i) {
        this.innerRadDiff = i;
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (getVisibility() == 0 && this.progress != 0.0f) {
            int measuredWidth = getMeasuredWidth() / 2;
            int measuredHeight = getMeasuredHeight() / 2;
            eraser2.setStrokeWidth((float) AndroidUtilities.dp(30.0f));
            this.drawBitmap.eraseColor(0);
            float f = this.progress;
            f = f >= 0.5f ? 1.0f : f / 0.5f;
            float f2 = this.progress;
            float f3 = f2 < 0.5f ? 0.0f : (f2 - 0.5f) / 0.5f;
            f2 = this.isCheckAnimation ? this.progress : 1.0f - this.progress;
            f2 = f2 < 0.2f ? (((float) AndroidUtilities.dp(2.0f)) * f2) / 0.2f : f2 < 0.4f ? ((float) AndroidUtilities.dp(2.0f)) - ((((float) AndroidUtilities.dp(2.0f)) * (f2 - 0.2f)) / 0.2f) : 0.0f;
            if (f3 != 0.0f) {
                canvas.drawCircle((float) measuredWidth, (float) measuredHeight, (((float) (measuredWidth - AndroidUtilities.dp(2.0f))) + (((float) AndroidUtilities.dp(2.0f)) * f3)) - f2, this.backgroundPaint);
            }
            float f4 = ((float) (measuredWidth - this.innerRadDiff)) - f2;
            float f5 = (float) measuredWidth;
            float f6 = (float) measuredHeight;
            this.bitmapCanvas.drawCircle(f5, f6, f4, this.backgroundInnerPaint);
            this.bitmapCanvas.drawCircle(f5, f6, f4 * (1.0f - f), eraser);
            canvas.drawBitmap(this.drawBitmap, 0.0f, 0.0f, null);
            f2 = (((float) AndroidUtilities.dp(10.0f)) * f3) * this.checkScale;
            float dp = (((float) AndroidUtilities.dp(5.0f)) * f3) * this.checkScale;
            int dp2 = measuredWidth - AndroidUtilities.dp(1.0f);
            float sqrt = (float) Math.sqrt((double) ((dp * dp) / 2.0f));
            dp = (float) dp2;
            f5 = (float) (measuredHeight + AndroidUtilities.dp(4.0f));
            float f7 = f5;
            canvas.drawLine(dp, f7, dp - sqrt, f5 - sqrt, this.checkPaint);
            sqrt = (float) Math.sqrt((double) ((f2 * f2) / 2.0f));
            dp = (float) (dp2 - AndroidUtilities.dp(1.2f));
            canvas.drawLine(dp, f7, dp + sqrt, f5 - sqrt, this.checkPaint);
        }
    }
}
