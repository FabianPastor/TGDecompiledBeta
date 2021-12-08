package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class CombinedDrawable extends Drawable implements Drawable.Callback {
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

    /* access modifiers changed from: protected */
    public boolean onStateChange(int[] state) {
        return true;
    }

    public void jumpToCurrentState() {
        this.icon.jumpToCurrentState();
    }

    public Drawable.ConstantState getConstantState() {
        return this.icon.getConstantState();
    }

    public void draw(Canvas canvas) {
        this.background.setBounds(getBounds());
        this.background.draw(canvas);
        if (this.icon != null) {
            if (this.fullSize) {
                Rect bounds = getBounds();
                if (this.left != 0) {
                    this.icon.setBounds(bounds.left + this.left, bounds.top + this.top, bounds.right - this.left, bounds.bottom - this.top);
                } else {
                    this.icon.setBounds(bounds);
                }
            } else if (this.iconWidth != 0) {
                int x = (getBounds().centerX() - (this.iconWidth / 2)) + this.left + this.offsetX;
                int centerY = getBounds().centerY();
                int i = this.iconHeight;
                int y = (centerY - (i / 2)) + this.top + this.offsetY;
                this.icon.setBounds(x, y, this.iconWidth + x, i + y);
            } else {
                int x2 = (getBounds().centerX() - (this.icon.getIntrinsicWidth() / 2)) + this.left;
                int y2 = (getBounds().centerY() - (this.icon.getIntrinsicHeight() / 2)) + this.top;
                Drawable drawable = this.icon;
                drawable.setBounds(x2, y2, drawable.getIntrinsicWidth() + x2, this.icon.getIntrinsicHeight() + y2);
            }
            this.icon.draw(canvas);
        }
    }

    public void setAlpha(int alpha) {
        this.icon.setAlpha(alpha);
        this.background.setAlpha(alpha);
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
