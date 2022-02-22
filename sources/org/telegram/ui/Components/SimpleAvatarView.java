package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;

public class SimpleAvatarView extends View {
    /* access modifiers changed from: private */
    public ValueAnimator animator;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private ImageReceiver avatarImage = new ImageReceiver(this);
    private boolean isAvatarHidden;
    private Paint selectPaint = new Paint(1);
    private float selectProgress;

    public SimpleAvatarView(Context context) {
        super(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(28.0f));
        this.selectPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.selectPaint.setStyle(Paint.Style.STROKE);
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
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.selectPaint.setColor(Theme.getColor("dialogTextBlue"));
        canvas.drawCircle(((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f, (((float) Math.min(getWidth(), getHeight())) / 2.0f) - this.selectPaint.getStrokeWidth(), this.selectPaint);
        if (!this.isAvatarHidden) {
            float strokeWidth = this.selectPaint.getStrokeWidth() * 2.5f * this.selectProgress;
            float f = 2.0f * strokeWidth;
            this.avatarImage.setImageCoords(strokeWidth, strokeWidth, ((float) getWidth()) - f, ((float) getHeight()) - f);
            this.avatarImage.draw(canvas);
        }
    }

    public void setAvatar(TLObject tLObject) {
        this.avatarDrawable.setInfo(tLObject);
        this.avatarImage.setForUserOrChat(tLObject, this.avatarDrawable);
    }

    public boolean isSelected() {
        return this.selectProgress == 1.0f;
    }

    public void setSelected(boolean z, boolean z2) {
        float f = 1.0f;
        if (z2) {
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (!z) {
                f = 0.0f;
            }
            ValueAnimator duration = ValueAnimator.ofFloat(new float[]{this.selectProgress, f}).setDuration((long) (Math.abs(f - this.selectProgress) * 150.0f));
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new SimpleAvatarView$$ExternalSyntheticLambda0(this));
            duration.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (SimpleAvatarView.this.animator == animator) {
                        ValueAnimator unused = SimpleAvatarView.this.animator = null;
                    }
                }
            });
            duration.start();
            this.animator = duration;
            return;
        }
        if (!z) {
            f = 0.0f;
        }
        this.selectProgress = f;
        invalidate();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setSelected$0(ValueAnimator valueAnimator) {
        this.selectProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void setHideAvatar(boolean z) {
        this.isAvatarHidden = z;
        invalidate();
    }
}
