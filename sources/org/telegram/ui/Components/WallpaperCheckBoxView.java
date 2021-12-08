package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.Property;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimationProperties;

public class WallpaperCheckBoxView extends View {
    private static final float progressBounceDiff = 0.2f;
    public final Property<WallpaperCheckBoxView, Float> PROGRESS_PROPERTY = new AnimationProperties.FloatProperty<WallpaperCheckBoxView>("progress") {
        public void setValue(WallpaperCheckBoxView object, float value) {
            float unused = WallpaperCheckBoxView.this.progress = value;
            WallpaperCheckBoxView.this.invalidate();
        }

        public Float get(WallpaperCheckBoxView object) {
            return Float.valueOf(WallpaperCheckBoxView.this.progress);
        }
    };
    private Paint backgroundPaint;
    private ObjectAnimator checkAnimator;
    private Paint checkPaint;
    private int[] colors = new int[4];
    private String currentText;
    private int currentTextSize;
    private Bitmap drawBitmap;
    private Canvas drawCanvas;
    private Paint eraserPaint;
    private boolean isChecked;
    private int maxTextSize;
    private View parentView;
    /* access modifiers changed from: private */
    public float progress;
    private RectF rect = new RectF();
    private TextPaint textPaint;

    public WallpaperCheckBoxView(Context context, boolean check, View parent) {
        super(context);
        if (check) {
            this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), Bitmap.Config.ARGB_4444);
            this.drawCanvas = new Canvas(this.drawBitmap);
        }
        this.parentView = parent;
        TextPaint textPaint2 = new TextPaint(1);
        this.textPaint = textPaint2;
        textPaint2.setTextSize((float) AndroidUtilities.dp(14.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        Paint paint = new Paint(1);
        this.checkPaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.checkPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.checkPaint.setColor(0);
        this.checkPaint.setStrokeCap(Paint.Cap.ROUND);
        this.checkPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Paint paint2 = new Paint(1);
        this.eraserPaint = paint2;
        paint2.setColor(0);
        this.eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.backgroundPaint = new Paint(1);
    }

    public void setText(String text, int current, int max) {
        this.currentText = text;
        this.currentTextSize = current;
        this.maxTextSize = max;
    }

    public void setColor(int index, int color) {
        if (this.colors == null) {
            this.colors = new int[4];
        }
        this.colors[index] = color;
        invalidate();
    }

    public TextPaint getTextPaint() {
        return this.textPaint;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.maxTextSize + AndroidUtilities.dp(56.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float checkProgress;
        float bounceProgress;
        Canvas canvas2 = canvas;
        this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
        Theme.applyServiceShaderMatrixForView(this, this.parentView);
        canvas2.drawRoundRect(this.rect, (float) (getMeasuredHeight() / 2), (float) (getMeasuredHeight() / 2), Theme.chat_actionBackgroundPaint);
        if (Theme.hasGradientService()) {
            canvas2.drawRoundRect(this.rect, (float) (getMeasuredHeight() / 2), (float) (getMeasuredHeight() / 2), Theme.chat_actionBackgroundGradientDarkenPaint);
        }
        this.textPaint.setColor(Theme.getColor("chat_serviceText"));
        int x = ((getMeasuredWidth() - this.currentTextSize) - AndroidUtilities.dp(28.0f)) / 2;
        canvas2.drawText(this.currentText, (float) (AndroidUtilities.dp(28.0f) + x), (float) AndroidUtilities.dp(21.0f), this.textPaint);
        canvas.save();
        canvas2.translate((float) x, (float) AndroidUtilities.dp(7.0f));
        if (this.drawBitmap != null) {
            float bounceProgress2 = this.progress;
            if (bounceProgress2 <= 0.5f) {
                bounceProgress = bounceProgress2 / 0.5f;
                checkProgress = bounceProgress;
            } else {
                bounceProgress = 2.0f - (bounceProgress2 / 0.5f);
                checkProgress = 1.0f;
            }
            float bounce = ((float) AndroidUtilities.dp(1.0f)) * bounceProgress;
            this.rect.set(bounce, bounce, ((float) AndroidUtilities.dp(18.0f)) - bounce, ((float) AndroidUtilities.dp(18.0f)) - bounce);
            this.drawBitmap.eraseColor(0);
            this.backgroundPaint.setColor(Theme.getColor("chat_serviceText"));
            Canvas canvas3 = this.drawCanvas;
            RectF rectF = this.rect;
            canvas3.drawRoundRect(rectF, rectF.width() / 2.0f, this.rect.height() / 2.0f, this.backgroundPaint);
            if (checkProgress != 1.0f) {
                float rad = Math.min((float) AndroidUtilities.dp(7.0f), (((float) AndroidUtilities.dp(7.0f)) * checkProgress) + bounce);
                this.rect.set(((float) AndroidUtilities.dp(2.0f)) + rad, ((float) AndroidUtilities.dp(2.0f)) + rad, ((float) AndroidUtilities.dp(16.0f)) - rad, ((float) AndroidUtilities.dp(16.0f)) - rad);
                Canvas canvas4 = this.drawCanvas;
                RectF rectF2 = this.rect;
                canvas4.drawRoundRect(rectF2, rectF2.width() / 2.0f, this.rect.height() / 2.0f, this.eraserPaint);
            }
            if (this.progress > 0.5f) {
                this.drawCanvas.drawLine((float) AndroidUtilities.dp(7.3f), (float) AndroidUtilities.dp(13.0f), (float) ((int) (((float) AndroidUtilities.dp(7.3f)) - (((float) AndroidUtilities.dp(2.5f)) * (1.0f - bounceProgress)))), (float) ((int) (((float) AndroidUtilities.dp(13.0f)) - (((float) AndroidUtilities.dp(2.5f)) * (1.0f - bounceProgress)))), this.checkPaint);
                this.drawCanvas.drawLine((float) AndroidUtilities.dp(7.3f), (float) AndroidUtilities.dp(13.0f), (float) ((int) (((float) AndroidUtilities.dp(7.3f)) + (((float) AndroidUtilities.dp(6.0f)) * (1.0f - bounceProgress)))), (float) ((int) (((float) AndroidUtilities.dp(13.0f)) - (((float) AndroidUtilities.dp(6.0f)) * (1.0f - bounceProgress)))), this.checkPaint);
            }
            canvas2.drawBitmap(this.drawBitmap, 0.0f, 0.0f, (Paint) null);
        } else {
            this.rect.set(0.0f, 0.0f, (float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(18.0f));
            int[] iArr = this.colors;
            if (iArr[3] != 0) {
                for (int a = 0; a < 4; a++) {
                    this.backgroundPaint.setColor(this.colors[a]);
                    canvas.drawArc(this.rect, (float) ((a * 90) - 90), 90.0f, true, this.backgroundPaint);
                }
            } else if (iArr[2] != 0) {
                for (int a2 = 0; a2 < 3; a2++) {
                    this.backgroundPaint.setColor(this.colors[a2]);
                    canvas.drawArc(this.rect, (float) ((a2 * 120) - 90), 120.0f, true, this.backgroundPaint);
                }
            } else if (iArr[1] != 0) {
                for (int a3 = 0; a3 < 2; a3++) {
                    this.backgroundPaint.setColor(this.colors[a3]);
                    canvas.drawArc(this.rect, (float) ((a3 * 180) - 90), 180.0f, true, this.backgroundPaint);
                }
            } else {
                this.backgroundPaint.setColor(iArr[0]);
                RectF rectF3 = this.rect;
                canvas2.drawRoundRect(rectF3, rectF3.width() / 2.0f, this.rect.height() / 2.0f, this.backgroundPaint);
            }
        }
        canvas.restore();
    }

    private void setProgress(float value) {
        if (this.progress != value) {
            this.progress = value;
            invalidate();
        }
    }

    private void cancelCheckAnimator() {
        ObjectAnimator objectAnimator = this.checkAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
    }

    private void animateToCheckedState(boolean newCheckedState) {
        Property<WallpaperCheckBoxView, Float> property = this.PROGRESS_PROPERTY;
        float[] fArr = new float[1];
        fArr[0] = newCheckedState ? 1.0f : 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, property, fArr);
        this.checkAnimator = ofFloat;
        ofFloat.setDuration(300);
        this.checkAnimator.start();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setChecked(boolean checked, boolean animated) {
        if (checked != this.isChecked) {
            this.isChecked = checked;
            if (animated) {
                animateToCheckedState(checked);
                return;
            }
            cancelCheckAnimator();
            this.progress = checked ? 1.0f : 0.0f;
            invalidate();
        }
    }

    public boolean isChecked() {
        return this.isChecked;
    }
}
