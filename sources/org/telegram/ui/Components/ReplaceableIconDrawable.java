package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

public class ReplaceableIconDrawable extends Drawable implements Animator.AnimatorListener {
    private ValueAnimator animation;
    private ColorFilter colorFilter;
    private Context context;
    private Drawable currentDrawable;
    private int currentResId = 0;
    private Drawable outDrawable;
    private float progress = 1.0f;

    public ReplaceableIconDrawable(Context context2) {
        this.context = context2;
    }

    public void setIcon(int resId, boolean animated) {
        if (this.currentResId != resId) {
            setIcon(ContextCompat.getDrawable(this.context, resId).mutate(), animated);
            this.currentResId = resId;
        }
    }

    public void setIcon(Drawable drawable, boolean animated) {
        if (drawable == null) {
            this.currentDrawable = null;
            this.outDrawable = null;
            invalidateSelf();
            return;
        }
        if (getBounds() == null || getBounds().isEmpty()) {
            animated = false;
        }
        Drawable drawable2 = this.currentDrawable;
        if (drawable == drawable2) {
            drawable2.setColorFilter(this.colorFilter);
            return;
        }
        this.currentResId = 0;
        this.outDrawable = drawable2;
        this.currentDrawable = drawable;
        drawable.setColorFilter(this.colorFilter);
        updateBounds(this.currentDrawable, getBounds());
        updateBounds(this.outDrawable, getBounds());
        ValueAnimator valueAnimator = this.animation;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.animation.cancel();
        }
        if (!animated) {
            this.progress = 1.0f;
            this.outDrawable = null;
            return;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.animation = ofFloat;
        ofFloat.addUpdateListener(new ReplaceableIconDrawable$$ExternalSyntheticLambda0(this));
        this.animation.addListener(this);
        this.animation.setDuration(150);
        this.animation.start();
    }

    /* renamed from: lambda$setIcon$0$org-telegram-ui-Components-ReplaceableIconDrawable  reason: not valid java name */
    public /* synthetic */ void m4293xd9f8d97e(ValueAnimator animation2) {
        this.progress = ((Float) animation2.getAnimatedValue()).floatValue();
        invalidateSelf();
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        updateBounds(this.currentDrawable, bounds);
        updateBounds(this.outDrawable, bounds);
    }

    private void updateBounds(Drawable d, Rect bounds) {
        int top;
        int offset;
        int left;
        int offset2;
        if (d != null) {
            if (d.getIntrinsicHeight() < 0) {
                offset = bounds.top;
                top = bounds.bottom;
            } else {
                int offset3 = (bounds.height() - d.getIntrinsicHeight()) / 2;
                offset = bounds.top + offset3;
                top = bounds.top + offset3 + d.getIntrinsicHeight();
            }
            if (d.getIntrinsicWidth() < 0) {
                offset2 = bounds.left;
                left = bounds.right;
            } else {
                int offset4 = (bounds.width() - d.getIntrinsicWidth()) / 2;
                offset2 = bounds.left + offset4;
                left = bounds.left + offset4 + d.getIntrinsicWidth();
            }
            d.setBounds(offset2, offset, left, top);
        }
    }

    public void draw(Canvas canvas) {
        int cX = getBounds().centerX();
        int cY = getBounds().centerY();
        if (this.progress == 1.0f || this.currentDrawable == null) {
            Drawable drawable = this.currentDrawable;
            if (drawable != null) {
                drawable.setAlpha(255);
                this.currentDrawable.draw(canvas);
            }
        } else {
            canvas.save();
            float f = this.progress;
            canvas.scale(f, f, (float) cX, (float) cY);
            this.currentDrawable.setAlpha((int) (this.progress * 255.0f));
            this.currentDrawable.draw(canvas);
            canvas.restore();
        }
        float f2 = this.progress;
        if (f2 == 1.0f || this.outDrawable == null) {
            Drawable drawable2 = this.outDrawable;
            if (drawable2 != null) {
                drawable2.setAlpha(255);
                this.outDrawable.draw(canvas);
                return;
            }
            return;
        }
        float progressRev = 1.0f - f2;
        canvas.save();
        canvas.scale(progressRev, progressRev, (float) cX, (float) cY);
        this.outDrawable.setAlpha((int) (255.0f * progressRev));
        this.outDrawable.draw(canvas);
        canvas.restore();
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter colorFilter2) {
        this.colorFilter = colorFilter2;
        Drawable drawable = this.currentDrawable;
        if (drawable != null) {
            drawable.setColorFilter(colorFilter2);
        }
        Drawable drawable2 = this.outDrawable;
        if (drawable2 != null) {
            drawable2.setColorFilter(colorFilter2);
        }
        invalidateSelf();
    }

    public int getOpacity() {
        return -2;
    }

    public void onAnimationEnd(Animator animation2) {
        this.outDrawable = null;
        invalidateSelf();
    }

    public void onAnimationStart(Animator animation2) {
    }

    public void onAnimationCancel(Animator animation2) {
    }

    public void onAnimationRepeat(Animator animation2) {
    }
}
