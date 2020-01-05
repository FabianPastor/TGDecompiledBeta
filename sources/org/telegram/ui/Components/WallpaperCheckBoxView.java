package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.text.TextPaint;
import android.util.Property;
import android.view.View;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimationProperties.FloatProperty;

public class WallpaperCheckBoxView extends View {
    private static final float progressBounceDiff = 0.2f;
    public final Property<WallpaperCheckBoxView, Float> PROGRESS_PROPERTY = new FloatProperty<WallpaperCheckBoxView>("progress") {
        public void setValue(WallpaperCheckBoxView wallpaperCheckBoxView, float f) {
            WallpaperCheckBoxView.this.progress = f;
            WallpaperCheckBoxView.this.invalidate();
        }

        public Float get(WallpaperCheckBoxView wallpaperCheckBoxView) {
            return Float.valueOf(WallpaperCheckBoxView.this.progress);
        }
    };
    private int backgroundColor;
    private int backgroundGradientColor;
    private Paint backgroundPaint;
    private ObjectAnimator checkAnimator;
    private Paint checkPaint;
    private LinearGradient colorGradient;
    private String currentText;
    private int currentTextSize;
    private Bitmap drawBitmap;
    private Canvas drawCanvas;
    private Paint eraserPaint;
    private boolean isChecked;
    private int maxTextSize;
    private float progress;
    private RectF rect = new RectF();
    private TextPaint textPaint;

    public WallpaperCheckBoxView(Context context, boolean z) {
        super(context);
        if (z) {
            this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), Config.ARGB_4444);
            this.drawCanvas = new Canvas(this.drawBitmap);
        }
        this.textPaint = new TextPaint(1);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.checkPaint = new Paint(1);
        this.checkPaint.setStyle(Style.STROKE);
        this.checkPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.checkPaint.setColor(0);
        this.checkPaint.setStrokeCap(Cap.ROUND);
        this.checkPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        this.eraserPaint = new Paint(1);
        this.eraserPaint.setColor(0);
        this.eraserPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        this.backgroundPaint = new Paint(1);
    }

    public void setText(String str, int i, int i2) {
        this.currentText = str;
        this.currentTextSize = i;
        this.maxTextSize = i2;
    }

    public void setBackgroundColor(int i) {
        this.colorGradient = null;
        this.backgroundColor = i;
        invalidate();
    }

    public void setBackgroundGradientColor(int i) {
        this.colorGradient = null;
        this.backgroundGradientColor = i;
        invalidate();
    }

    public TextPaint getTextPaint() {
        return this.textPaint;
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(this.maxTextSize + AndroidUtilities.dp(56.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
        canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_actionBackgroundPaint);
        String str = "chat_serviceText";
        this.textPaint.setColor(Theme.getColor(str));
        int measuredWidth = ((getMeasuredWidth() - this.currentTextSize) - AndroidUtilities.dp(28.0f)) / 2;
        canvas2.drawText(this.currentText, (float) (AndroidUtilities.dp(28.0f) + measuredWidth), (float) AndroidUtilities.dp(21.0f), this.textPaint);
        canvas.save();
        canvas2.translate((float) measuredWidth, (float) AndroidUtilities.dp(7.0f));
        float f;
        RectF rectF;
        if (this.drawBitmap != null) {
            float f2 = this.progress;
            if (f2 <= 0.5f) {
                f2 /= 0.5f;
                f = f2;
            } else {
                f2 = 2.0f - (f2 / 0.5f);
                f = 1.0f;
            }
            float dp = ((float) AndroidUtilities.dp(1.0f)) * f2;
            this.rect.set(dp, dp, ((float) AndroidUtilities.dp(18.0f)) - dp, ((float) AndroidUtilities.dp(18.0f)) - dp);
            this.drawBitmap.eraseColor(0);
            this.backgroundPaint.setColor(Theme.getColor(str));
            Canvas canvas3 = this.drawCanvas;
            RectF rectF2 = this.rect;
            canvas3.drawRoundRect(rectF2, rectF2.width() / 2.0f, this.rect.height() / 2.0f, this.backgroundPaint);
            if (f != 1.0f) {
                float min = Math.min((float) AndroidUtilities.dp(7.0f), (((float) AndroidUtilities.dp(7.0f)) * f) + dp);
                this.rect.set(((float) AndroidUtilities.dp(2.0f)) + min, ((float) AndroidUtilities.dp(2.0f)) + min, ((float) AndroidUtilities.dp(16.0f)) - min, ((float) AndroidUtilities.dp(16.0f)) - min);
                canvas3 = this.drawCanvas;
                rectF = this.rect;
                canvas3.drawRoundRect(rectF, rectF.width() / 2.0f, this.rect.height() / 2.0f, this.eraserPaint);
            }
            if (this.progress > 0.5f) {
                float f3 = 1.0f - f2;
                this.drawCanvas.drawLine((float) AndroidUtilities.dp(7.3f), (float) AndroidUtilities.dp(13.0f), (float) ((int) (((float) AndroidUtilities.dp(7.3f)) - (((float) AndroidUtilities.dp(2.5f)) * f3))), (float) ((int) (((float) AndroidUtilities.dp(13.0f)) - (((float) AndroidUtilities.dp(2.5f)) * f3))), this.checkPaint);
                this.drawCanvas.drawLine((float) AndroidUtilities.dp(7.3f), (float) AndroidUtilities.dp(13.0f), (float) ((int) (((float) AndroidUtilities.dp(7.3f)) + (((float) AndroidUtilities.dp(6.0f)) * f3))), (float) ((int) (((float) AndroidUtilities.dp(13.0f)) - (((float) AndroidUtilities.dp(6.0f)) * f3))), this.checkPaint);
            }
            canvas2.drawBitmap(this.drawBitmap, 0.0f, 0.0f, null);
        } else {
            this.rect.set(0.0f, 0.0f, (float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(18.0f));
            if (this.backgroundGradientColor != 0) {
                if (this.colorGradient == null) {
                    rectF = this.rect;
                    float f4 = rectF.left;
                    f = f4;
                    this.colorGradient = new LinearGradient(f, rectF.bottom, f4, rectF.top, new int[]{this.backgroundColor, measuredWidth}, null, TileMode.CLAMP);
                    this.backgroundPaint.setShader(this.colorGradient);
                }
                this.backgroundPaint.setColor(this.backgroundColor);
            } else {
                this.backgroundPaint.setColor(this.backgroundColor);
                this.backgroundPaint.setShader(null);
            }
            RectF rectF3 = this.rect;
            canvas2.drawRoundRect(rectF3, rectF3.width() / 2.0f, this.rect.height() / 2.0f, this.backgroundPaint);
        }
        canvas.restore();
    }

    private void setProgress(float f) {
        if (this.progress != f) {
            this.progress = f;
            invalidate();
        }
    }

    private void cancelCheckAnimator() {
        ObjectAnimator objectAnimator = this.checkAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
    }

    private void animateToCheckedState(boolean z) {
        Property property = this.PROGRESS_PROPERTY;
        float[] fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.0f;
        this.checkAnimator = ObjectAnimator.ofFloat(this, property, fArr);
        this.checkAnimator.setDuration(300);
        this.checkAnimator.start();
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
    }

    public void setChecked(boolean z, boolean z2) {
        if (z != this.isChecked) {
            this.isChecked = z;
            if (z2) {
                animateToCheckedState(z);
            } else {
                cancelCheckAnimator();
                this.progress = z ? 1.0f : 0.0f;
                invalidate();
            }
        }
    }

    public boolean isChecked() {
        return this.isChecked;
    }
}
