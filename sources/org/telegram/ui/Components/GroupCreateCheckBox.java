package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.View;
import androidx.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class GroupCreateCheckBox extends View {
    private static Paint eraser;
    private static Paint eraser2;
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

    @Keep
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

    public void setInnerRadDiff(int i) {
        this.innerRadDiff = i;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f;
        if (getVisibility() == 0 && this.progress != 0.0f) {
            int measuredWidth = getMeasuredWidth() / 2;
            int measuredHeight = getMeasuredHeight() / 2;
            eraser2.setStrokeWidth((float) AndroidUtilities.dp(30.0f));
            this.drawBitmap.eraseColor(0);
            float f2 = this.progress;
            float f3 = f2 >= 0.5f ? 1.0f : f2 / 0.5f;
            float f4 = this.progress;
            float f5 = f4 < 0.5f ? 0.0f : (f4 - 0.5f) / 0.5f;
            float f6 = this.isCheckAnimation ? this.progress : 1.0f - this.progress;
            if (f6 < 0.2f) {
                f = (((float) AndroidUtilities.dp(2.0f)) * f6) / 0.2f;
            } else {
                f = f6 < 0.4f ? ((float) AndroidUtilities.dp(2.0f)) - ((((float) AndroidUtilities.dp(2.0f)) * (f6 - 0.2f)) / 0.2f) : 0.0f;
            }
            if (f5 != 0.0f) {
                canvas.drawCircle((float) measuredWidth, (float) measuredHeight, (((float) (measuredWidth - AndroidUtilities.dp(2.0f))) + (((float) AndroidUtilities.dp(2.0f)) * f5)) - f, this.backgroundPaint);
            }
            float f7 = ((float) (measuredWidth - this.innerRadDiff)) - f;
            float f8 = (float) measuredWidth;
            float f9 = (float) measuredHeight;
            this.bitmapCanvas.drawCircle(f8, f9, f7, this.backgroundInnerPaint);
            this.bitmapCanvas.drawCircle(f8, f9, f7 * (1.0f - f3), eraser);
            canvas.drawBitmap(this.drawBitmap, 0.0f, 0.0f, (Paint) null);
            float dp = ((float) AndroidUtilities.dp(10.0f)) * f5 * this.checkScale;
            float dp2 = ((float) AndroidUtilities.dp(5.0f)) * f5 * this.checkScale;
            int dp3 = measuredWidth - AndroidUtilities.dp(1.0f);
            int dp4 = measuredHeight + AndroidUtilities.dp(4.0f);
            float sqrt = (float) Math.sqrt((double) ((dp2 * dp2) / 2.0f));
            float var_ = (float) dp3;
            float var_ = (float) dp4;
            float var_ = var_;
            canvas.drawLine(var_, var_, var_ - sqrt, var_ - sqrt, this.checkPaint);
            float sqrt2 = (float) Math.sqrt((double) ((dp * dp) / 2.0f));
            float dp5 = (float) (dp3 - AndroidUtilities.dp(1.2f));
            canvas.drawLine(dp5, var_, dp5 + sqrt2, var_ - sqrt2, this.checkPaint);
        }
    }
}
