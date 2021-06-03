package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.ActionBar.Theme;

public class PullForegroundDrawable {
    private ValueAnimator accentRevalAnimatorIn;
    private ValueAnimator accentRevalAnimatorOut;
    private float accentRevalProgress;
    private float accentRevalProgressOut;
    private boolean animateOut;
    private boolean animateToColorize;
    private boolean animateToEndText;
    /* access modifiers changed from: private */
    public boolean animateToTextIn;
    private boolean arrowAnimateTo;
    private final ArrowDrawable arrowDrawable;
    private ValueAnimator arrowRotateAnimator;
    private float arrowRotateProgress;
    private String avatarBackgroundColorKey = "avatar_backgroundArchivedHidden";
    private String backgroundActiveColorKey = "chats_archivePullDownBackgroundActive";
    private String backgroundColorKey = "chats_archivePullDownBackground";
    private final Paint backgroundPaint = new Paint();
    private boolean bounceIn;
    private float bounceProgress;
    private View cell;
    private boolean changeAvatarColor = true;
    private final Path circleClipPath;
    private boolean isOut;
    private RecyclerListView listView;
    private AnimatorSet outAnimator;
    public float outCx;
    public float outCy;
    public float outImageSize;
    public float outOverScroll;
    public float outProgress;
    public float outRadius;
    private final Paint paintBackgroundAccent = new Paint(1);
    private final Paint paintSecondary = new Paint(1);
    private final Paint paintWhite = new Paint(1);
    public float pullProgress;
    private String pullTooltip;
    private final RectF rectF = new RectF();
    private String releaseTooltip;
    public int scrollDy;
    /* access modifiers changed from: private */
    public float textInProgress;
    Runnable textInRunnable;
    /* access modifiers changed from: private */
    public ValueAnimator.AnimatorUpdateListener textInUpdateListener;
    /* access modifiers changed from: private */
    public ValueAnimator textIntAnimator;
    private float textSwappingProgress;
    private ValueAnimator.AnimatorUpdateListener textSwappingUpdateListener;
    private ValueAnimator textSwipingAnimator;
    private final Paint tooltipTextPaint;
    private float touchSlop;
    boolean wasSendCallback;
    private boolean willDraw;

    /* access modifiers changed from: protected */
    public float getViewOffset() {
        throw null;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$PullForegroundDrawable(ValueAnimator valueAnimator) {
        this.textSwappingProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$PullForegroundDrawable(ValueAnimator valueAnimator) {
        this.textInProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    public PullForegroundDrawable(String str, String str2) {
        TextPaint textPaint = new TextPaint(1);
        this.tooltipTextPaint = textPaint;
        this.arrowDrawable = new ArrowDrawable();
        this.circleClipPath = new Path();
        this.textSwappingProgress = 1.0f;
        this.arrowRotateProgress = 1.0f;
        this.accentRevalProgress = 1.0f;
        this.accentRevalProgressOut = 1.0f;
        this.textSwappingUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                PullForegroundDrawable.this.lambda$new$0$PullForegroundDrawable(valueAnimator);
            }
        };
        this.textInUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                PullForegroundDrawable.this.lambda$new$1$PullForegroundDrawable(valueAnimator);
            }
        };
        this.textInRunnable = new Runnable() {
            public void run() {
                boolean unused = PullForegroundDrawable.this.animateToTextIn = true;
                if (PullForegroundDrawable.this.textIntAnimator != null) {
                    PullForegroundDrawable.this.textIntAnimator.cancel();
                }
                float unused2 = PullForegroundDrawable.this.textInProgress = 0.0f;
                ValueAnimator unused3 = PullForegroundDrawable.this.textIntAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                PullForegroundDrawable.this.textIntAnimator.addUpdateListener(PullForegroundDrawable.this.textInUpdateListener);
                PullForegroundDrawable.this.textIntAnimator.setInterpolator(new LinearInterpolator());
                PullForegroundDrawable.this.textIntAnimator.setDuration(150);
                PullForegroundDrawable.this.textIntAnimator.start();
            }
        };
        this.wasSendCallback = false;
        textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        this.touchSlop = (float) ViewConfiguration.get(ApplicationLoader.applicationContext).getScaledTouchSlop();
        this.pullTooltip = str;
        this.releaseTooltip = str2;
    }

    public static int getMaxOverscroll() {
        return AndroidUtilities.dp(72.0f);
    }

    public void setCell(View view) {
        this.cell = view;
        updateColors();
    }

    public void updateColors() {
        int color = Theme.getColor(this.backgroundColorKey);
        this.tooltipTextPaint.setColor(-1);
        this.paintWhite.setColor(-1);
        this.paintSecondary.setColor(ColorUtils.setAlphaComponent(-1, 100));
        this.backgroundPaint.setColor(color);
        this.arrowDrawable.setColor(color);
        this.paintBackgroundAccent.setColor(Theme.getColor(this.avatarBackgroundColorKey));
    }

    public void setListView(RecyclerListView recyclerListView) {
        this.listView = recyclerListView;
    }

    public void drawOverScroll(Canvas canvas) {
        draw(canvas, true);
    }

    public void draw(Canvas canvas) {
        draw(canvas, false);
    }

    public void draw(Canvas canvas, boolean z) {
        int i;
        float f;
        int i2;
        int i3;
        float f2;
        int i4;
        int i5;
        int i6;
        float f3;
        float f4;
        Canvas canvas2 = canvas;
        if (this.willDraw && !this.isOut && this.cell != null && this.listView != null) {
            int dp = AndroidUtilities.dp(28.0f);
            int dp2 = AndroidUtilities.dp(8.0f);
            int dp3 = AndroidUtilities.dp(9.0f);
            int dp4 = AndroidUtilities.dp(18.0f);
            int viewOffset = (int) getViewOffset();
            float f5 = this.pullProgress;
            int height = (int) (((float) this.cell.getHeight()) * f5);
            float f6 = this.bounceIn ? (this.bounceProgress * 0.07f) - 0.05f : this.bounceProgress * 0.02f;
            updateTextProgress(f5);
            float f7 = this.outProgress * 2.0f;
            if (f7 > 1.0f) {
                f7 = 1.0f;
            }
            float f8 = this.outCx;
            float f9 = this.outCy;
            if (z) {
                f9 += (float) viewOffset;
            }
            int i7 = dp + dp3;
            int measuredHeight = (this.cell.getMeasuredHeight() - dp2) - dp3;
            if (z) {
                measuredHeight += viewOffset;
            }
            int i8 = dp4 + (dp2 * 2);
            if (height > i8) {
                i = dp3;
                f = 1.0f;
            } else {
                i = dp3;
                f = ((float) height) / ((float) i8);
            }
            canvas.save();
            if (z) {
                i3 = dp4;
                i2 = viewOffset;
                canvas2.clipRect(0, 0, this.listView.getMeasuredWidth(), viewOffset + 1);
            } else {
                i3 = dp4;
                i2 = viewOffset;
            }
            if (this.outProgress == 0.0f) {
                if (!(this.accentRevalProgress == 1.0f || this.accentRevalProgressOut == 1.0f)) {
                    canvas2.drawPaint(this.backgroundPaint);
                }
                i4 = dp2;
                f2 = f6;
            } else {
                float var_ = this.outRadius;
                float var_ = this.outRadius;
                i4 = dp2;
                float width = var_ + ((((float) this.cell.getWidth()) - var_) * (1.0f - this.outProgress)) + (var_ * f6);
                if (!(this.accentRevalProgress == 1.0f || this.accentRevalProgressOut == 1.0f)) {
                    canvas2.drawCircle(f8, f9, width, this.backgroundPaint);
                }
                this.circleClipPath.reset();
                f2 = f6;
                this.rectF.set(f8 - width, f9 - width, f8 + width, width + f9);
                this.circleClipPath.addOval(this.rectF, Path.Direction.CW);
                canvas2.clipPath(this.circleClipPath);
            }
            if (this.animateToColorize) {
                if (this.accentRevalProgressOut > this.accentRevalProgress) {
                    canvas.save();
                    float var_ = (float) i7;
                    float var_ = this.outProgress;
                    float var_ = (float) measuredHeight;
                    canvas2.translate((f8 - var_) * var_, (f9 - var_) * var_);
                    canvas2.drawCircle(var_, var_, ((float) this.cell.getWidth()) * this.accentRevalProgressOut, this.backgroundPaint);
                    canvas.restore();
                }
                if (this.accentRevalProgress > 0.0f) {
                    canvas.save();
                    float var_ = (float) i7;
                    float var_ = this.outProgress;
                    float var_ = (float) measuredHeight;
                    canvas2.translate((f8 - var_) * var_, (f9 - var_) * var_);
                    canvas2.drawCircle(var_, var_, ((float) this.cell.getWidth()) * this.accentRevalProgress, this.paintBackgroundAccent);
                    canvas.restore();
                }
            } else {
                if (this.accentRevalProgress > this.accentRevalProgressOut) {
                    canvas.save();
                    float var_ = (float) i7;
                    float var_ = this.outProgress;
                    float var_ = (float) measuredHeight;
                    canvas2.translate((f8 - var_) * var_, (f9 - var_) * var_);
                    canvas2.drawCircle(var_, var_, ((float) this.cell.getWidth()) * this.accentRevalProgress, this.paintBackgroundAccent);
                    canvas.restore();
                }
                if (this.accentRevalProgressOut > 0.0f) {
                    canvas.save();
                    float var_ = (float) i7;
                    float var_ = this.outProgress;
                    float var_ = (float) measuredHeight;
                    canvas2.translate((f8 - var_) * var_, (f9 - var_) * var_);
                    canvas2.drawCircle(var_, var_, ((float) this.cell.getWidth()) * this.accentRevalProgressOut, this.backgroundPaint);
                    canvas.restore();
                }
            }
            if (height > i8) {
                this.paintSecondary.setAlpha((int) ((1.0f - f7) * 0.4f * f * 255.0f));
                if (z) {
                    i5 = i4;
                    this.rectF.set((float) dp, (float) i5, (float) (dp + i3), (float) (i5 + i2 + i));
                } else {
                    i5 = i4;
                    this.rectF.set((float) dp, (float) (((this.cell.getHeight() - height) + i5) - i2), (float) (dp + i3), (float) (this.cell.getHeight() - i5));
                }
                i6 = i;
                float var_ = (float) i6;
                canvas2.drawRoundRect(this.rectF, var_, var_, this.paintSecondary);
            } else {
                i6 = i;
                i5 = i4;
            }
            if (z) {
                canvas.restore();
                return;
            }
            if (this.outProgress == 0.0f) {
                this.paintWhite.setAlpha((int) (f * 255.0f));
                float var_ = (float) i7;
                float var_ = (float) measuredHeight;
                canvas2.drawCircle(var_, var_, (float) i6, this.paintWhite);
                int intrinsicHeight = this.arrowDrawable.getIntrinsicHeight();
                int intrinsicWidth = this.arrowDrawable.getIntrinsicWidth() >> 1;
                f3 = f9;
                int i9 = intrinsicHeight >> 1;
                this.arrowDrawable.setBounds(i7 - intrinsicWidth, measuredHeight - i9, intrinsicWidth + i7, measuredHeight + i9);
                float var_ = 1.0f - this.arrowRotateProgress;
                if (var_ < 0.0f) {
                    var_ = 0.0f;
                }
                float var_ = 1.0f - var_;
                canvas.save();
                canvas2.rotate(180.0f * var_, var_, var_);
                canvas2.translate(0.0f, (AndroidUtilities.dpf2(1.0f) * 1.0f) - var_);
                this.arrowDrawable.setColor(this.animateToColorize ? this.paintBackgroundAccent.getColor() : Theme.getColor(this.backgroundColorKey));
                this.arrowDrawable.draw(canvas2);
                canvas.restore();
            } else {
                f3 = f9;
            }
            if (this.pullProgress > 0.0f) {
                textIn();
            }
            float height2 = (((float) this.cell.getHeight()) - (((float) i8) / 2.0f)) + ((float) AndroidUtilities.dp(6.0f));
            this.tooltipTextPaint.setAlpha((int) (this.textSwappingProgress * 255.0f * f * this.textInProgress));
            float width2 = (((float) this.cell.getWidth()) / 2.0f) - ((float) AndroidUtilities.dp(2.0f));
            float var_ = this.textSwappingProgress;
            if (var_ <= 0.0f || var_ >= 1.0f) {
                f4 = 1.0f;
            } else {
                canvas.save();
                float var_ = (this.textSwappingProgress * 0.2f) + 0.8f;
                f4 = 1.0f;
                canvas2.scale(var_, var_, width2, (((float) AndroidUtilities.dp(16.0f)) * (1.0f - this.textSwappingProgress)) + height2);
            }
            canvas2.drawText(this.pullTooltip, width2, (((float) AndroidUtilities.dp(8.0f)) * (f4 - this.textSwappingProgress)) + height2, this.tooltipTextPaint);
            float var_ = this.textSwappingProgress;
            if (var_ > 0.0f && var_ < f4) {
                canvas.restore();
            }
            float var_ = this.textSwappingProgress;
            if (var_ > 0.0f && var_ < f4) {
                canvas.save();
                float var_ = ((f4 - this.textSwappingProgress) * 0.1f) + 0.9f;
                canvas2.scale(var_, var_, width2, height2 - (((float) AndroidUtilities.dp(8.0f)) * this.textSwappingProgress));
            }
            this.tooltipTextPaint.setAlpha((int) ((1.0f - this.textSwappingProgress) * 255.0f * f * this.textInProgress));
            canvas2.drawText(this.releaseTooltip, width2, height2 - (((float) AndroidUtilities.dp(8.0f)) * this.textSwappingProgress), this.tooltipTextPaint);
            float var_ = this.textSwappingProgress;
            if (var_ > 0.0f && var_ < 1.0f) {
                canvas.restore();
            }
            canvas.restore();
            if (this.changeAvatarColor && this.outProgress > 0.0f) {
                canvas.save();
                int intrinsicWidth2 = Theme.dialogs_archiveAvatarDrawable.getIntrinsicWidth();
                int height3 = (this.cell.getHeight() - i5) - i6;
                float var_ = (float) intrinsicWidth2;
                float dp5 = ((float) AndroidUtilities.dp(24.0f)) / var_;
                float var_ = this.outProgress;
                float var_ = dp5 + ((1.0f - dp5) * var_) + f2;
                canvas2.translate((((float) i7) - f8) * (1.0f - var_), (((float) height3) - f3) * (1.0f - var_));
                float var_ = f3;
                canvas2.scale(var_, var_, f8, var_);
                Theme.dialogs_archiveAvatarDrawable.setProgress(0.0f);
                if (!Theme.dialogs_archiveAvatarDrawableRecolored) {
                    Theme.dialogs_archiveAvatarDrawable.beginApplyLayerColors();
                    Theme.dialogs_archiveAvatarDrawable.setLayerColor("Arrow1.**", Theme.getNonAnimatedColor(this.avatarBackgroundColorKey));
                    Theme.dialogs_archiveAvatarDrawable.setLayerColor("Arrow2.**", Theme.getNonAnimatedColor(this.avatarBackgroundColorKey));
                    Theme.dialogs_archiveAvatarDrawable.commitApplyLayerColors();
                    Theme.dialogs_archiveAvatarDrawableRecolored = true;
                }
                float var_ = var_ / 2.0f;
                Theme.dialogs_archiveAvatarDrawable.setBounds((int) (f8 - var_), (int) (var_ - var_), (int) (f8 + var_), (int) (var_ + var_));
                Theme.dialogs_archiveAvatarDrawable.draw(canvas2);
                canvas.restore();
            }
        }
    }

    private void updateTextProgress(float f) {
        boolean z = f > 0.85f;
        float f2 = 1.0f;
        if (this.animateToEndText != z) {
            this.animateToEndText = z;
            if (this.textInProgress == 0.0f) {
                ValueAnimator valueAnimator = this.textSwipingAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                this.textSwappingProgress = z ? 0.0f : 1.0f;
            } else {
                ValueAnimator valueAnimator2 = this.textSwipingAnimator;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                }
                float[] fArr = new float[2];
                fArr[0] = this.textSwappingProgress;
                fArr[1] = z ? 0.0f : 1.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.textSwipingAnimator = ofFloat;
                ofFloat.addUpdateListener(this.textSwappingUpdateListener);
                this.textSwipingAnimator.setInterpolator(new LinearInterpolator());
                this.textSwipingAnimator.setDuration(170);
                this.textSwipingAnimator.start();
            }
        }
        if (z != this.arrowAnimateTo) {
            this.arrowAnimateTo = z;
            ValueAnimator valueAnimator3 = this.arrowRotateAnimator;
            if (valueAnimator3 != null) {
                valueAnimator3.cancel();
            }
            float[] fArr2 = new float[2];
            fArr2[0] = this.arrowRotateProgress;
            if (this.arrowAnimateTo) {
                f2 = 0.0f;
            }
            fArr2[1] = f2;
            ValueAnimator ofFloat2 = ValueAnimator.ofFloat(fArr2);
            this.arrowRotateAnimator = ofFloat2;
            ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PullForegroundDrawable.this.lambda$updateTextProgress$2$PullForegroundDrawable(valueAnimator);
                }
            });
            this.arrowRotateAnimator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            this.arrowRotateAnimator.setDuration(250);
            this.arrowRotateAnimator.start();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateTextProgress$2 */
    public /* synthetic */ void lambda$updateTextProgress$2$PullForegroundDrawable(ValueAnimator valueAnimator) {
        this.arrowRotateProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    public void colorize(boolean z) {
        if (this.animateToColorize != z) {
            this.animateToColorize = z;
            if (z) {
                ValueAnimator valueAnimator = this.accentRevalAnimatorIn;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.accentRevalAnimatorIn = null;
                }
                this.accentRevalProgress = 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.accentRevalAnimatorIn = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        PullForegroundDrawable.this.lambda$colorize$3$PullForegroundDrawable(valueAnimator);
                    }
                });
                this.accentRevalAnimatorIn.setInterpolator(AndroidUtilities.accelerateInterpolator);
                this.accentRevalAnimatorIn.setDuration(230);
                this.accentRevalAnimatorIn.start();
                return;
            }
            ValueAnimator valueAnimator2 = this.accentRevalAnimatorOut;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
                this.accentRevalAnimatorOut = null;
            }
            this.accentRevalProgressOut = 0.0f;
            ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.accentRevalAnimatorOut = ofFloat2;
            ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PullForegroundDrawable.this.lambda$colorize$4$PullForegroundDrawable(valueAnimator);
                }
            });
            this.accentRevalAnimatorOut.setInterpolator(AndroidUtilities.accelerateInterpolator);
            this.accentRevalAnimatorOut.setDuration(230);
            this.accentRevalAnimatorOut.start();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$colorize$3 */
    public /* synthetic */ void lambda$colorize$3$PullForegroundDrawable(ValueAnimator valueAnimator) {
        this.accentRevalProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.invalidate();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$colorize$4 */
    public /* synthetic */ void lambda$colorize$4$PullForegroundDrawable(ValueAnimator valueAnimator) {
        this.accentRevalProgressOut = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.invalidate();
        }
    }

    private void textIn() {
        if (this.animateToTextIn) {
            return;
        }
        if (((float) Math.abs(this.scrollDy)) >= this.touchSlop * 0.5f) {
            this.wasSendCallback = true;
            this.cell.removeCallbacks(this.textInRunnable);
            this.cell.postDelayed(this.textInRunnable, 200);
        } else if (!this.wasSendCallback) {
            this.textInProgress = 1.0f;
            this.animateToTextIn = true;
        }
    }

    public void startOutAnimation() {
        if (!this.animateOut && this.listView != null) {
            AnimatorSet animatorSet = this.outAnimator;
            if (animatorSet != null) {
                animatorSet.removeAllListeners();
                this.outAnimator.cancel();
            }
            this.animateOut = true;
            this.bounceIn = true;
            this.bounceProgress = 0.0f;
            this.outOverScroll = this.listView.getTranslationY() / ((float) AndroidUtilities.dp(100.0f));
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PullForegroundDrawable.this.lambda$startOutAnimation$5$PullForegroundDrawable(valueAnimator);
                }
            });
            ofFloat.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            ofFloat.setDuration(250);
            ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PullForegroundDrawable.this.lambda$startOutAnimation$6$PullForegroundDrawable(valueAnimator);
                }
            });
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_BOTH;
            ofFloat2.setInterpolator(cubicBezierInterpolator);
            ofFloat2.setDuration(150);
            ValueAnimator ofFloat3 = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
            ofFloat3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PullForegroundDrawable.this.lambda$startOutAnimation$7$PullForegroundDrawable(valueAnimator);
                }
            });
            ofFloat3.setInterpolator(cubicBezierInterpolator);
            ofFloat3.setDuration(135);
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.outAnimator = animatorSet2;
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    PullForegroundDrawable.this.doNotShow();
                }
            });
            AnimatorSet animatorSet3 = new AnimatorSet();
            animatorSet3.playSequentially(new Animator[]{ofFloat2, ofFloat3});
            animatorSet3.setStartDelay(180);
            this.outAnimator.playTogether(new Animator[]{ofFloat, animatorSet3});
            this.outAnimator.start();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startOutAnimation$5 */
    public /* synthetic */ void lambda$startOutAnimation$5$PullForegroundDrawable(ValueAnimator valueAnimator) {
        setOutProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startOutAnimation$6 */
    public /* synthetic */ void lambda$startOutAnimation$6$PullForegroundDrawable(ValueAnimator valueAnimator) {
        this.bounceProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.bounceIn = true;
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startOutAnimation$7 */
    public /* synthetic */ void lambda$startOutAnimation$7$PullForegroundDrawable(ValueAnimator valueAnimator) {
        this.bounceProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.bounceIn = false;
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    private void setOutProgress(float f) {
        this.outProgress = f;
        int blendARGB = ColorUtils.blendARGB(Theme.getNonAnimatedColor(this.avatarBackgroundColorKey), Theme.getNonAnimatedColor(this.backgroundActiveColorKey), 1.0f - this.outProgress);
        this.paintBackgroundAccent.setColor(blendARGB);
        if (this.changeAvatarColor && isDraw()) {
            Theme.dialogs_archiveAvatarDrawable.beginApplyLayerColors();
            Theme.dialogs_archiveAvatarDrawable.setLayerColor("Arrow1.**", blendARGB);
            Theme.dialogs_archiveAvatarDrawable.setLayerColor("Arrow2.**", blendARGB);
            Theme.dialogs_archiveAvatarDrawable.commitApplyLayerColors();
        }
    }

    public void doNotShow() {
        ValueAnimator valueAnimator = this.textSwipingAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator valueAnimator2 = this.textIntAnimator;
        if (valueAnimator2 != null) {
            valueAnimator2.cancel();
        }
        View view = this.cell;
        if (view != null) {
            view.removeCallbacks(this.textInRunnable);
        }
        ValueAnimator valueAnimator3 = this.accentRevalAnimatorIn;
        if (valueAnimator3 != null) {
            valueAnimator3.cancel();
        }
        this.textSwappingProgress = 1.0f;
        this.arrowRotateProgress = 1.0f;
        this.animateToEndText = false;
        this.arrowAnimateTo = false;
        this.animateToTextIn = false;
        this.wasSendCallback = false;
        this.textInProgress = 0.0f;
        this.isOut = true;
        setOutProgress(1.0f);
        this.animateToColorize = false;
        this.accentRevalProgress = 0.0f;
    }

    public void showHidden() {
        AnimatorSet animatorSet = this.outAnimator;
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            this.outAnimator.cancel();
        }
        setOutProgress(0.0f);
        this.isOut = false;
        this.animateOut = false;
    }

    public boolean isDraw() {
        return this.willDraw && !this.isOut;
    }

    public void setWillDraw(boolean z) {
        this.willDraw = z;
    }

    public void resetText() {
        ValueAnimator valueAnimator = this.textIntAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        View view = this.cell;
        if (view != null) {
            view.removeCallbacks(this.textInRunnable);
        }
        this.textInProgress = 0.0f;
        this.animateToTextIn = false;
        this.wasSendCallback = false;
    }

    private class ArrowDrawable extends Drawable {
        private float lastDensity;
        private Paint paint = new Paint(1);
        private Path path = new Path();

        public int getOpacity() {
            return 0;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public ArrowDrawable() {
            updatePath();
        }

        private void updatePath() {
            int dp = AndroidUtilities.dp(18.0f);
            this.path.reset();
            float f = (float) (dp >> 1);
            this.path.moveTo(f, AndroidUtilities.dpf2(4.98f));
            this.path.lineTo(AndroidUtilities.dpf2(4.95f), AndroidUtilities.dpf2(9.0f));
            this.path.lineTo(((float) dp) - AndroidUtilities.dpf2(4.95f), AndroidUtilities.dpf2(9.0f));
            this.path.lineTo(f, AndroidUtilities.dpf2(4.98f));
            this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
            this.paint.setStrokeJoin(Paint.Join.ROUND);
            this.paint.setStrokeWidth(AndroidUtilities.dpf2(1.0f));
            this.lastDensity = AndroidUtilities.density;
        }

        public void setColor(int i) {
            this.paint.setColor(i);
        }

        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(18.0f);
        }

        public int getIntrinsicWidth() {
            return getIntrinsicHeight();
        }

        public void draw(Canvas canvas) {
            if (this.lastDensity != AndroidUtilities.density) {
                updatePath();
            }
            canvas.save();
            canvas.translate((float) getBounds().left, (float) getBounds().top);
            canvas.drawPath(this.path, this.paint);
            Canvas canvas2 = canvas;
            canvas2.drawRect(AndroidUtilities.dpf2(7.56f), AndroidUtilities.dpf2(8.0f), ((float) AndroidUtilities.dp(18.0f)) - AndroidUtilities.dpf2(7.56f), AndroidUtilities.dpf2(11.1f), this.paint);
            canvas.restore();
        }
    }
}
