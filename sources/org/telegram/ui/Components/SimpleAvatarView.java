package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;

public class SimpleAvatarView extends View {
    public static final int SELECT_ANIMATION_DURATION = 200;
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

    public SimpleAvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(28.0f));
        this.selectPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.selectPaint.setStyle(Paint.Style.STROKE);
    }

    public SimpleAvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        float scale = (this.selectProgress * 0.1f) + 0.9f;
        canvas.scale(scale, scale);
        this.selectPaint.setColor(Theme.getColor("dialogTextBlue"));
        Paint paint = this.selectPaint;
        paint.setAlpha((int) (((float) Color.alpha(paint.getColor())) * this.selectProgress));
        float stroke = this.selectPaint.getStrokeWidth();
        AndroidUtilities.rectTmp.set(stroke, stroke, ((float) getWidth()) - stroke, ((float) getHeight()) - stroke);
        canvas.drawArc(AndroidUtilities.rectTmp, -90.0f, this.selectProgress * 360.0f, false, this.selectPaint);
        canvas.restore();
        if (!this.isAvatarHidden) {
            float pad = this.selectPaint.getStrokeWidth() * 2.5f * this.selectProgress;
            this.avatarImage.setImageCoords(pad, pad, ((float) getWidth()) - (pad * 2.0f), ((float) getHeight()) - (2.0f * pad));
            this.avatarImage.draw(canvas);
        }
    }

    public void setAvatar(TLObject obj) {
        this.avatarDrawable.setInfo(obj);
        this.avatarImage.setForUserOrChat(obj, this.avatarDrawable);
    }

    public boolean isSelected() {
        return this.selectProgress == 1.0f;
    }

    public void setSelected(boolean s, boolean animate) {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        float to = 1.0f;
        if (animate) {
            if (!s) {
                to = 0.0f;
            }
            ValueAnimator anim = ValueAnimator.ofFloat(new float[]{this.selectProgress, to}).setDuration(200);
            anim.setInterpolator(CubicBezierInterpolator.DEFAULT);
            anim.addUpdateListener(new SimpleAvatarView$$ExternalSyntheticLambda0(this));
            anim.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (SimpleAvatarView.this.animator == animation) {
                        ValueAnimator unused = SimpleAvatarView.this.animator = null;
                    }
                }
            });
            anim.start();
            this.animator = anim;
            return;
        }
        if (!s) {
            to = 0.0f;
        }
        this.selectProgress = to;
        invalidate();
    }

    /* renamed from: lambda$setSelected$0$org-telegram-ui-Components-SimpleAvatarView  reason: not valid java name */
    public /* synthetic */ void m1408lambda$setSelected$0$orgtelegramuiComponentsSimpleAvatarView(ValueAnimator animation) {
        this.selectProgress = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void setHideAvatar(boolean h) {
        this.isAvatarHidden = h;
        invalidate();
    }
}
