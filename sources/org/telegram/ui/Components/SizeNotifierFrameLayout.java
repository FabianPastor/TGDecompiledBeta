package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.ActionBar;

public class SizeNotifierFrameLayout extends FrameLayout {
    private Drawable backgroundDrawable;
    private int bottomClip;
    private SizeNotifierFrameLayoutDelegate delegate;
    private int keyboardHeight;
    private boolean occupyStatusBar = true;
    private Rect rect = new Rect();

    public interface SizeNotifierFrameLayoutDelegate {
        void onSizeChanged(int i, boolean z);
    }

    protected boolean isActionBarVisible() {
        return true;
    }

    public SizeNotifierFrameLayout(Context context) {
        super(context);
        setWillNotDraw(null);
    }

    public void setBackgroundImage(Drawable drawable) {
        this.backgroundDrawable = drawable;
        invalidate();
    }

    public Drawable getBackgroundImage() {
        return this.backgroundDrawable;
    }

    public void setDelegate(SizeNotifierFrameLayoutDelegate sizeNotifierFrameLayoutDelegate) {
        this.delegate = sizeNotifierFrameLayoutDelegate;
    }

    public void setOccupyStatusBar(boolean z) {
        this.occupyStatusBar = z;
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        notifyHeightChanged();
    }

    public int getKeyboardHeight() {
        View rootView = getRootView();
        getWindowVisibleDisplayFrame(this.rect);
        return ((rootView.getHeight() - (this.rect.top != 0 ? AndroidUtilities.statusBarHeight : 0)) - AndroidUtilities.getViewInset(rootView)) - (this.rect.bottom - this.rect.top);
    }

    public void notifyHeightChanged() {
        if (this.delegate != null) {
            this.keyboardHeight = getKeyboardHeight();
            final boolean z = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y;
            post(new Runnable() {
                public void run() {
                    if (SizeNotifierFrameLayout.this.delegate != null) {
                        SizeNotifierFrameLayout.this.delegate.onSizeChanged(SizeNotifierFrameLayout.this.keyboardHeight, z);
                    }
                }
            });
        }
    }

    public void setBottomClip(int i) {
        this.bottomClip = i;
    }

    protected void onDraw(Canvas canvas) {
        if (this.backgroundDrawable == null) {
            super.onDraw(canvas);
        } else if (this.backgroundDrawable instanceof ColorDrawable) {
            if (this.bottomClip != 0) {
                canvas.save();
                canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - this.bottomClip);
            }
            this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.backgroundDrawable.draw(canvas);
            if (this.bottomClip != 0) {
                canvas.restore();
            }
        } else if (!(this.backgroundDrawable instanceof BitmapDrawable)) {
        } else {
            if (((BitmapDrawable) this.backgroundDrawable).getTileModeX() == TileMode.REPEAT) {
                canvas.save();
                float f = 2.0f / AndroidUtilities.density;
                canvas.scale(f, f);
                this.backgroundDrawable.setBounds(0, 0, (int) Math.ceil((double) (((float) getMeasuredWidth()) / f)), (int) Math.ceil((double) (((float) getMeasuredHeight()) / f)));
                this.backgroundDrawable.draw(canvas);
                canvas.restore();
                return;
            }
            int currentActionBarHeight = isActionBarVisible() ? ActionBar.getCurrentActionBarHeight() : 0;
            int i = (VERSION.SDK_INT < 21 || !this.occupyStatusBar) ? 0 : AndroidUtilities.statusBarHeight;
            currentActionBarHeight += i;
            i = getMeasuredHeight() - currentActionBarHeight;
            float measuredWidth = ((float) getMeasuredWidth()) / ((float) this.backgroundDrawable.getIntrinsicWidth());
            float intrinsicHeight = ((float) (this.keyboardHeight + i)) / ((float) this.backgroundDrawable.getIntrinsicHeight());
            if (measuredWidth < intrinsicHeight) {
                measuredWidth = intrinsicHeight;
            }
            int ceil = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicWidth()) * measuredWidth));
            int ceil2 = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicHeight()) * measuredWidth));
            int measuredWidth2 = (getMeasuredWidth() - ceil) / 2;
            i = (((i - ceil2) + this.keyboardHeight) / 2) + currentActionBarHeight;
            canvas.save();
            canvas.clipRect(0, currentActionBarHeight, ceil, getMeasuredHeight() - this.bottomClip);
            this.backgroundDrawable.setBounds(measuredWidth2, i, ceil + measuredWidth2, ceil2 + i);
            this.backgroundDrawable.draw(canvas);
            canvas.restore();
        }
    }
}
