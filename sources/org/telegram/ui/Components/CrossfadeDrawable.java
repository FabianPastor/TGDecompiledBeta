package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class CrossfadeDrawable extends Drawable {
    private final Drawable bottomDrawable;
    /* access modifiers changed from: private */
    public float progress;
    private final Drawable topDrawable;

    public CrossfadeDrawable(Drawable topDrawable2, Drawable bottomDrawable2) {
        this.topDrawable = topDrawable2;
        this.bottomDrawable = bottomDrawable2;
        if (topDrawable2 != null) {
            topDrawable2.setCallback(new Drawable.Callback() {
                public void invalidateDrawable(Drawable drawable) {
                    if (CrossfadeDrawable.this.progress < 1.0f) {
                        CrossfadeDrawable.this.invalidateSelf();
                    }
                }

                public void scheduleDrawable(Drawable drawable, Runnable runnable, long l) {
                }

                public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
                }
            });
        }
        if (bottomDrawable2 != null) {
            bottomDrawable2.setCallback(new Drawable.Callback() {
                public void invalidateDrawable(Drawable drawable) {
                    if (CrossfadeDrawable.this.progress > 0.0f) {
                        CrossfadeDrawable.this.invalidateSelf();
                    }
                }

                public void scheduleDrawable(Drawable drawable, Runnable runnable, long l) {
                }

                public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect bounds) {
        this.topDrawable.setBounds(bounds);
        this.bottomDrawable.setBounds(bounds);
    }

    public void draw(Canvas canvas) {
        float f = this.progress;
        if (f < 1.0f) {
            this.topDrawable.setAlpha((int) ((1.0f - f) * 255.0f));
            this.topDrawable.draw(canvas);
        }
        float f2 = this.progress;
        if (f2 > 0.0f) {
            this.bottomDrawable.setAlpha((int) (f2 * 255.0f));
            this.bottomDrawable.draw(canvas);
        }
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.topDrawable.setColorFilter(colorFilter);
    }

    public int getOpacity() {
        return -3;
    }

    public int getIntrinsicWidth() {
        return this.topDrawable.getIntrinsicWidth();
    }

    public int getIntrinsicHeight() {
        return this.topDrawable.getIntrinsicHeight();
    }

    public float getProgress() {
        return this.progress;
    }

    public void setProgress(float value) {
        this.progress = value;
    }
}
