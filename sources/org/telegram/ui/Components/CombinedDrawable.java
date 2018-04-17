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

    public CombinedDrawable(Drawable backgroundDrawable, Drawable iconDrawable, int leftOffset, int topOffset) {
        this.background = backgroundDrawable;
        this.icon = iconDrawable;
        this.left = leftOffset;
        this.top = topOffset;
        if (iconDrawable != null) {
            iconDrawable.setCallback(this);
        }
    }

    public void setIconSize(int width, int height) {
        this.iconWidth = width;
        this.iconHeight = height;
    }

    public CombinedDrawable(Drawable backgroundDrawable, Drawable iconDrawable) {
        this.background = backgroundDrawable;
        this.icon = iconDrawable;
        if (iconDrawable != null) {
            iconDrawable.setCallback(this);
        }
    }

    public void setCustomSize(int width, int height) {
        this.backWidth = width;
        this.backHeight = height;
    }

    public void setIconOffset(int x, int y) {
        this.offsetX = x;
        this.offsetY = y;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public Drawable getBackground() {
        return this.background;
    }

    public void setFullsize(boolean value) {
        this.fullSize = value;
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.icon.setColorFilter(colorFilter);
    }

    public boolean isStateful() {
        return this.icon.isStateful();
    }

    public boolean setState(int[] stateSet) {
        this.icon.setState(stateSet);
        return true;
    }

    public int[] getState() {
        return this.icon.getState();
    }

    protected boolean onStateChange(int[] state) {
        return true;
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
                x = ((getBounds().centerX() - (this.iconWidth / 2)) + this.left) + this.offsetX;
                y = ((getBounds().centerY() - (this.iconHeight / 2)) + this.top) + this.offsetY;
                this.icon.setBounds(x, y, this.iconWidth + x, this.iconHeight + y);
            } else {
                x = (getBounds().centerX() - (this.icon.getIntrinsicWidth() / 2)) + this.left;
                y = (getBounds().centerY() - (this.icon.getIntrinsicHeight() / 2)) + this.top;
                this.icon.setBounds(x, y, this.icon.getIntrinsicWidth() + x, this.icon.getIntrinsicHeight() + y);
            }
            this.icon.draw(canvas);
        }
    }

    public void setAlpha(int alpha) {
        this.icon.setAlpha(alpha);
        this.background.setAlpha(alpha);
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

    public void invalidateDrawable(Drawable who) {
        invalidateSelf();
    }

    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        scheduleSelf(what, when);
    }

    public void unscheduleDrawable(Drawable who, Runnable what) {
        unscheduleSelf(what);
    }
}
