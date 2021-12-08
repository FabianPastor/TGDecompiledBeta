package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;

public class MessageBackgroundDrawable extends Drawable {
    private boolean animationInProgress;
    private float currentAnimationProgress;
    private float finalRadius;
    private boolean isSelected;
    private long lastAnimationTime;
    private long lastTouchTime;
    private Paint paint = new Paint(1);
    private View parentView;
    private float touchOverrideX = -1.0f;
    private float touchOverrideY = -1.0f;
    private float touchX = -1.0f;
    private float touchY = -1.0f;

    public MessageBackgroundDrawable(View parent) {
        this.parentView = parent;
    }

    public void setColor(int color) {
        this.paint.setColor(color);
    }

    public void setSelected(boolean selected, boolean animated) {
        float f = 1.0f;
        if (this.isSelected != selected) {
            this.isSelected = selected;
            this.animationInProgress = animated;
            if (animated) {
                this.lastAnimationTime = SystemClock.elapsedRealtime();
            } else {
                if (!selected) {
                    f = 0.0f;
                }
                this.currentAnimationProgress = f;
            }
            calcRadius();
            invalidate();
        } else if (this.animationInProgress != animated && !animated) {
            if (!selected) {
                f = 0.0f;
            }
            this.currentAnimationProgress = f;
            this.animationInProgress = false;
        }
    }

    private void invalidate() {
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
            if (this.parentView.getParent() != null) {
                ((ViewGroup) this.parentView.getParent()).invalidate();
            }
        }
    }

    private void calcRadius() {
        Rect bounds = getBounds();
        float x1 = (float) bounds.centerX();
        float y1 = (float) bounds.centerY();
        this.finalRadius = (float) Math.ceil(Math.sqrt((double) (((((float) bounds.left) - x1) * (((float) bounds.left) - x1)) + ((((float) bounds.top) - y1) * (((float) bounds.top) - y1)))));
    }

    public void setTouchCoords(float x, float y) {
        this.touchX = x;
        this.touchY = y;
        this.lastTouchTime = SystemClock.elapsedRealtime();
    }

    public void setTouchCoordsOverride(float x, float y) {
        this.touchOverrideX = x;
        this.touchOverrideY = y;
    }

    public float getTouchX() {
        return this.touchX;
    }

    public float getTouchY() {
        return this.touchY;
    }

    public long getLastTouchTime() {
        return this.lastTouchTime;
    }

    public boolean isAnimationInProgress() {
        return this.animationInProgress;
    }

    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        calcRadius();
    }

    public void setBounds(Rect bounds) {
        super.setBounds(bounds);
        calcRadius();
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
    }

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int alpha) {
        this.paint.setAlpha(alpha);
    }

    public void draw(Canvas canvas) {
        float interpolatedProgress;
        float y1;
        float x1;
        float f = this.currentAnimationProgress;
        if (f == 1.0f) {
            canvas.drawRect(getBounds(), this.paint);
        } else if (f != 0.0f) {
            if (this.isSelected) {
                interpolatedProgress = CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(this.currentAnimationProgress);
            } else {
                interpolatedProgress = 1.0f - CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(1.0f - this.currentAnimationProgress);
            }
            Rect bounds = getBounds();
            float centerX = (float) bounds.centerX();
            float centerY = (float) bounds.centerY();
            if (this.touchOverrideX >= 0.0f && this.touchOverrideY >= 0.0f) {
                x1 = this.touchOverrideX;
                y1 = this.touchOverrideY;
            } else if (this.touchX < 0.0f || this.touchY < 0.0f) {
                x1 = centerX;
                y1 = centerY;
            } else {
                x1 = this.touchX;
                y1 = this.touchY;
            }
            canvas.drawCircle(((1.0f - interpolatedProgress) * (x1 - centerX)) + centerX, ((1.0f - interpolatedProgress) * (y1 - centerY)) + centerY, this.finalRadius * interpolatedProgress, this.paint);
        }
        if (this.animationInProgress) {
            long newTime = SystemClock.elapsedRealtime();
            long dt = newTime - this.lastAnimationTime;
            if (dt > 20) {
                dt = 17;
            }
            this.lastAnimationTime = newTime;
            boolean finished = false;
            if (this.isSelected) {
                float f2 = this.currentAnimationProgress + (((float) dt) / 240.0f);
                this.currentAnimationProgress = f2;
                if (f2 >= 1.0f) {
                    this.currentAnimationProgress = 1.0f;
                    finished = true;
                }
            } else {
                float f3 = this.currentAnimationProgress - (((float) dt) / 240.0f);
                this.currentAnimationProgress = f3;
                if (f3 <= 0.0f) {
                    this.currentAnimationProgress = 0.0f;
                    finished = true;
                }
            }
            if (finished) {
                this.touchX = -1.0f;
                this.touchY = -1.0f;
                this.touchOverrideX = -1.0f;
                this.touchOverrideY = -1.0f;
                this.animationInProgress = false;
            }
            invalidate();
        }
    }
}
