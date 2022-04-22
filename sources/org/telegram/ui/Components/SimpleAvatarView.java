package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
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
        canvas.save();
        float f = (this.selectProgress * 0.1f) + 0.9f;
        canvas.scale(f, f);
        this.selectPaint.setColor(Theme.getColor("dialogTextBlue"));
        Paint paint = this.selectPaint;
        paint.setAlpha((int) (((float) Color.alpha(paint.getColor())) * this.selectProgress));
        float strokeWidth = this.selectPaint.getStrokeWidth();
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(strokeWidth, strokeWidth, ((float) getWidth()) - strokeWidth, ((float) getHeight()) - strokeWidth);
        canvas.drawArc(rectF, -90.0f, this.selectProgress * 360.0f, false, this.selectPaint);
        canvas.restore();
        if (!this.isAvatarHidden) {
            float strokeWidth2 = this.selectPaint.getStrokeWidth() * 2.5f * this.selectProgress;
            float f2 = 2.0f * strokeWidth2;
            this.avatarImage.setImageCoords(strokeWidth2, strokeWidth2, ((float) getWidth()) - f2, ((float) getHeight()) - f2);
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
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        float f = 1.0f;
        if (z2) {
            if (!z) {
                f = 0.0f;
            }
            ValueAnimator duration = ValueAnimator.ofFloat(new float[]{this.selectProgress, f}).setDuration(200);
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
