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
    private final Theme.ResourcesProvider resourcesProvider;
    Paint selectedPaint = new Paint(1);
    int selectedPosition = -1;
    private float startOffsetFrom;
    private float startOffsetTo;
    int totalCount = 0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PinnedLineView(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.selectedPaint.setStyle(Paint.Style.FILL);
        this.selectedPaint.setStrokeCap(Paint.Cap.ROUND);
        this.fadePaint = new Paint();
        this.fadePaint.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, (float) AndroidUtilities.dp(6.0f), new int[]{-1, 0}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
        this.fadePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        this.fadePaint2 = new Paint();
        Paint paint2 = this.fadePaint2;
        paint2.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, (float) AndroidUtilities.dp(6.0f), new int[]{0, -1}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
        this.fadePaint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        updateColors();
    }

    public void updateColors() {
        int themedColor = getThemedColor("chat_topPanelLine");
        this.color = themedColor;
        this.paint.setColor(ColorUtils.setAlphaComponent(themedColor, (int) ((((float) Color.alpha(themedColor)) / 255.0f) * 112.0f)));
        this.selectedPaint.setColor(this.color);
    }

    /* access modifiers changed from: private */
    public void selectPosition(int position) {
        if (this.replaceInProgress) {
            this.nextPosition = position;
            return;
        }
        if (!this.animationInProgress) {
            this.animateFromPosition = (float) this.selectedPosition;
        } else if (this.animateToPosition != position) {
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
        if (position != this.selectedPosition) {
            this.animateToPosition = position;
            this.animationInProgress = true;
            this.animationProgress = 0.0f;
            invalidate();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.animator = ofFloat;
            ofFloat.addUpdateListener(new PinnedLineView$$ExternalSyntheticLambda0(this));
            this.animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    PinnedLineView.this.animationInProgress = false;
                    PinnedLineView pinnedLineView = PinnedLineView.this;
                    pinnedLineView.selectedPosition = pinnedLineView.animateToPosition;
                    PinnedLineView.this.invalidate();
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

    /* renamed from: lambda$selectPosition$0$org-telegram-ui-Components-PinnedLineView  reason: not valid java name */
    public /* synthetic */ void m2501xd2955f1c(ValueAnimator valueAnimator) {
        this.animationProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        float lineH;
        float startOffset;
        float offset1;
        Canvas canvas2 = canvas;
        super.onDraw(canvas);
        if (this.selectedPosition >= 0 && (i = this.totalCount) != 0) {
            if (this.replaceInProgress) {
                i = Math.max(this.animateFromTotal, this.animateToTotal);
            }
            boolean drawFade = i > 3;
            if (drawFade) {
                canvas.saveLayerAlpha(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), 255, 31);
            }
            int viewPadding = AndroidUtilities.dp(8.0f);
            if (this.replaceInProgress) {
                float f = this.animationProgress;
                lineH = (((float) this.lineHFrom) * (1.0f - f)) + (((float) this.lineHTo) * f);
            } else if (this.totalCount != 0) {
                lineH = ((float) (getMeasuredHeight() - (viewPadding * 2))) / ((float) Math.min(this.totalCount, 3));
            } else {
                return;
            }
            float f2 = 0.0f;
            if (lineH != 0.0f) {
                float linePadding = AndroidUtilities.dpf2(0.7f);
                if (this.replaceInProgress) {
                    float f3 = this.startOffsetFrom;
                    float f4 = this.animationProgress;
                    startOffset = (f3 * (1.0f - f4)) + (this.startOffsetTo * f4);
                } else {
                    if (this.animationInProgress) {
                        float f5 = this.animationProgress;
                        offset1 = ((1.0f - f5) * (this.animateFromPosition - 1.0f) * lineH) + (f5 * ((float) (this.animateToPosition - 1)) * lineH);
                    } else {
                        offset1 = ((float) (this.selectedPosition - 1)) * lineH;
                    }
                    if (offset1 < 0.0f) {
                        startOffset = 0.0f;
                    } else if ((((float) viewPadding) + (((float) (this.totalCount - 1)) * lineH)) - offset1 < ((float) (getMeasuredHeight() - viewPadding)) - lineH) {
                        startOffset = (((float) viewPadding) + (((float) (this.totalCount - 1)) * lineH)) - (((float) (getMeasuredHeight() - viewPadding)) - lineH);
                    } else {
                        startOffset = offset1;
                    }
                }
                float r = ((float) getMeasuredWidth()) / 2.0f;
                int start = Math.max(0, (int) (((((float) viewPadding) + startOffset) / lineH) - 1.0f));
                int end = Math.min(start + 6, this.replaceInProgress ? Math.max(this.animateFromTotal, this.animateToTotal) : this.totalCount);
                int i2 = start;
                while (i2 < end) {
                    float startY = (((float) viewPadding) + (((float) i2) * lineH)) - startOffset;
                    if (startY + lineH < f2) {
                    } else if (startY <= ((float) getMeasuredHeight())) {
                        float f6 = startY;
                        this.rectF.set(f2, startY + linePadding, (float) getMeasuredWidth(), (startY + lineH) - linePadding);
                        boolean z = this.replaceInProgress;
                        if (z && i2 >= this.animateToTotal) {
                            Paint paint2 = this.paint;
                            int i3 = this.color;
                            paint2.setColor(ColorUtils.setAlphaComponent(i3, (int) ((((float) Color.alpha(i3)) / 255.0f) * 76.0f * (1.0f - this.animationProgress))));
                            canvas2.drawRoundRect(this.rectF, r, r, this.paint);
                            Paint paint3 = this.paint;
                            int i4 = this.color;
                            paint3.setColor(ColorUtils.setAlphaComponent(i4, (int) ((((float) Color.alpha(i4)) / 255.0f) * 76.0f)));
                        } else if (!z || i2 < this.animateFromTotal) {
                            canvas2.drawRoundRect(this.rectF, r, r, this.paint);
                        } else {
                            Paint paint4 = this.paint;
                            int i5 = this.color;
                            paint4.setColor(ColorUtils.setAlphaComponent(i5, (int) ((((float) Color.alpha(i5)) / 255.0f) * 76.0f * this.animationProgress)));
                            canvas2.drawRoundRect(this.rectF, r, r, this.paint);
                            Paint paint5 = this.paint;
                            int i6 = this.color;
                            paint5.setColor(ColorUtils.setAlphaComponent(i6, (int) ((((float) Color.alpha(i6)) / 255.0f) * 76.0f)));
                        }
                    }
                    i2++;
                    f2 = 0.0f;
                }
                if (this.animationInProgress != 0) {
                    float f7 = this.animateFromPosition;
                    float f8 = this.animationProgress;
                    float startY2 = (((float) viewPadding) + (((f7 * (1.0f - f8)) + (((float) this.animateToPosition) * f8)) * lineH)) - startOffset;
                    this.rectF.set(0.0f, startY2 + linePadding, (float) getMeasuredWidth(), (startY2 + lineH) - linePadding);
                    canvas2.drawRoundRect(this.rectF, r, r, this.selectedPaint);
                } else {
                    float startY3 = (((float) viewPadding) + (((float) this.selectedPosition) * lineH)) - startOffset;
                    this.rectF.set(0.0f, startY3 + linePadding, (float) getMeasuredWidth(), (startY3 + lineH) - linePadding);
                    canvas2.drawRoundRect(this.rectF, r, r, this.selectedPaint);
                }
                if (drawFade) {
                    int i7 = end;
                    canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(6.0f), this.fadePaint);
                    canvas.drawRect(0.0f, (float) (getMeasuredHeight() - AndroidUtilities.dp(6.0f)), (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.fadePaint);
                    canvas2.translate(0.0f, (float) (getMeasuredHeight() - AndroidUtilities.dp(6.0f)));
                    canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(6.0f), this.fadePaint2);
                    return;
                }
            }
        }
    }

    public void set(int position, int totalCount2, boolean animated) {
        int i = this.selectedPosition;
        if (i < 0 || totalCount2 == 0 || this.totalCount == 0) {
            animated = false;
        }
        if (!animated) {
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.selectedPosition = position;
            this.totalCount = totalCount2;
            invalidate();
        } else if (this.totalCount != totalCount2 || (Math.abs(i - position) > 2 && !this.animationInProgress && !this.replaceInProgress)) {
            ValueAnimator valueAnimator2 = this.animator;
            if (valueAnimator2 != null) {
                this.nextPosition = 0;
                valueAnimator2.cancel();
            }
            int viewPadding = AndroidUtilities.dp(8.0f);
            this.lineHFrom = (getMeasuredHeight() - (viewPadding * 2)) / Math.min(this.totalCount, 3);
            this.lineHTo = (getMeasuredHeight() - (viewPadding * 2)) / Math.min(totalCount2, 3);
            int i2 = this.lineHFrom;
            float f = (float) ((this.selectedPosition - 1) * i2);
            this.startOffsetFrom = f;
            if (f < 0.0f) {
                this.startOffsetFrom = 0.0f;
            } else {
                int i3 = this.lineHFrom;
                if (((float) (((this.totalCount - 1) * i2) + viewPadding)) - f < ((float) ((getMeasuredHeight() - viewPadding) - i3))) {
                    this.startOffsetFrom = (float) ((((this.totalCount - 1) * i3) + viewPadding) - ((getMeasuredHeight() - viewPadding) - this.lineHFrom));
                }
            }
            int i4 = this.lineHTo;
            float f2 = (float) ((position - 1) * i4);
            this.startOffsetTo = f2;
            if (f2 < 0.0f) {
                this.startOffsetTo = 0.0f;
            } else {
                int i5 = this.lineHTo;
                if (((float) (((totalCount2 - 1) * i4) + viewPadding)) - f2 < ((float) ((getMeasuredHeight() - viewPadding) - i5))) {
                    this.startOffsetTo = (float) ((((totalCount2 - 1) * i5) + viewPadding) - ((getMeasuredHeight() - viewPadding) - this.lineHTo));
                }
            }
            this.animateFromPosition = (float) this.selectedPosition;
            this.animateToPosition = position;
            this.selectedPosition = position;
            this.animateFromTotal = this.totalCount;
            this.animateToTotal = totalCount2;
            this.totalCount = totalCount2;
            this.replaceInProgress = true;
            this.animationInProgress = true;
            this.animationProgress = 0.0f;
            invalidate();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.animator = ofFloat;
            ofFloat.addUpdateListener(new PinnedLineView$$ExternalSyntheticLambda1(this));
            this.animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    PinnedLineView.this.replaceInProgress = false;
                    PinnedLineView.this.animationInProgress = false;
                    PinnedLineView.this.invalidate();
                    if (PinnedLineView.this.nextPosition >= 0) {
                        PinnedLineView pinnedLineView = PinnedLineView.this;
                        pinnedLineView.selectPosition(pinnedLineView.nextPosition);
                        int unused = PinnedLineView.this.nextPosition = -1;
                    }
                }
            });
            this.animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.animator.setDuration(220);
            this.animator.start();
        } else {
            selectPosition(position);
        }
    }

    /* renamed from: lambda$set$1$org-telegram-ui-Components-PinnedLineView  reason: not valid java name */
    public /* synthetic */ void m2502lambda$set$1$orgtelegramuiComponentsPinnedLineView(ValueAnimator valueAnimator) {
        this.animationProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color2 = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color2 != null ? color2.intValue() : Theme.getColor(key);
    }
}
