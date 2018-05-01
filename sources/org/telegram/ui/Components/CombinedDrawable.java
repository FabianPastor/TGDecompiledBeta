package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
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

    protected boolean onStateChange(int[] iArr) {
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
        return 1;
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
            if (this.fullSize) {
                this.icon.setBounds(getBounds());
            } else if (this.iconWidth != 0) {
                r0 = ((getBounds().centerX() - (this.iconWidth / 2)) + this.left) + this.offsetX;
                r1 = ((getBounds().centerY() - (this.iconHeight / 2)) + this.top) + this.offsetY;
                this.icon.setBounds(r0, r1, this.iconWidth + r0, this.iconHeight + r1);
            } else {
                r0 = (getBounds().centerX() - (this.icon.getIntrinsicWidth() / 2)) + this.left;
                r1 = (getBounds().centerY() - (this.icon.getIntrinsicHeight() / 2)) + this.top;
                this.icon.setBounds(r0, r1, this.icon.getIntrinsicWidth() + r0, this.icon.getIntrinsicHeight() + r1);
            }
            this.icon.draw(canvas);
        }
    }

    public void setAlpha(int i) {
        this.icon.setAlpha(i);
        this.background.setAlpha(i);
    }

    public int getIntrinsicWidth() {
        return this.backWidth != 0 ? this.backWidth : this.background.getIntrinsicWidth();
    }

    public int getIntrinsicHeight() {
        return this.backHeight != 0 ? this.backHeight : this.background.getIntrinsicHeight();
    }

    public int getMinimumWidth() {
        return this.backWidth != 0 ? this.backWidth : this.background.getMinimumWidth();
    }

    public int getMinimumHeight() {
        return this.backHeight != 0 ? this.backHeight : this.background.getMinimumHeight();
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
