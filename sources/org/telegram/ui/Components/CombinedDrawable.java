package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Drawable.ConstantState;

public class CombinedDrawable extends Drawable implements Callback {
    private int backHeight;
    private int backWidth;
    private Drawable background;
    private boolean fullSize;
    private Drawable icon;
    private int iconHeight;
    private int iconWidth;
    private int left;
    private int offsetX;
    private int offsetY;
    private int top;

    /* Access modifiers changed, original: protected */
    public boolean onStateChange(int[] iArr) {
        return true;
    }

    public CombinedDrawable(Drawable drawable, Drawable drawable2, int i, int i2) {
        this.background = drawable;
        this.icon = drawable2;
        this.left = i;
        this.top = i2;
        if (drawable2 != null) {
            drawable2.setCallback(this);
        }
    }

    public void setIconSize(int i, int i2) {
        this.iconWidth = i;
        this.iconHeight = i2;
    }

    public CombinedDrawable(Drawable drawable, Drawable drawable2) {
        this.background = drawable;
        this.icon = drawable2;
        if (drawable2 != null) {
            drawable2.setCallback(this);
        }
    }

    public void setCustomSize(int i, int i2) {
        this.backWidth = i;
        this.backHeight = i2;
    }

    public void setIconOffset(int i, int i2) {
        this.offsetX = i;
        this.offsetY = i2;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public Drawable getBackground() {
        return this.background;
    }

    public void setFullsize(boolean z) {
        this.fullSize = z;
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.icon.setColorFilter(colorFilter);
    }

    public boolean isStateful() {
        return this.icon.isStateful();
    }

    public boolean setState(int[] iArr) {
        this.icon.setState(iArr);
        return true;
    }

    public int[] getState() {
        return this.icon.getState();
    }

    public void jumpToCurrentState() {
        this.icon.jumpToCurrentState();
    }

    public ConstantState getConstantState() {
        return this.icon.getConstantState();
    }

    public void draw(Canvas canvas) {
        this.background.setBounds(getBounds());
        this.background.draw(canvas);
        if (this.icon != null) {
            int i;
            Drawable drawable;
            int centerX;
            if (this.fullSize) {
                Rect bounds = getBounds();
                i = this.left;
                if (i != 0) {
                    drawable = this.icon;
                    int i2 = bounds.left + i;
                    int i3 = bounds.top;
                    int i4 = this.top;
                    drawable.setBounds(i2, i3 + i4, bounds.right - i, bounds.bottom - i4);
                } else {
                    this.icon.setBounds(bounds);
                }
            } else if (this.iconWidth != 0) {
                centerX = ((getBounds().centerX() - (this.iconWidth / 2)) + this.left) + this.offsetX;
                i = getBounds().centerY();
                int i5 = this.iconHeight;
                i = ((i - (i5 / 2)) + this.top) + this.offsetY;
                this.icon.setBounds(centerX, i, this.iconWidth + centerX, i5 + i);
            } else {
                centerX = (getBounds().centerX() - (this.icon.getIntrinsicWidth() / 2)) + this.left;
                i = (getBounds().centerY() - (this.icon.getIntrinsicHeight() / 2)) + this.top;
                drawable = this.icon;
                drawable.setBounds(centerX, i, drawable.getIntrinsicWidth() + centerX, this.icon.getIntrinsicHeight() + i);
            }
            this.icon.draw(canvas);
        }
    }

    public void setAlpha(int i) {
        this.icon.setAlpha(i);
        this.background.setAlpha(i);
    }

    public int getIntrinsicWidth() {
        int i = this.backWidth;
        return i != 0 ? i : this.background.getIntrinsicWidth();
    }

    public int getIntrinsicHeight() {
        int i = this.backHeight;
        return i != 0 ? i : this.background.getIntrinsicHeight();
    }

    public int getMinimumWidth() {
        int i = this.backWidth;
        return i != 0 ? i : this.background.getMinimumWidth();
    }

    public int getMinimumHeight() {
        int i = this.backHeight;
        return i != 0 ? i : this.background.getMinimumHeight();
    }

    public int getOpacity() {
        return this.icon.getOpacity();
    }

    public void invalidateDrawable(Drawable drawable) {
        invalidateSelf();
    }

    public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
        scheduleSelf(runnable, j);
    }

    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        unscheduleSelf(runnable);
    }
}
