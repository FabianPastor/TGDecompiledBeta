package org.telegram.ui.Components.Premium;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.voip.CellFlickerDrawable;

public class PremiumButtonView extends FrameLayout {
    public TextView buttonTextView;
    CellFlickerDrawable flickerDrawable;
    private boolean inc;
    private int lastW;
    ValueAnimator overlayAnimator;
    /* access modifiers changed from: private */
    public float overlayProgress;
    public TextView overlayTextView;
    private Paint paint = new Paint(1);
    private Paint paintOverlayPaint = new Paint(1);
    Path path;
    private float progress;
    private Shader shader;
    /* access modifiers changed from: private */
    public boolean showOverlay;

    public PremiumButtonView(Context context, boolean z) {
        super(context);
        new Matrix();
        this.path = new Path();
        CellFlickerDrawable cellFlickerDrawable = new CellFlickerDrawable();
        this.flickerDrawable = cellFlickerDrawable;
        cellFlickerDrawable.animationSpeedScale = 1.0f;
        cellFlickerDrawable.drawFrame = false;
        cellFlickerDrawable.repeatProgress = 2.0f;
        TextView textView = new TextView(context);
        this.buttonTextView = textView;
        textView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextColor(-1);
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.buttonTextView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(8.0f), 0, ColorUtils.setAlphaComponent(-1, 120)));
        addView(this.buttonTextView);
        if (z) {
            TextView textView2 = new TextView(context);
            this.overlayTextView = textView2;
            textView2.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            this.overlayTextView.setGravity(17);
            this.overlayTextView.setTextColor(-1);
            this.overlayTextView.setTextSize(1, 14.0f);
            this.overlayTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.overlayTextView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(8.0f), 0, ColorUtils.setAlphaComponent(-1, 120)));
            addView(this.overlayTextView);
            this.paintOverlayPaint.setColor(Theme.getColor("featuredStickers_addButton"));
            updateOverlayProgress();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.lastW != getMeasuredWidth()) {
            this.lastW = getMeasuredWidth();
            LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, (float) getMeasuredWidth(), 0.0f, new int[]{-9015575, -1026983, -1792170}, (float[]) null, Shader.TileMode.CLAMP);
            this.shader = linearGradient;
            this.paint.setShader(linearGradient);
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.shader != null) {
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
            if (this.overlayProgress != 1.0f) {
                if (this.inc) {
                    float f = this.progress + 0.016f;
                    this.progress = f;
                    if (f > 3.0f) {
                        this.inc = false;
                    }
                } else {
                    float f2 = this.progress - 0.016f;
                    this.progress = f2;
                    if (f2 < 1.0f) {
                        this.inc = true;
                    }
                }
                PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, getMeasuredWidth(), getMeasuredHeight(), this.progress * ((float) (-getMeasuredWidth())) * 0.1f, 0.0f);
                canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(8.0f), PremiumGradient.getInstance().getMainGradientPaint());
                invalidate();
            }
            if (!BuildVars.IS_BILLING_UNAVAILABLE) {
                this.flickerDrawable.setParentWidth(getMeasuredWidth());
                this.flickerDrawable.draw(canvas, rectF, (float) AndroidUtilities.dp(8.0f));
            }
            float f3 = this.overlayProgress;
            if (f3 != 0.0f) {
                this.paintOverlayPaint.setAlpha((int) (f3 * 255.0f));
                if (this.overlayProgress != 1.0f) {
                    this.path.rewind();
                    this.path.addCircle(((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f, ((float) Math.max(getMeasuredWidth(), getMeasuredHeight())) * 1.4f * this.overlayProgress, Path.Direction.CW);
                    canvas.save();
                    canvas.clipPath(this.path);
                    canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(8.0f), this.paintOverlayPaint);
                    canvas.restore();
                } else {
                    canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(8.0f), this.paintOverlayPaint);
                }
            }
        }
        super.dispatchDraw(canvas);
    }

    public void setOverlayText(String str, boolean z) {
        this.showOverlay = true;
        this.overlayTextView.setText(str);
        updateOverlay(z);
    }

    private void updateOverlay(boolean z) {
        ValueAnimator valueAnimator = this.overlayAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.overlayAnimator.cancel();
        }
        float f = 1.0f;
        if (!z) {
            if (!this.showOverlay) {
                f = 0.0f;
            }
            this.overlayProgress = f;
            updateOverlayProgress();
            return;
        }
        float[] fArr = new float[2];
        fArr[0] = this.overlayProgress;
        if (!this.showOverlay) {
            f = 0.0f;
        }
        fArr[1] = f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
        this.overlayAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float unused = PremiumButtonView.this.overlayProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                PremiumButtonView.this.updateOverlayProgress();
            }
        });
        this.overlayAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                PremiumButtonView premiumButtonView = PremiumButtonView.this;
                float unused = premiumButtonView.overlayProgress = premiumButtonView.showOverlay ? 1.0f : 0.0f;
                PremiumButtonView.this.updateOverlayProgress();
            }
        });
        this.overlayAnimator.setDuration(250);
        this.overlayAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.overlayAnimator.start();
    }

    /* access modifiers changed from: private */
    public void updateOverlayProgress() {
        this.overlayTextView.setAlpha(this.overlayProgress);
        this.overlayTextView.setTranslationY(((float) AndroidUtilities.dp(12.0f)) * (1.0f - this.overlayProgress));
        this.buttonTextView.setAlpha(1.0f - this.overlayProgress);
        this.buttonTextView.setTranslationY(((float) (-AndroidUtilities.dp(12.0f))) * this.overlayProgress);
        int i = 8;
        this.buttonTextView.setVisibility(this.overlayProgress == 1.0f ? 8 : 0);
        TextView textView = this.overlayTextView;
        if (this.overlayProgress != 0.0f) {
            i = 0;
        }
        textView.setVisibility(i);
        invalidate();
    }

    public void clearOverlayText() {
        this.showOverlay = false;
        updateOverlay(true);
    }
}
