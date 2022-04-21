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
    public static final float SNAP_HEIGHT = 0.85f;
    public static final float endPullParallax = 0.25f;
    public static final long minPullingTime = 200;
    public static final float startPullOverScroll = 0.2f;
    public static final float startPullParallax = 0.45f;
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

    /* renamed from: lambda$new$0$org-telegram-ui-Components-PullForegroundDrawable  reason: not valid java name */
    public /* synthetic */ void m4256lambda$new$0$orgtelegramuiComponentsPullForegroundDrawable(ValueAnimator animation) {
        this.textSwappingProgress = ((Float) animation.getAnimatedValue()).floatValue();
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-PullForegroundDrawable  reason: not valid java name */
    public /* synthetic */ void m4257lambda$new$1$orgtelegramuiComponentsPullForegroundDrawable(ValueAnimator animation) {
        this.textInProgress = ((Float) animation.getAnimatedValue()).floatValue();
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    public PullForegroundDrawable(String pullText, String releaseText) {
        TextPaint textPaint = new TextPaint(1);
        this.tooltipTextPaint = textPaint;
        this.arrowDrawable = new ArrowDrawable();
        this.circleClipPath = new Path();
        this.textSwappingProgress = 1.0f;
        this.arrowRotateProgress = 1.0f;
        this.accentRevalProgress = 1.0f;
        this.accentRevalProgressOut = 1.0f;
        this.textSwappingUpdateListener = new PullForegroundDrawable$$ExternalSyntheticLambda2(this);
        this.textInUpdateListener = new PullForegroundDrawable$$ExternalSyntheticLambda3(this);
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
        this.pullTooltip = pullText;
        this.releaseTooltip = releaseText;
    }

    public static int getMaxOverscroll() {
        return AndroidUtilities.dp(72.0f);
    }

    public void setColors(String background, String active) {
        this.backgroundColorKey = background;
        this.backgroundActiveColorKey = active;
        this.changeAvatarColor = false;
        updateColors();
    }

    public void setCell(View view) {
        this.cell = view;
        updateColors();
    }

    public void updateColors() {
        int backgroundColor = Theme.getColor(this.backgroundColorKey);
        this.tooltipTextPaint.setColor(-1);
        this.paintWhite.setColor(-1);
        this.paintSecondary.setColor(ColorUtils.setAlphaComponent(-1, 100));
        this.backgroundPaint.setColor(backgroundColor);
        this.arrowDrawable.setColor(backgroundColor);
        this.paintBackgroundAccent.setColor(Theme.getColor(this.avatarBackgroundColorKey));
    }

    public void setListView(RecyclerListView listView2) {
        this.listView = listView2;
    }

    public void drawOverScroll(Canvas canvas) {
        draw(canvas, true);
    }

    public void draw(Canvas canvas) {
        draw(canvas, false);
    }

    /* access modifiers changed from: protected */
    public float getViewOffset() {
        return 0.0f;
    }

    public void draw(Canvas canvas, boolean header) {
        int overscroll;
        int radius;
        float startPullProgress;
        float bounceP;
        int startPadding;
        int startPadding2;
        int radius2;
        Canvas canvas2 = canvas;
        if (this.willDraw && !this.isOut && this.cell != null && this.listView != null) {
            int startPadding3 = AndroidUtilities.dp(28.0f);
            int smallMargin = AndroidUtilities.dp(8.0f);
            int radius3 = AndroidUtilities.dp(9.0f);
            int diameter = AndroidUtilities.dp(18.0f);
            int overscroll2 = (int) getViewOffset();
            float f = this.pullProgress;
            int visibleHeight = (int) (((float) this.cell.getHeight()) * f);
            float bounceP2 = this.bounceIn ? (this.bounceProgress * 0.07f) - 0.05f : this.bounceProgress * 0.02f;
            updateTextProgress(f);
            float outProgressHalf = this.outProgress * 2.0f;
            if (outProgressHalf > 1.0f) {
                outProgressHalf = 1.0f;
            }
            float cX = this.outCx;
            float cY = this.outCy;
            if (header) {
                cY += (float) overscroll2;
            }
            int smallCircleX = startPadding3 + radius3;
            int smallCircleY = (this.cell.getMeasuredHeight() - smallMargin) - radius3;
            if (header) {
                smallCircleY += overscroll2;
            }
            float startPullProgress2 = visibleHeight > diameter + (smallMargin * 2) ? 1.0f : ((float) visibleHeight) / ((float) (diameter + (smallMargin * 2)));
            canvas.save();
            if (header) {
                radius = radius3;
                overscroll = overscroll2;
                canvas2.clipRect(0, 0, this.listView.getMeasuredWidth(), overscroll2 + 1);
            } else {
                radius = radius3;
                overscroll = overscroll2;
            }
            if (this.outProgress != 0.0f) {
                float f2 = this.outRadius;
                float f3 = this.outRadius;
                startPadding = startPadding3;
                float outBackgroundRadius = f2 + ((((float) this.cell.getWidth()) - f3) * (1.0f - this.outProgress)) + (f3 * bounceP2);
                if (!(this.accentRevalProgress == 1.0f || this.accentRevalProgressOut == 1.0f)) {
                    canvas2.drawCircle(cX, cY, outBackgroundRadius, this.backgroundPaint);
                }
                this.circleClipPath.reset();
                bounceP = bounceP2;
                startPullProgress = startPullProgress2;
                this.rectF.set(cX - outBackgroundRadius, cY - outBackgroundRadius, cX + outBackgroundRadius, cY + outBackgroundRadius);
                this.circleClipPath.addOval(this.rectF, Path.Direction.CW);
                canvas2.clipPath(this.circleClipPath);
            } else if (this.accentRevalProgress == 1.0f || this.accentRevalProgressOut == 1.0f) {
                startPadding = startPadding3;
                bounceP = bounceP2;
                startPullProgress = startPullProgress2;
            } else {
                canvas2.drawPaint(this.backgroundPaint);
                startPadding = startPadding3;
                bounceP = bounceP2;
                startPullProgress = startPullProgress2;
            }
            if (this.animateToColorize) {
                if (this.accentRevalProgressOut > this.accentRevalProgress) {
                    canvas.save();
                    float f4 = this.outProgress;
                    canvas2.translate((cX - ((float) smallCircleX)) * f4, (cY - ((float) smallCircleY)) * f4);
                    canvas2.drawCircle((float) smallCircleX, (float) smallCircleY, ((float) this.cell.getWidth()) * this.accentRevalProgressOut, this.backgroundPaint);
                    canvas.restore();
                }
                if (this.accentRevalProgress > 0.0f) {
                    canvas.save();
                    float f5 = this.outProgress;
                    canvas2.translate((cX - ((float) smallCircleX)) * f5, (cY - ((float) smallCircleY)) * f5);
                    canvas2.drawCircle((float) smallCircleX, (float) smallCircleY, ((float) this.cell.getWidth()) * this.accentRevalProgress, this.paintBackgroundAccent);
                    canvas.restore();
                }
            } else {
                if (this.accentRevalProgress > this.accentRevalProgressOut) {
                    canvas.save();
                    float f6 = this.outProgress;
                    canvas2.translate((cX - ((float) smallCircleX)) * f6, (cY - ((float) smallCircleY)) * f6);
                    canvas2.drawCircle((float) smallCircleX, (float) smallCircleY, ((float) this.cell.getWidth()) * this.accentRevalProgress, this.paintBackgroundAccent);
                    canvas.restore();
                }
                if (this.accentRevalProgressOut > 0.0f) {
                    canvas.save();
                    float f7 = this.outProgress;
                    canvas2.translate((cX - ((float) smallCircleX)) * f7, (cY - ((float) smallCircleY)) * f7);
                    canvas2.drawCircle((float) smallCircleX, (float) smallCircleY, ((float) this.cell.getWidth()) * this.accentRevalProgressOut, this.backgroundPaint);
                    canvas.restore();
                }
            }
            if (visibleHeight > (smallMargin * 2) + diameter) {
                this.paintSecondary.setAlpha((int) ((1.0f - outProgressHalf) * 0.4f * startPullProgress * 255.0f));
                if (header) {
                    startPadding2 = startPadding;
                    this.rectF.set((float) startPadding2, (float) smallMargin, (float) (startPadding2 + diameter), (float) (smallMargin + overscroll + radius));
                } else {
                    startPadding2 = startPadding;
                    this.rectF.set((float) startPadding2, (float) (((this.cell.getHeight() - visibleHeight) + smallMargin) - overscroll), (float) (startPadding2 + diameter), (float) (this.cell.getHeight() - smallMargin));
                }
                radius2 = radius;
                canvas2.drawRoundRect(this.rectF, (float) radius2, (float) radius2, this.paintSecondary);
            } else {
                radius2 = radius;
                startPadding2 = startPadding;
            }
            if (header) {
                canvas.restore();
                return;
            }
            if (this.outProgress == 0.0f) {
                this.paintWhite.setAlpha((int) (startPullProgress * 255.0f));
                canvas2.drawCircle((float) smallCircleX, (float) smallCircleY, (float) radius2, this.paintWhite);
                int ih = this.arrowDrawable.getIntrinsicHeight();
                int iw = this.arrowDrawable.getIntrinsicWidth();
                int i = visibleHeight;
                float f8 = outProgressHalf;
                int i2 = ih;
                this.arrowDrawable.setBounds(smallCircleX - (iw >> 1), smallCircleY - (ih >> 1), smallCircleX + (iw >> 1), smallCircleY + (ih >> 1));
                float rotateProgress = 1.0f - this.arrowRotateProgress;
                if (rotateProgress < 0.0f) {
                    rotateProgress = 0.0f;
                }
                float rotateProgress2 = 1.0f - rotateProgress;
                canvas.save();
                canvas2.rotate(180.0f * rotateProgress2, (float) smallCircleX, (float) smallCircleY);
                canvas2.translate(0.0f, (AndroidUtilities.dpf2(1.0f) * 1.0f) - rotateProgress2);
                this.arrowDrawable.setColor(this.animateToColorize ? this.paintBackgroundAccent.getColor() : Theme.getColor(this.backgroundColorKey));
                this.arrowDrawable.draw(canvas2);
                canvas.restore();
            } else {
                float f9 = outProgressHalf;
            }
            if (this.pullProgress > 0.0f) {
                textIn();
            }
            float textY = (((float) this.cell.getHeight()) - (((float) ((smallMargin * 2) + diameter)) / 2.0f)) + ((float) AndroidUtilities.dp(6.0f));
            this.tooltipTextPaint.setAlpha((int) (this.textSwappingProgress * 255.0f * startPullProgress * this.textInProgress));
            float textCx = (((float) this.cell.getWidth()) / 2.0f) - ((float) AndroidUtilities.dp(2.0f));
            float var_ = this.textSwappingProgress;
            if (var_ > 0.0f && var_ < 1.0f) {
                canvas.save();
                float scale = (this.textSwappingProgress * 0.2f) + 0.8f;
                canvas2.scale(scale, scale, textCx, (((float) AndroidUtilities.dp(16.0f)) * (1.0f - this.textSwappingProgress)) + textY);
            }
            canvas2.drawText(this.pullTooltip, textCx, (((float) AndroidUtilities.dp(8.0f)) * (1.0f - this.textSwappingProgress)) + textY, this.tooltipTextPaint);
            float var_ = this.textSwappingProgress;
            if (var_ > 0.0f && var_ < 1.0f) {
                canvas.restore();
            }
            float var_ = this.textSwappingProgress;
            if (var_ > 0.0f && var_ < 1.0f) {
                canvas.save();
                float scale2 = ((1.0f - this.textSwappingProgress) * 0.1f) + 0.9f;
                canvas2.scale(scale2, scale2, textCx, textY - (((float) AndroidUtilities.dp(8.0f)) * this.textSwappingProgress));
            }
            this.tooltipTextPaint.setAlpha((int) ((1.0f - this.textSwappingProgress) * 255.0f * startPullProgress * this.textInProgress));
            canvas2.drawText(this.releaseTooltip, textCx, textY - (((float) AndroidUtilities.dp(8.0f)) * this.textSwappingProgress), this.tooltipTextPaint);
            float var_ = this.textSwappingProgress;
            if (var_ > 0.0f && var_ < 1.0f) {
                canvas.restore();
            }
            canvas.restore();
            if (!this.changeAvatarColor || this.outProgress <= 0.0f) {
                int i3 = smallCircleY;
                int i4 = smallMargin;
                int i5 = radius2;
                int i6 = diameter;
                return;
            }
            canvas.save();
            int iw2 = Theme.dialogs_archiveAvatarDrawable.getIntrinsicWidth();
            float var_ = textY;
            float scaleStart = ((float) AndroidUtilities.dp(24.0f)) / ((float) iw2);
            float var_ = this.outProgress;
            int i7 = smallCircleY;
            float scale3 = scaleStart + ((1.0f - scaleStart) * var_) + bounceP;
            int i8 = smallMargin;
            int i9 = (int) cX;
            int i10 = (int) cY;
            int i11 = radius2;
            canvas2.translate((((float) (startPadding2 + radius2)) - cX) * (1.0f - var_), (((float) ((this.cell.getHeight() - smallMargin) - radius2)) - cY) * (1.0f - var_));
            canvas2.scale(scale3, scale3, cX, cY);
            Theme.dialogs_archiveAvatarDrawable.setProgress(0.0f);
            if (!Theme.dialogs_archiveAvatarDrawableRecolored) {
                Theme.dialogs_archiveAvatarDrawable.beginApplyLayerColors();
                Theme.dialogs_archiveAvatarDrawable.setLayerColor("Arrow1.**", Theme.getNonAnimatedColor(this.avatarBackgroundColorKey));
                Theme.dialogs_archiveAvatarDrawable.setLayerColor("Arrow2.**", Theme.getNonAnimatedColor(this.avatarBackgroundColorKey));
                Theme.dialogs_archiveAvatarDrawable.commitApplyLayerColors();
                Theme.dialogs_archiveAvatarDrawableRecolored = true;
            }
            float var_ = scale3;
            int i12 = diameter;
            Theme.dialogs_archiveAvatarDrawable.setBounds((int) (cX - (((float) iw2) / 2.0f)), (int) (cY - (((float) iw2) / 2.0f)), (int) ((((float) iw2) / 2.0f) + cX), (int) ((((float) iw2) / 2.0f) + cY));
            Theme.dialogs_archiveAvatarDrawable.draw(canvas2);
            canvas.restore();
        }
    }

    private void updateTextProgress(float pullProgress2) {
        boolean endText = pullProgress2 > 0.85f;
        float f = 1.0f;
        if (this.animateToEndText != endText) {
            this.animateToEndText = endText;
            if (this.textInProgress == 0.0f) {
                ValueAnimator valueAnimator = this.textSwipingAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                this.textSwappingProgress = endText ? 0.0f : 1.0f;
            } else {
                ValueAnimator valueAnimator2 = this.textSwipingAnimator;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                }
                float[] fArr = new float[2];
                fArr[0] = this.textSwappingProgress;
                fArr[1] = endText ? 0.0f : 1.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.textSwipingAnimator = ofFloat;
                ofFloat.addUpdateListener(this.textSwappingUpdateListener);
                this.textSwipingAnimator.setInterpolator(new LinearInterpolator());
                this.textSwipingAnimator.setDuration(170);
                this.textSwipingAnimator.start();
            }
        }
        if (endText != this.arrowAnimateTo) {
            this.arrowAnimateTo = endText;
            ValueAnimator valueAnimator3 = this.arrowRotateAnimator;
            if (valueAnimator3 != null) {
                valueAnimator3.cancel();
            }
            float[] fArr2 = new float[2];
            fArr2[0] = this.arrowRotateProgress;
            if (this.arrowAnimateTo) {
                f = 0.0f;
            }
            fArr2[1] = f;
            ValueAnimator ofFloat2 = ValueAnimator.ofFloat(fArr2);
            this.arrowRotateAnimator = ofFloat2;
            ofFloat2.addUpdateListener(new PullForegroundDrawable$$ExternalSyntheticLambda7(this));
            this.arrowRotateAnimator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            this.arrowRotateAnimator.setDuration(250);
            this.arrowRotateAnimator.start();
        }
    }

    /* renamed from: lambda$updateTextProgress$2$org-telegram-ui-Components-PullForegroundDrawable  reason: not valid java name */
    public /* synthetic */ void m4261xbc2e1var_(ValueAnimator animation) {
        this.arrowRotateProgress = ((Float) animation.getAnimatedValue()).floatValue();
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    public void colorize(boolean colorize) {
        if (this.animateToColorize != colorize) {
            this.animateToColorize = colorize;
            if (colorize) {
                ValueAnimator valueAnimator = this.accentRevalAnimatorIn;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.accentRevalAnimatorIn = null;
                }
                this.accentRevalProgress = 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.accentRevalAnimatorIn = ofFloat;
                ofFloat.addUpdateListener(new PullForegroundDrawable$$ExternalSyntheticLambda0(this));
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
            ofFloat2.addUpdateListener(new PullForegroundDrawable$$ExternalSyntheticLambda1(this));
            this.accentRevalAnimatorOut.setInterpolator(AndroidUtilities.accelerateInterpolator);
            this.accentRevalAnimatorOut.setDuration(230);
            this.accentRevalAnimatorOut.start();
        }
    }

    /* renamed from: lambda$colorize$3$org-telegram-ui-Components-PullForegroundDrawable  reason: not valid java name */
    public /* synthetic */ void m4254x8521dvar_(ValueAnimator animation) {
        this.accentRevalProgress = ((Float) animation.getAnimatedValue()).floatValue();
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.invalidate();
        }
    }

    /* renamed from: lambda$colorize$4$org-telegram-ui-Components-PullForegroundDrawable  reason: not valid java name */
    public /* synthetic */ void m4255xc8acfd21(ValueAnimator animation) {
        this.accentRevalProgressOut = ((Float) animation.getAnimatedValue()).floatValue();
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
            ValueAnimator out = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            out.addUpdateListener(new PullForegroundDrawable$$ExternalSyntheticLambda4(this));
            out.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            out.setDuration(250);
            ValueAnimator bounceIn2 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            bounceIn2.addUpdateListener(new PullForegroundDrawable$$ExternalSyntheticLambda5(this));
            bounceIn2.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            bounceIn2.setDuration(150);
            ValueAnimator bounceOut = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
            bounceOut.addUpdateListener(new PullForegroundDrawable$$ExternalSyntheticLambda6(this));
            bounceOut.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            bounceOut.setDuration(135);
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.outAnimator = animatorSet2;
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    PullForegroundDrawable.this.doNotShow();
                }
            });
            AnimatorSet bounce = new AnimatorSet();
            bounce.playSequentially(new Animator[]{bounceIn2, bounceOut});
            bounce.setStartDelay(180);
            this.outAnimator.playTogether(new Animator[]{out, bounce});
            this.outAnimator.start();
        }
    }

    /* renamed from: lambda$startOutAnimation$5$org-telegram-ui-Components-PullForegroundDrawable  reason: not valid java name */
    public /* synthetic */ void m4258x17d2e0cf(ValueAnimator animation) {
        setOutProgress(((Float) animation.getAnimatedValue()).floatValue());
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    /* renamed from: lambda$startOutAnimation$6$org-telegram-ui-Components-PullForegroundDrawable  reason: not valid java name */
    public /* synthetic */ void m4259x5b5dfe90(ValueAnimator animation) {
        this.bounceProgress = ((Float) animation.getAnimatedValue()).floatValue();
        this.bounceIn = true;
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    /* renamed from: lambda$startOutAnimation$7$org-telegram-ui-Components-PullForegroundDrawable  reason: not valid java name */
    public /* synthetic */ void m4260x9ee91CLASSNAME(ValueAnimator animation) {
        this.bounceProgress = ((Float) animation.getAnimatedValue()).floatValue();
        this.bounceIn = false;
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    private void setOutProgress(float value) {
        this.outProgress = value;
        int color = ColorUtils.blendARGB(Theme.getNonAnimatedColor(this.avatarBackgroundColorKey), Theme.getNonAnimatedColor(this.backgroundActiveColorKey), 1.0f - this.outProgress);
        this.paintBackgroundAccent.setColor(color);
        if (this.changeAvatarColor && isDraw()) {
            Theme.dialogs_archiveAvatarDrawable.beginApplyLayerColors();
            Theme.dialogs_archiveAvatarDrawable.setLayerColor("Arrow1.**", color);
            Theme.dialogs_archiveAvatarDrawable.setLayerColor("Arrow2.**", color);
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

    public void destroyView() {
        this.cell = null;
        ValueAnimator valueAnimator = this.textSwipingAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        AnimatorSet animatorSet = this.outAnimator;
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            this.outAnimator.cancel();
        }
    }

    public boolean isDraw() {
        return this.willDraw && !this.isOut;
    }

    public void setWillDraw(boolean b) {
        this.willDraw = b;
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

    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    private class ArrowDrawable extends Drawable {
        private float lastDensity;
        private Paint paint = new Paint(1);
        private Path path = new Path();

        public ArrowDrawable() {
            updatePath();
        }

        private void updatePath() {
            int h = AndroidUtilities.dp(18.0f);
            this.path.reset();
            this.path.moveTo((float) (h >> 1), AndroidUtilities.dpf2(4.98f));
            this.path.lineTo(AndroidUtilities.dpf2(4.95f), AndroidUtilities.dpf2(9.0f));
            this.path.lineTo(((float) h) - AndroidUtilities.dpf2(4.95f), AndroidUtilities.dpf2(9.0f));
            this.path.lineTo((float) (h >> 1), AndroidUtilities.dpf2(4.98f));
            this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
            this.paint.setStrokeJoin(Paint.Join.ROUND);
            this.paint.setStrokeWidth(AndroidUtilities.dpf2(1.0f));
            this.lastDensity = AndroidUtilities.density;
        }

        public void setColor(int color) {
            this.paint.setColor(color);
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

        public void setAlpha(int alpha) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public int getOpacity() {
            return 0;
        }
    }
}
