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
    /* access modifiers changed from: private */
    public int nextPosition = -1;
    Paint paint = new Paint(1);
    RectF rectF = new RectF();
    boolean replaceInProgress;
    Paint selectedPaint = new Paint(1);
    int selectedPosition = -1;
    private float startOffsetFrom;
    private float startOffsetTo;
    int totalCount = 0;

    public PinnedLineView(Context context) {
        super(context);
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.selectedPaint.setStyle(Paint.Style.FILL);
        this.selectedPaint.setStrokeCap(Paint.Cap.ROUND);
        this.fadePaint = new Paint();
        this.fadePaint.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, (float) AndroidUtilities.dp(6.0f), new int[]{-1, 0}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
        this.fadePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        this.fadePaint2 = new Paint();
        this.fadePaint2.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, (float) AndroidUtilities.dp(6.0f), new int[]{0, -1}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
        this.fadePaint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        updateColors();
    }

    public void updateColors() {
        int color2 = Theme.getColor("chat_topPanelLine");
        this.color = color2;
        this.paint.setColor(ColorUtils.setAlphaComponent(color2, (int) ((((float) Color.alpha(color2)) / 255.0f) * 112.0f)));
        this.selectedPaint.setColor(Theme.getColor("chat_topPanelLine"));
    }

    /* access modifiers changed from: private */
    public void selectPosition(int i) {
        if (this.replaceInProgress) {
            this.nextPosition = i;
            return;
        }
        if (!this.animationInProgress) {
            this.animateFromPosition = (float) this.selectedPosition;
        } else if (this.animateToPosition != i) {
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            float f = this.animateFromPosition;
            float f2 = this.animationProgress;
            this.animateFromPosition = (f * (1.0f - f2)) + (((float) this.animateToPosition) * f2);
        } else {
            return;
        }
        if (i != this.selectedPosition) {
            this.animateToPosition = i;
            this.animationInProgress = true;
            this.animationProgress = 0.0f;
            invalidate();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.animator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PinnedLineView.this.lambda$selectPosition$0$PinnedLineView(valueAnimator);
                }
            });
            this.animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    PinnedLineView pinnedLineView = PinnedLineView.this;
                    pinnedLineView.animationInProgress = false;
                    pinnedLineView.selectedPosition = pinnedLineView.animateToPosition;
                    pinnedLineView.invalidate();
                    if (PinnedLineView.this.nextPosition >= 0) {
                        PinnedLineView pinnedLineView2 = PinnedLineView.this;
                        pinnedLineView2.selectPosition(pinnedLineView2.nextPosition);
                        int unused = PinnedLineView.this.nextPosition = -1;
                    }
                }
            });
            this.animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.animator.setDuration(220);
            this.animator.start();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$selectPosition$0 */
    public /* synthetic */ void lambda$selectPosition$0$PinnedLineView(ValueAnimator valueAnimator) {
        this.animationProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        float f;
        float f2;
        Canvas canvas2 = canvas;
        super.onDraw(canvas);
        if (this.selectedPosition >= 0 && (i = this.totalCount) != 0) {
            if (this.replaceInProgress) {
                i = Math.max(this.animateFromTotal, this.animateToTotal);
            }
            boolean z = i > 3;
            if (z) {
                canvas.saveLayerAlpha(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), 255, 31);
            }
            int dp = AndroidUtilities.dp(8.0f);
            if (this.replaceInProgress) {
                float f3 = this.animationProgress;
                f = (((float) this.lineHFrom) * (1.0f - f3)) + (((float) this.lineHTo) * f3);
            } else if (this.totalCount != 0) {
                f = ((float) (getMeasuredHeight() - (dp * 2))) / ((float) Math.min(this.totalCount, 3));
            } else {
                return;
            }
            float f4 = 0.0f;
            if (f != 0.0f) {
                float dpf2 = AndroidUtilities.dpf2(0.7f);
                if (this.replaceInProgress) {
                    float f5 = this.startOffsetFrom;
                    float f6 = this.animationProgress;
                    f2 = (f5 * (1.0f - f6)) + (this.startOffsetTo * f6);
                } else {
                    if (this.animationInProgress) {
                        float f7 = this.animationProgress;
                        f2 = ((this.animateFromPosition - 1.0f) * f * (1.0f - f7)) + (((float) (this.animateToPosition - 1)) * f * f7);
                    } else {
                        f2 = ((float) (this.selectedPosition - 1)) * f;
                    }
                    if (f2 < 0.0f) {
                        f2 = 0.0f;
                    } else {
                        float f8 = (float) dp;
                        if (((((float) (this.totalCount - 1)) * f) + f8) - f2 < ((float) (getMeasuredHeight() - dp)) - f) {
                            f2 = (f8 + (((float) (this.totalCount - 1)) * f)) - (((float) (getMeasuredHeight() - dp)) - f);
                        }
                    }
                }
                float measuredWidth = ((float) getMeasuredWidth()) / 2.0f;
                float f9 = (float) dp;
                int max = Math.max(0, (int) (((f9 + f2) / f) - 1.0f));
                int min = Math.min(max + 6, this.replaceInProgress ? Math.max(this.animateFromTotal, this.animateToTotal) : this.totalCount);
                while (max < min) {
                    float var_ = ((((float) max) * f) + f9) - f2;
                    float var_ = var_ + f;
                    if (var_ >= f4 && var_ <= ((float) getMeasuredHeight())) {
                        this.rectF.set(f4, var_ + dpf2, (float) getMeasuredWidth(), var_ - dpf2);
                        boolean z2 = this.replaceInProgress;
                        if (z2 && max >= this.animateToTotal) {
                            Paint paint2 = this.paint;
                            int i2 = this.color;
                            paint2.setColor(ColorUtils.setAlphaComponent(i2, (int) ((((float) Color.alpha(i2)) / 255.0f) * 76.0f * (1.0f - this.animationProgress))));
                            canvas2.drawRoundRect(this.rectF, measuredWidth, measuredWidth, this.paint);
                            Paint paint3 = this.paint;
                            int i3 = this.color;
                            paint3.setColor(ColorUtils.setAlphaComponent(i3, (int) ((((float) Color.alpha(i3)) / 255.0f) * 76.0f)));
                        } else if (!z2 || max < this.animateFromTotal) {
                            canvas2.drawRoundRect(this.rectF, measuredWidth, measuredWidth, this.paint);
                        } else {
                            Paint paint4 = this.paint;
                            int i4 = this.color;
                            paint4.setColor(ColorUtils.setAlphaComponent(i4, (int) ((((float) Color.alpha(i4)) / 255.0f) * 76.0f * this.animationProgress)));
                            canvas2.drawRoundRect(this.rectF, measuredWidth, measuredWidth, this.paint);
                            Paint paint5 = this.paint;
                            int i5 = this.color;
                            paint5.setColor(ColorUtils.setAlphaComponent(i5, (int) ((((float) Color.alpha(i5)) / 255.0f) * 76.0f)));
                        }
                    }
                    max++;
                    f4 = 0.0f;
                }
                if (this.animationInProgress) {
                    float var_ = this.animateFromPosition;
                    float var_ = this.animationProgress;
                    float var_ = (f9 + (((var_ * (1.0f - var_)) + (((float) this.animateToPosition) * var_)) * f)) - f2;
                    this.rectF.set(0.0f, var_ + dpf2, (float) getMeasuredWidth(), (var_ + f) - dpf2);
                    canvas2.drawRoundRect(this.rectF, measuredWidth, measuredWidth, this.selectedPaint);
                } else {
                    float var_ = (f9 + (((float) this.selectedPosition) * f)) - f2;
                    this.rectF.set(0.0f, var_ + dpf2, (float) getMeasuredWidth(), (var_ + f) - dpf2);
                    canvas2.drawRoundRect(this.rectF, measuredWidth, measuredWidth, this.selectedPaint);
                }
                if (z) {
                    canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(6.0f), this.fadePaint);
                    canvas.drawRect(0.0f, (float) (getMeasuredHeight() - AndroidUtilities.dp(6.0f)), (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.fadePaint);
                    canvas2.translate(0.0f, (float) (getMeasuredHeight() - AndroidUtilities.dp(6.0f)));
                    canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(6.0f), this.fadePaint2);
                }
            }
        }
    }

    public void set(int i, int i2, boolean z) {
        int i3 = this.selectedPosition;
        if (i3 < 0 || i2 == 0 || this.totalCount == 0) {
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
        } else if (this.totalCount != i2 || Math.abs(i3 - i) > 2) {
            ValueAnimator valueAnimator2 = this.animator;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
            int dp = AndroidUtilities.dp(8.0f);
            int i4 = dp * 2;
            this.lineHFrom = (getMeasuredHeight() - i4) / Math.min(this.totalCount, 3);
            this.lineHTo = (getMeasuredHeight() - i4) / Math.min(i2, 3);
            int i5 = this.lineHFrom;
            float f = (float) ((this.selectedPosition - 1) * i5);
            this.startOffsetFrom = f;
            if (f < 0.0f) {
                this.startOffsetFrom = 0.0f;
            } else {
                int i6 = this.lineHFrom;
                if (((float) (((this.totalCount - 1) * i5) + dp)) - f < ((float) ((getMeasuredHeight() - dp) - i6))) {
                    this.startOffsetFrom = (float) ((((this.totalCount - 1) * i6) + dp) - ((getMeasuredHeight() - dp) - this.lineHFrom));
                }
            }
            int i7 = this.lineHTo;
            float f2 = (float) ((i - 1) * i7);
            this.startOffsetTo = f2;
            if (f2 < 0.0f) {
                this.startOffsetTo = 0.0f;
            } else {
                int i8 = i2 - 1;
                int i9 = this.lineHTo;
                if (((float) ((i7 * i8) + dp)) - f2 < ((float) ((getMeasuredHeight() - dp) - i9))) {
                    this.startOffsetTo = (float) (((i8 * i9) + dp) - ((getMeasuredHeight() - dp) - this.lineHTo));
                }
            }
            this.animateFromPosition = (float) this.selectedPosition;
            this.animateToPosition = i;
            this.selectedPosition = i;
            this.animateFromTotal = this.totalCount;
            this.animateToTotal = i2;
            this.totalCount = i2;
            this.replaceInProgress = true;
            this.animationInProgress = true;
            this.animationProgress = 0.0f;
            invalidate();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.animator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PinnedLineView.this.lambda$set$1$PinnedLineView(valueAnimator);
                }
            });
            this.animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    PinnedLineView pinnedLineView = PinnedLineView.this;
                    pinnedLineView.replaceInProgress = false;
                    pinnedLineView.animationInProgress = false;
                    pinnedLineView.invalidate();
                    if (PinnedLineView.this.nextPosition >= 0) {
                        PinnedLineView pinnedLineView2 = PinnedLineView.this;
                        pinnedLineView2.selectPosition(pinnedLineView2.nextPosition);
                        int unused = PinnedLineView.this.nextPosition = -1;
                    }
                }
            });
            this.animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.animator.setDuration(220);
            this.animator.start();
        } else {
            selectPosition(i);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$set$1 */
    public /* synthetic */ void lambda$set$1$PinnedLineView(ValueAnimator valueAnimator) {
        this.animationProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }
}
