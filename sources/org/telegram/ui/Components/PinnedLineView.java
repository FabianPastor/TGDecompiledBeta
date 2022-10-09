package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes3.dex */
public class PinnedLineView extends View {
    float animateFromPosition;
    int animateFromTotal;
    int animateToPosition;
    int animateToTotal;
    boolean animationInProgress;
    float animationProgress;
    ValueAnimator animator;
    private int color;
    Paint fadePaint;
    Paint fadePaint2;
    private int lineHFrom;
    private int lineHTo;
    private int nextPosition;
    Paint paint;
    RectF rectF;
    boolean replaceInProgress;
    private final Theme.ResourcesProvider resourcesProvider;
    Paint selectedPaint;
    int selectedPosition;
    private float startOffsetFrom;
    private float startOffsetTo;
    int totalCount;

    public PinnedLineView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.selectedPosition = -1;
        this.totalCount = 0;
        this.rectF = new RectF();
        this.paint = new Paint(1);
        this.selectedPaint = new Paint(1);
        this.nextPosition = -1;
        this.resourcesProvider = resourcesProvider;
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.selectedPaint.setStyle(Paint.Style.FILL);
        this.selectedPaint.setStrokeCap(Paint.Cap.ROUND);
        this.fadePaint = new Paint();
        this.fadePaint.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, AndroidUtilities.dp(6.0f), new int[]{-1, 0}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
        this.fadePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        this.fadePaint2 = new Paint();
        this.fadePaint2.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, AndroidUtilities.dp(6.0f), new int[]{0, -1}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
        this.fadePaint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        updateColors();
    }

    public void updateColors() {
        int themedColor = getThemedColor("chat_topPanelLine");
        this.color = themedColor;
        this.paint.setColor(ColorUtils.setAlphaComponent(themedColor, (int) ((Color.alpha(themedColor) / 255.0f) * 112.0f)));
        this.selectedPaint.setColor(this.color);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void selectPosition(int i) {
        if (this.replaceInProgress) {
            this.nextPosition = i;
            return;
        }
        if (this.animationInProgress) {
            if (this.animateToPosition == i) {
                return;
            }
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            float f = this.animateFromPosition;
            float f2 = this.animationProgress;
            this.animateFromPosition = (f * (1.0f - f2)) + (this.animateToPosition * f2);
        } else {
            this.animateFromPosition = this.selectedPosition;
        }
        if (i == this.selectedPosition) {
            return;
        }
        this.animateToPosition = i;
        this.animationInProgress = true;
        this.animationProgress = 0.0f;
        invalidate();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.animator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PinnedLineView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                PinnedLineView.this.lambda$selectPosition$0(valueAnimator2);
            }
        });
        this.animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PinnedLineView.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                PinnedLineView pinnedLineView = PinnedLineView.this;
                pinnedLineView.animationInProgress = false;
                pinnedLineView.selectedPosition = pinnedLineView.animateToPosition;
                pinnedLineView.invalidate();
                if (PinnedLineView.this.nextPosition >= 0) {
                    PinnedLineView pinnedLineView2 = PinnedLineView.this;
                    pinnedLineView2.selectPosition(pinnedLineView2.nextPosition);
                    PinnedLineView.this.nextPosition = -1;
                }
            }
        });
        this.animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.animator.setDuration(220L);
        this.animator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$selectPosition$0(ValueAnimator valueAnimator) {
        this.animationProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int i;
        float measuredHeight;
        float f;
        int i2;
        int i3;
        int i4;
        int i5;
        super.onDraw(canvas);
        if (this.selectedPosition < 0 || (i = this.totalCount) == 0) {
            return;
        }
        if (this.replaceInProgress) {
            i = Math.max(this.animateFromTotal, this.animateToTotal);
        }
        boolean z = i > 3;
        if (z) {
            canvas.saveLayerAlpha(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), 255, 31);
        }
        int dp = AndroidUtilities.dp(8.0f);
        if (this.replaceInProgress) {
            float f2 = this.animationProgress;
            measuredHeight = (this.lineHFrom * (1.0f - f2)) + (this.lineHTo * f2);
        } else if (this.totalCount == 0) {
            return;
        } else {
            measuredHeight = (getMeasuredHeight() - (dp * 2)) / Math.min(this.totalCount, 3);
        }
        float f3 = 0.0f;
        if (measuredHeight == 0.0f) {
            return;
        }
        float dpf2 = AndroidUtilities.dpf2(0.7f);
        if (this.replaceInProgress) {
            float f4 = this.startOffsetFrom;
            float f5 = this.animationProgress;
            f = (f4 * (1.0f - f5)) + (this.startOffsetTo * f5);
        } else {
            if (this.animationInProgress) {
                float f6 = this.animationProgress;
                f = ((this.animateFromPosition - 1.0f) * measuredHeight * (1.0f - f6)) + ((this.animateToPosition - 1) * measuredHeight * f6);
            } else {
                f = (this.selectedPosition - 1) * measuredHeight;
            }
            if (f < 0.0f) {
                f = 0.0f;
            } else {
                float f7 = dp;
                if ((((this.totalCount - 1) * measuredHeight) + f7) - f < (getMeasuredHeight() - dp) - measuredHeight) {
                    f = (f7 + ((this.totalCount - 1) * measuredHeight)) - ((getMeasuredHeight() - dp) - measuredHeight);
                }
            }
        }
        float measuredWidth = getMeasuredWidth() / 2.0f;
        float f8 = dp;
        int max = Math.max(0, (int) (((f8 + f) / measuredHeight) - 1.0f));
        int min = Math.min(max + 6, this.replaceInProgress ? Math.max(this.animateFromTotal, this.animateToTotal) : this.totalCount);
        while (max < min) {
            float f9 = ((max * measuredHeight) + f8) - f;
            float var_ = f9 + measuredHeight;
            if (var_ >= f3 && f9 <= getMeasuredHeight()) {
                this.rectF.set(f3, f9 + dpf2, getMeasuredWidth(), var_ - dpf2);
                boolean z2 = this.replaceInProgress;
                if (z2 && max >= this.animateToTotal) {
                    this.paint.setColor(ColorUtils.setAlphaComponent(this.color, (int) ((Color.alpha(i4) / 255.0f) * 76.0f * (1.0f - this.animationProgress))));
                    canvas.drawRoundRect(this.rectF, measuredWidth, measuredWidth, this.paint);
                    this.paint.setColor(ColorUtils.setAlphaComponent(this.color, (int) ((Color.alpha(i5) / 255.0f) * 76.0f)));
                } else if (z2 && max >= this.animateFromTotal) {
                    this.paint.setColor(ColorUtils.setAlphaComponent(this.color, (int) ((Color.alpha(i2) / 255.0f) * 76.0f * this.animationProgress)));
                    canvas.drawRoundRect(this.rectF, measuredWidth, measuredWidth, this.paint);
                    this.paint.setColor(ColorUtils.setAlphaComponent(this.color, (int) ((Color.alpha(i3) / 255.0f) * 76.0f)));
                } else {
                    canvas.drawRoundRect(this.rectF, measuredWidth, measuredWidth, this.paint);
                }
            }
            max++;
            f3 = 0.0f;
        }
        if (this.animationInProgress) {
            float var_ = this.animateFromPosition;
            float var_ = this.animationProgress;
            float var_ = (f8 + (((var_ * (1.0f - var_)) + (this.animateToPosition * var_)) * measuredHeight)) - f;
            this.rectF.set(0.0f, var_ + dpf2, getMeasuredWidth(), (var_ + measuredHeight) - dpf2);
            canvas.drawRoundRect(this.rectF, measuredWidth, measuredWidth, this.selectedPaint);
        } else {
            float var_ = (f8 + (this.selectedPosition * measuredHeight)) - f;
            this.rectF.set(0.0f, var_ + dpf2, getMeasuredWidth(), (var_ + measuredHeight) - dpf2);
            canvas.drawRoundRect(this.rectF, measuredWidth, measuredWidth, this.selectedPaint);
        }
        if (!z) {
            return;
        }
        canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), AndroidUtilities.dp(6.0f), this.fadePaint);
        canvas.drawRect(0.0f, getMeasuredHeight() - AndroidUtilities.dp(6.0f), getMeasuredWidth(), getMeasuredHeight(), this.fadePaint);
        canvas.translate(0.0f, getMeasuredHeight() - AndroidUtilities.dp(6.0f));
        canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), AndroidUtilities.dp(6.0f), this.fadePaint2);
    }

    public void set(int i, int i2, boolean z) {
        int dp;
        int i3;
        int i4;
        int i5 = this.selectedPosition;
        if (i5 < 0 || i2 == 0 || this.totalCount == 0) {
            z = false;
        }
        if (!z) {
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.selectedPosition = i;
            this.totalCount = i2;
            invalidate();
        } else if (this.totalCount != i2 || (Math.abs(i5 - i) > 2 && !this.animationInProgress && !this.replaceInProgress)) {
            ValueAnimator valueAnimator2 = this.animator;
            if (valueAnimator2 != null) {
                this.nextPosition = 0;
                valueAnimator2.cancel();
            }
            int dp2 = AndroidUtilities.dp(8.0f) * 2;
            this.lineHFrom = (getMeasuredHeight() - dp2) / Math.min(this.totalCount, 3);
            this.lineHTo = (getMeasuredHeight() - dp2) / Math.min(i2, 3);
            float f = (this.selectedPosition - 1) * this.lineHFrom;
            this.startOffsetFrom = f;
            if (f < 0.0f) {
                this.startOffsetFrom = 0.0f;
            } else {
                int i6 = this.lineHFrom;
                if ((((this.totalCount - 1) * i3) + dp) - f < (getMeasuredHeight() - dp) - i6) {
                    this.startOffsetFrom = (((this.totalCount - 1) * i6) + dp) - ((getMeasuredHeight() - dp) - this.lineHFrom);
                }
            }
            float f2 = (i - 1) * this.lineHTo;
            this.startOffsetTo = f2;
            if (f2 < 0.0f) {
                this.startOffsetTo = 0.0f;
            } else {
                int i7 = i2 - 1;
                int i8 = this.lineHTo;
                if (((i4 * i7) + dp) - f2 < (getMeasuredHeight() - dp) - i8) {
                    this.startOffsetTo = ((i7 * i8) + dp) - ((getMeasuredHeight() - dp) - this.lineHTo);
                }
            }
            this.animateFromPosition = this.selectedPosition;
            this.animateToPosition = i;
            this.selectedPosition = i;
            this.animateFromTotal = this.totalCount;
            this.animateToTotal = i2;
            this.totalCount = i2;
            this.replaceInProgress = true;
            this.animationInProgress = true;
            this.animationProgress = 0.0f;
            invalidate();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.animator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PinnedLineView$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    PinnedLineView.this.lambda$set$1(valueAnimator3);
                }
            });
            this.animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PinnedLineView.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    PinnedLineView pinnedLineView = PinnedLineView.this;
                    pinnedLineView.replaceInProgress = false;
                    pinnedLineView.animationInProgress = false;
                    pinnedLineView.invalidate();
                    if (PinnedLineView.this.nextPosition >= 0) {
                        PinnedLineView pinnedLineView2 = PinnedLineView.this;
                        pinnedLineView2.selectPosition(pinnedLineView2.nextPosition);
                        PinnedLineView.this.nextPosition = -1;
                    }
                }
            });
            this.animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.animator.setDuration(220L);
            this.animator.start();
        } else {
            selectPosition(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$set$1(ValueAnimator valueAnimator) {
        this.animationProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
