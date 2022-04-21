package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;

public class SenderSelectView extends View {
    private static final FloatPropertyCompat<SenderSelectView> MENU_PROGRESS = new SimpleFloatPropertyCompat("menuProgress", SenderSelectView$$ExternalSyntheticLambda3.INSTANCE, SenderSelectView$$ExternalSyntheticLambda4.INSTANCE).setMultiplier(100.0f);
    private static final float SPRING_MULTIPLIER = 100.0f;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private ImageReceiver avatarImage = new ImageReceiver(this);
    private Paint backgroundPaint = new Paint(1);
    /* access modifiers changed from: private */
    public ValueAnimator menuAnimator;
    private Paint menuPaint = new Paint(1);
    /* access modifiers changed from: private */
    public float menuProgress;
    private SpringAnimation menuSpring;
    private boolean scaleIn;
    private boolean scaleOut;
    private Drawable selectorDrawable;

    static /* synthetic */ void lambda$static$1(SenderSelectView obj, float value) {
        obj.menuProgress = value;
        obj.invalidate();
    }

    public SenderSelectView(Context context) {
        super(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(28.0f));
        this.menuPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.menuPaint.setStrokeCap(Paint.Cap.ROUND);
        this.menuPaint.setStyle(Paint.Style.STROKE);
        updateColors();
    }

    private void updateColors() {
        this.backgroundPaint.setColor(Theme.getColor("chat_messagePanelVoiceBackground"));
        this.menuPaint.setColor(Theme.getColor("chat_messagePanelVoicePressed"));
        Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(16.0f), 0, Theme.getColor("windowBackgroundWhite"));
        this.selectorDrawable = createSimpleSelectorRoundRectDrawable;
        createSimpleSelectorRoundRectDrawable.setCallback(this);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(getLayoutParams().width, NUM), View.MeasureSpec.makeMeasureSpec(getLayoutParams().height, NUM));
        this.avatarImage.setImageCoords(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float sc;
        canvas.save();
        if (this.scaleOut) {
            sc = 1.0f - this.menuProgress;
        } else if (this.scaleIn) {
            sc = this.menuProgress;
        } else {
            sc = 1.0f;
        }
        canvas.scale(sc, sc, ((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f);
        super.onDraw(canvas);
        this.avatarImage.draw(canvas);
        int alpha = (int) (this.menuProgress * 255.0f);
        this.backgroundPaint.setAlpha(alpha);
        canvas.drawCircle(((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f, ((float) Math.min(getWidth(), getHeight())) / 2.0f, this.backgroundPaint);
        canvas.save();
        this.menuPaint.setAlpha(alpha);
        float padding = ((float) AndroidUtilities.dp(9.0f)) + this.menuPaint.getStrokeWidth();
        Canvas canvas2 = canvas;
        float f = padding;
        canvas2.drawLine(f, padding, ((float) getWidth()) - padding, ((float) getHeight()) - padding, this.menuPaint);
        canvas2.drawLine(f, ((float) getHeight()) - padding, ((float) getWidth()) - padding, padding, this.menuPaint);
        canvas.restore();
        this.selectorDrawable.setBounds(0, 0, getWidth(), getHeight());
        this.selectorDrawable.draw(canvas);
        canvas.restore();
    }

    public void setAvatar(TLObject obj) {
        this.avatarDrawable.setInfo(obj);
        this.avatarImage.setForUserOrChat(obj, this.avatarDrawable);
    }

    public void setProgress(float progress) {
        setProgress(progress, true);
    }

    public void setProgress(float progress, boolean animate) {
        setProgress(progress, animate, progress != 0.0f);
    }

    public void setProgress(float progress, boolean animate, boolean useSpring) {
        if (animate) {
            SpringAnimation springAnimation = this.menuSpring;
            if (springAnimation != null) {
                springAnimation.cancel();
            }
            ValueAnimator valueAnimator = this.menuAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            boolean z = false;
            this.scaleIn = false;
            this.scaleOut = false;
            if (useSpring) {
                float startValue = this.menuProgress * 100.0f;
                SpringAnimation springAnimation2 = (SpringAnimation) new SpringAnimation(this, MENU_PROGRESS).setStartValue(startValue);
                this.menuSpring = springAnimation2;
                boolean reverse = progress < this.menuProgress;
                float finalPos = 100.0f * progress;
                this.scaleIn = reverse;
                if (!reverse) {
                    z = true;
                }
                this.scaleOut = z;
                springAnimation2.setSpring(new SpringForce(finalPos).setFinalPosition(finalPos).setStiffness(450.0f).setDampingRatio(1.0f));
                this.menuSpring.addUpdateListener(new SenderSelectView$$ExternalSyntheticLambda2(this, reverse, startValue, finalPos));
                this.menuSpring.addEndListener(new SenderSelectView$$ExternalSyntheticLambda1(this));
                this.menuSpring.start();
                return;
            }
            ValueAnimator duration = ValueAnimator.ofFloat(new float[]{this.menuProgress, progress}).setDuration(200);
            this.menuAnimator = duration;
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.menuAnimator.addUpdateListener(new SenderSelectView$$ExternalSyntheticLambda0(this));
            this.menuAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (animation == SenderSelectView.this.menuAnimator) {
                        ValueAnimator unused = SenderSelectView.this.menuAnimator = null;
                    }
                }
            });
            this.menuAnimator.start();
            return;
        }
        this.menuProgress = progress;
        invalidate();
    }

    /* renamed from: lambda$setProgress$2$org-telegram-ui-Components-SenderSelectView  reason: not valid java name */
    public /* synthetic */ void m4330lambda$setProgress$2$orgtelegramuiComponentsSenderSelectView(boolean reverse, float startValue, float finalPos, DynamicAnimation animation, float value, float velocity) {
        if (reverse) {
            if (value > startValue / 2.0f || !this.scaleIn) {
                return;
            }
        } else if (value < finalPos / 2.0f || !this.scaleOut) {
            return;
        }
        this.scaleIn = !reverse;
        this.scaleOut = reverse;
    }

    /* renamed from: lambda$setProgress$3$org-telegram-ui-Components-SenderSelectView  reason: not valid java name */
    public /* synthetic */ void m4331lambda$setProgress$3$orgtelegramuiComponentsSenderSelectView(DynamicAnimation animation, boolean canceled, float value, float velocity) {
        this.scaleIn = false;
        this.scaleOut = false;
        if (!canceled) {
            animation.cancel();
        }
        if (animation == this.menuSpring) {
            this.menuSpring = null;
        }
    }

    /* renamed from: lambda$setProgress$4$org-telegram-ui-Components-SenderSelectView  reason: not valid java name */
    public /* synthetic */ void m4332lambda$setProgress$4$orgtelegramuiComponentsSenderSelectView(ValueAnimator animation) {
        this.menuProgress = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    public float getProgress() {
        return this.menuProgress;
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || this.selectorDrawable == who;
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        this.selectorDrawable.setState(getDrawableState());
    }

    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        this.selectorDrawable.jumpToCurrentState();
    }
}
