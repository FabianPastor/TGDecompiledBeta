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
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatInvite;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes3.dex */
public class SenderSelectView extends View {
    private static final FloatPropertyCompat<SenderSelectView> MENU_PROGRESS = new SimpleFloatPropertyCompat("menuProgress", SenderSelectView$$ExternalSyntheticLambda3.INSTANCE, SenderSelectView$$ExternalSyntheticLambda4.INSTANCE).setMultiplier(100.0f);
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage;
    private Paint backgroundPaint;
    private ValueAnimator menuAnimator;
    private Paint menuPaint;
    private float menuProgress;
    private SpringAnimation menuSpring;
    private boolean scaleIn;
    private boolean scaleOut;
    private Drawable selectorDrawable;

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$static$1(SenderSelectView senderSelectView, float f) {
        senderSelectView.menuProgress = f;
        senderSelectView.invalidate();
    }

    public SenderSelectView(Context context) {
        super(context);
        this.avatarImage = new ImageReceiver(this);
        this.avatarDrawable = new AvatarDrawable();
        this.backgroundPaint = new Paint(1);
        this.menuPaint = new Paint(1);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(28.0f));
        this.menuPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.menuPaint.setStrokeCap(Paint.Cap.ROUND);
        this.menuPaint.setStyle(Paint.Style.STROKE);
        updateColors();
        setContentDescription(LocaleController.formatString("AccDescrSendAsPeer", R.string.AccDescrSendAsPeer, ""));
    }

    private void updateColors() {
        this.backgroundPaint.setColor(Theme.getColor("chat_messagePanelVoiceBackground"));
        this.menuPaint.setColor(Theme.getColor("chat_messagePanelVoicePressed"));
        Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(16.0f), 0, Theme.getColor("windowBackgroundWhite"));
        this.selectorDrawable = createSimpleSelectorRoundRectDrawable;
        createSimpleSelectorRoundRectDrawable.setCallback(this);
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(getLayoutParams().width, NUM), View.MeasureSpec.makeMeasureSpec(getLayoutParams().height, NUM));
        this.avatarImage.setImageCoords(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        canvas.save();
        float f = 1.0f;
        if (this.scaleOut) {
            f = 1.0f - this.menuProgress;
        } else if (this.scaleIn) {
            f = this.menuProgress;
        }
        canvas.scale(f, f, getWidth() / 2.0f, getHeight() / 2.0f);
        super.onDraw(canvas);
        this.avatarImage.draw(canvas);
        int i = (int) (this.menuProgress * 255.0f);
        this.backgroundPaint.setAlpha(i);
        canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, Math.min(getWidth(), getHeight()) / 2.0f, this.backgroundPaint);
        canvas.save();
        this.menuPaint.setAlpha(i);
        float dp = AndroidUtilities.dp(9.0f) + this.menuPaint.getStrokeWidth();
        canvas.drawLine(dp, dp, getWidth() - dp, getHeight() - dp, this.menuPaint);
        canvas.drawLine(dp, getHeight() - dp, getWidth() - dp, dp, this.menuPaint);
        canvas.restore();
        this.selectorDrawable.setBounds(0, 0, getWidth(), getHeight());
        this.selectorDrawable.draw(canvas);
        canvas.restore();
    }

    public void setAvatar(TLObject tLObject) {
        String str;
        if (tLObject instanceof TLRPC$User) {
            str = UserObject.getFirstName((TLRPC$User) tLObject);
        } else if (tLObject instanceof TLRPC$Chat) {
            str = ((TLRPC$Chat) tLObject).title;
        } else {
            str = tLObject instanceof TLRPC$ChatInvite ? ((TLRPC$ChatInvite) tLObject).title : "";
        }
        setContentDescription(LocaleController.formatString("AccDescrSendAsPeer", R.string.AccDescrSendAsPeer, str));
        this.avatarDrawable.setInfo(tLObject);
        this.avatarImage.setForUserOrChat(tLObject, this.avatarDrawable);
    }

    public void setProgress(float f) {
        setProgress(f, true);
    }

    public void setProgress(float f, boolean z) {
        setProgress(f, z, f != 0.0f);
    }

    public void setProgress(float f, boolean z, boolean z2) {
        if (z) {
            SpringAnimation springAnimation = this.menuSpring;
            if (springAnimation != null) {
                springAnimation.cancel();
            }
            ValueAnimator valueAnimator = this.menuAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            final boolean z3 = false;
            this.scaleIn = false;
            this.scaleOut = false;
            if (z2) {
                final float f2 = this.menuProgress * 100.0f;
                SpringAnimation startValue = new SpringAnimation(this, MENU_PROGRESS).setStartValue(f2);
                this.menuSpring = startValue;
                if (f < this.menuProgress) {
                    z3 = true;
                }
                final float f3 = f * 100.0f;
                this.scaleIn = z3;
                this.scaleOut = !z3;
                startValue.setSpring(new SpringForce(f3).setFinalPosition(f3).setStiffness(450.0f).setDampingRatio(1.0f));
                this.menuSpring.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.SenderSelectView$$ExternalSyntheticLambda2
                    @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
                    public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f4, float f5) {
                        SenderSelectView.this.lambda$setProgress$2(z3, f2, f3, dynamicAnimation, f4, f5);
                    }
                });
                this.menuSpring.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.SenderSelectView$$ExternalSyntheticLambda1
                    @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z4, float f4, float f5) {
                        SenderSelectView.this.lambda$setProgress$3(dynamicAnimation, z4, f4, f5);
                    }
                });
                this.menuSpring.start();
                return;
            }
            ValueAnimator duration = ValueAnimator.ofFloat(this.menuProgress, f).setDuration(200L);
            this.menuAnimator = duration;
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.menuAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.SenderSelectView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    SenderSelectView.this.lambda$setProgress$4(valueAnimator2);
                }
            });
            this.menuAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.SenderSelectView.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (animator == SenderSelectView.this.menuAnimator) {
                        SenderSelectView.this.menuAnimator = null;
                    }
                }
            });
            this.menuAnimator.start();
            return;
        }
        this.menuProgress = f;
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setProgress$2(boolean z, float f, float f2, DynamicAnimation dynamicAnimation, float f3, float f4) {
        if (z) {
            if (f3 > f / 2.0f || !this.scaleIn) {
                return;
            }
        } else if (f3 < f2 / 2.0f || !this.scaleOut) {
            return;
        }
        this.scaleIn = !z;
        this.scaleOut = z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setProgress$3(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.scaleIn = false;
        this.scaleOut = false;
        if (!z) {
            dynamicAnimation.cancel();
        }
        if (dynamicAnimation == this.menuSpring) {
            this.menuSpring = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setProgress$4(ValueAnimator valueAnimator) {
        this.menuProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public float getProgress() {
        return this.menuProgress;
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || this.selectorDrawable == drawable;
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        this.selectorDrawable.setState(getDrawableState());
    }

    @Override // android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        this.selectorDrawable.jumpToCurrentState();
    }
}
