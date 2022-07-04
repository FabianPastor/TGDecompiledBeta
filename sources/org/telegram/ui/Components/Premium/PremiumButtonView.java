package org.telegram.ui.Components.Premium;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.voip.CellFlickerDrawable;

public class PremiumButtonView extends FrameLayout {
    public FrameLayout buttonLayout;
    public TextView buttonTextView;
    private boolean drawOverlayColor;
    CellFlickerDrawable flickerDrawable;
    RLottieImageView iconView;
    private boolean inc;
    ValueAnimator overlayAnimator;
    /* access modifiers changed from: private */
    public float overlayProgress;
    public TextView overlayTextView;
    private Paint paintOverlayPaint = new Paint(1);
    Path path = new Path();
    private float progress;
    /* access modifiers changed from: private */
    public boolean showOverlay;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PremiumButtonView(Context context, boolean createOverlayTextView) {
        super(context);
        Context context2 = context;
        CellFlickerDrawable cellFlickerDrawable = new CellFlickerDrawable();
        this.flickerDrawable = cellFlickerDrawable;
        cellFlickerDrawable.animationSpeedScale = 1.2f;
        this.flickerDrawable.drawFrame = false;
        this.flickerDrawable.repeatProgress = 4.0f;
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        TextView textView = new TextView(context2);
        this.buttonTextView = textView;
        textView.setGravity(17);
        this.buttonTextView.setTextColor(-1);
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.iconView = rLottieImageView;
        rLottieImageView.setColorFilter(-1);
        this.iconView.setVisibility(8);
        FrameLayout frameLayout = new FrameLayout(context2);
        this.buttonLayout = frameLayout;
        frameLayout.addView(linearLayout, LayoutHelper.createFrame(-2, -2, 17));
        this.buttonLayout.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(8.0f), 0, ColorUtils.setAlphaComponent(-1, 120)));
        linearLayout.addView(this.buttonTextView, LayoutHelper.createLinear(-2, -2, 16));
        linearLayout.addView(this.iconView, LayoutHelper.createLinear(24, 24, 0.0f, 16, 4, 0, 0, 0));
        addView(this.buttonLayout);
        if (createOverlayTextView) {
            TextView textView2 = new TextView(context2);
            this.overlayTextView = textView2;
            textView2.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            this.overlayTextView.setGravity(17);
            this.overlayTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            this.overlayTextView.setTextSize(1, 14.0f);
            this.overlayTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.overlayTextView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(8.0f), 0, ColorUtils.setAlphaComponent(-1, 120)));
            addView(this.overlayTextView);
            this.paintOverlayPaint.setColor(Theme.getColor("featuredStickers_addButton"));
            updateOverlayProgress();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        AndroidUtilities.rectTmp.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
        if (this.overlayProgress != 1.0f || !this.drawOverlayColor) {
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
            canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(8.0f), PremiumGradient.getInstance().getMainGradientPaint());
            invalidate();
        }
        if (!BuildVars.IS_BILLING_UNAVAILABLE) {
            this.flickerDrawable.setParentWidth(getMeasuredWidth());
            this.flickerDrawable.draw(canvas, AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(8.0f), (View) null);
        }
        float f3 = this.overlayProgress;
        if (f3 != 0.0f && this.drawOverlayColor) {
            this.paintOverlayPaint.setAlpha((int) (f3 * 255.0f));
            if (this.overlayProgress != 1.0f) {
                this.path.rewind();
                this.path.addCircle(((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f, ((float) Math.max(getMeasuredWidth(), getMeasuredHeight())) * 1.4f * this.overlayProgress, Path.Direction.CW);
                canvas.save();
                canvas.clipPath(this.path);
                canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(8.0f), this.paintOverlayPaint);
                canvas.restore();
            } else {
                canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(8.0f), this.paintOverlayPaint);
            }
        }
        super.dispatchDraw(canvas);
    }

    public void setOverlayText(String text, boolean drawOverlayColor2, boolean animated) {
        this.showOverlay = true;
        this.drawOverlayColor = drawOverlayColor2;
        this.overlayTextView.setText(text);
        updateOverlay(animated);
    }

    private void updateOverlay(boolean animated) {
        ValueAnimator valueAnimator = this.overlayAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.overlayAnimator.cancel();
        }
        float f = 1.0f;
        if (!animated) {
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
            public void onAnimationUpdate(ValueAnimator animation) {
                float unused = PremiumButtonView.this.overlayProgress = ((Float) animation.getAnimatedValue()).floatValue();
                PremiumButtonView.this.updateOverlayProgress();
            }
        });
        this.overlayAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
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
        this.buttonLayout.setAlpha(1.0f - this.overlayProgress);
        this.buttonLayout.setTranslationY(((float) (-AndroidUtilities.dp(12.0f))) * this.overlayProgress);
        int i = 4;
        this.buttonLayout.setVisibility(this.overlayProgress == 1.0f ? 4 : 0);
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

    public void setIcon(int id) {
        this.iconView.setAnimation(id, 24, 24);
        this.flickerDrawable.progress = 2.0f;
        this.flickerDrawable.setOnRestartCallback(new PremiumButtonView$$ExternalSyntheticLambda0(this));
        invalidate();
        this.iconView.setVisibility(0);
    }

    /* renamed from: lambda$setIcon$0$org-telegram-ui-Components-Premium-PremiumButtonView  reason: not valid java name */
    public /* synthetic */ void m1250x1bdc7e71() {
        this.iconView.getAnimatedDrawable().setCurrentFrame(0, true);
        this.iconView.playAnimation();
    }

    public void hideIcon() {
        this.flickerDrawable.setOnRestartCallback((Runnable) null);
        this.iconView.setVisibility(8);
    }

    public void setButton(String text, View.OnClickListener clickListener) {
        this.buttonTextView.setText(text);
        this.buttonLayout.setOnClickListener(clickListener);
    }
}
