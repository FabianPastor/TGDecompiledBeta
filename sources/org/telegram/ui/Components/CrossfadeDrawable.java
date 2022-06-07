package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class CrossfadeDrawable extends Drawable {
    private final Drawable bottomDrawable;
    float globalAlpha = 255.0f;
    /* access modifiers changed from: private */
    public float progress;
    private final Drawable topDrawable;

    public int getOpacity() {
        return -3;
    }

    public CrossfadeDrawable(Drawable drawable, Drawable drawable2) {
        this.topDrawable = drawable;
        this.bottomDrawable = drawable2;
        if (drawable != null) {
            drawable.setCallback(new Drawable.Callback() {
                public void invalidateDrawable(Drawable drawable) {
                    if (CrossfadeDrawable.this.progress < 1.0f) {
                        CrossfadeDrawable.this.invalidateSelf();
                    }
                }

                public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
                    if (CrossfadeDrawable.this.progress < 1.0f) {
                        CrossfadeDrawable.this.scheduleSelf(runnable, j);
                    }
                }

                public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
                    if (CrossfadeDrawable.this.progress < 1.0f) {
                        CrossfadeDrawable.this.unscheduleSelf(runnable);
                    }
                }
            });
        }
        if (drawable2 != null) {
            drawable2.setCallback(new Drawable.Callback() {
                public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
                }

                public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
                }

                public void invalidateDrawable(Drawable drawable) {
                    if (CrossfadeDrawable.this.progress > 0.0f) {
                        CrossfadeDrawable.this.invalidateSelf();
                    }
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        this.topDrawable.setBounds(rect);
        this.bottomDrawable.setBounds(rect);
    }

    public void draw(Canvas canvas) {
        float f = this.progress;
        if (f < 1.0f) {
            this.topDrawable.setAlpha((int) (this.globalAlpha * (1.0f - f)));
            this.topDrawable.draw(canvas);
        }
        float f2 = this.progress;
        if (f2 > 0.0f) {
            this.bottomDrawable.setAlpha((int) (this.globalAlpha * f2));
            this.bottomDrawable.draw(canvas);
        }
    }

    public void setAlpha(int i) {
        this.globalAlpha = (float) i;
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.topDrawable.setColorFilter(colorFilter);
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

    public void setProgress(float f) {
        this.progress = f;
        invalidateSelf();
    }
}
