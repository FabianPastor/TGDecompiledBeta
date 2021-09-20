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

    public int getOpacity() {
        return -2;
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
    }

    public void setAlpha(int i) {
    }

    public ReplaceableIconDrawable(Context context2) {
        this.context = context2;
    }

    public void setIcon(int i, boolean z) {
        if (this.currentResId != i) {
            setIcon(ContextCompat.getDrawable(this.context, i).mutate(), z);
            this.currentResId = i;
        }
    }

    public void setIcon(Drawable drawable, boolean z) {
        if (drawable == null) {
            this.currentDrawable = null;
            this.outDrawable = null;
            invalidateSelf();
            return;
        }
        if (getBounds() == null || getBounds().isEmpty()) {
            z = false;
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
        if (!z) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setIcon$0(ValueAnimator valueAnimator) {
        this.progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidateSelf();
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        updateBounds(this.currentDrawable, rect);
        updateBounds(this.outDrawable, rect);
    }

    private void updateBounds(Drawable drawable, Rect rect) {
        int i;
        int i2;
        int i3;
        int i4;
        if (drawable != null) {
            if (drawable.getIntrinsicHeight() < 0) {
                i2 = rect.top;
                i = rect.bottom;
            } else {
                int height = (rect.height() - drawable.getIntrinsicHeight()) / 2;
                int i5 = rect.top;
                int i6 = i5 + height;
                i = i5 + height + drawable.getIntrinsicHeight();
                i2 = i6;
            }
            if (drawable.getIntrinsicWidth() < 0) {
                i4 = rect.left;
                i3 = rect.right;
            } else {
                int width = (rect.width() - drawable.getIntrinsicWidth()) / 2;
                int i7 = rect.left;
                int i8 = i7 + width;
                i3 = i7 + width + drawable.getIntrinsicWidth();
                i4 = i8;
            }
            drawable.setBounds(i4, i2, i3, i);
        }
    }

    public void draw(Canvas canvas) {
        int centerX = getBounds().centerX();
        int centerY = getBounds().centerY();
        if (this.progress == 1.0f || this.currentDrawable == null) {
            Drawable drawable = this.currentDrawable;
            if (drawable != null) {
                drawable.setAlpha(255);
                this.currentDrawable.draw(canvas);
            }
        } else {
            canvas.save();
            float f = this.progress;
            canvas.scale(f, f, (float) centerX, (float) centerY);
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
        float f3 = 1.0f - f2;
        canvas.save();
        canvas.scale(f3, f3, (float) centerX, (float) centerY);
        this.outDrawable.setAlpha((int) (f3 * 255.0f));
        this.outDrawable.draw(canvas);
        canvas.restore();
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

    public void onAnimationEnd(Animator animator) {
        this.outDrawable = null;
        invalidateSelf();
    }
}
