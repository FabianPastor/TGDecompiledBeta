package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
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
    private WallpaperParallaxEffect parallaxEffect;
    private float parallaxScale = 1.0f;
    private boolean paused = true;
    private Rect rect = new Rect();
    private float translationX;
    private float translationY;

    public interface SizeNotifierFrameLayoutDelegate {
        void onSizeChanged(int i, boolean z);
    }

    /* Access modifiers changed, original: protected */
    public boolean isActionBarVisible() {
        return true;
    }

    public SizeNotifierFrameLayout(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public void setBackgroundImage(Drawable drawable, boolean z) {
        this.backgroundDrawable = drawable;
        if (z) {
            if (this.parallaxEffect == null) {
                this.parallaxEffect = new WallpaperParallaxEffect(getContext());
                this.parallaxEffect.setCallback(new -$$Lambda$SizeNotifierFrameLayout$9xVW1u9E8sqKGYKEgYUca0gqvJA(this));
                if (!(getMeasuredWidth() == 0 || getMeasuredHeight() == 0)) {
                    this.parallaxScale = this.parallaxEffect.getScale(getMeasuredWidth(), getMeasuredHeight());
                }
            }
            if (!this.paused) {
                this.parallaxEffect.setEnabled(true);
            }
        } else {
            WallpaperParallaxEffect wallpaperParallaxEffect = this.parallaxEffect;
            if (wallpaperParallaxEffect != null) {
                wallpaperParallaxEffect.setEnabled(false);
                this.parallaxEffect = null;
                this.parallaxScale = 1.0f;
                this.translationX = 0.0f;
                this.translationY = 0.0f;
            }
        }
        invalidate();
    }

    public /* synthetic */ void lambda$setBackgroundImage$0$SizeNotifierFrameLayout(int i, int i2) {
        this.translationX = (float) i;
        this.translationY = (float) i2;
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

    public void onPause() {
        WallpaperParallaxEffect wallpaperParallaxEffect = this.parallaxEffect;
        if (wallpaperParallaxEffect != null) {
            wallpaperParallaxEffect.setEnabled(false);
        }
        this.paused = true;
    }

    public void onResume() {
        WallpaperParallaxEffect wallpaperParallaxEffect = this.parallaxEffect;
        if (wallpaperParallaxEffect != null) {
            wallpaperParallaxEffect.setEnabled(true);
        }
        this.paused = false;
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        notifyHeightChanged();
    }

    public int getKeyboardHeight() {
        View rootView = getRootView();
        getWindowVisibleDisplayFrame(this.rect);
        int height = (rootView.getHeight() - (this.rect.top != 0 ? AndroidUtilities.statusBarHeight : 0)) - AndroidUtilities.getViewInset(rootView);
        Rect rect = this.rect;
        return height - (rect.bottom - rect.top);
    }

    public void notifyHeightChanged() {
        if (this.delegate != null) {
            WallpaperParallaxEffect wallpaperParallaxEffect = this.parallaxEffect;
            if (wallpaperParallaxEffect != null) {
                this.parallaxScale = wallpaperParallaxEffect.getScale(getMeasuredWidth(), getMeasuredHeight());
            }
            this.keyboardHeight = getKeyboardHeight();
            Point point = AndroidUtilities.displaySize;
            post(new -$$Lambda$SizeNotifierFrameLayout$k4g_DFX6SvnMtnN4qrfdhEQSo18(this, point.x > point.y));
        }
    }

    public /* synthetic */ void lambda$notifyHeightChanged$1$SizeNotifierFrameLayout(boolean z) {
        SizeNotifierFrameLayoutDelegate sizeNotifierFrameLayoutDelegate = this.delegate;
        if (sizeNotifierFrameLayoutDelegate != null) {
            sizeNotifierFrameLayoutDelegate.onSizeChanged(this.keyboardHeight, z);
        }
    }

    public void setBottomClip(int i) {
        this.bottomClip = i;
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        Drawable drawable = this.backgroundDrawable;
        if (drawable == null) {
            super.onDraw(canvas);
        } else if (drawable instanceof ColorDrawable) {
            if (this.bottomClip != 0) {
                canvas.save();
                canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - this.bottomClip);
            }
            this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.backgroundDrawable.draw(canvas);
            if (this.bottomClip != 0) {
                canvas.restore();
            }
        } else if (!(drawable instanceof BitmapDrawable)) {
        } else {
            if (((BitmapDrawable) drawable).getTileModeX() == TileMode.REPEAT) {
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
            int ceil = (int) Math.ceil((double) ((((float) this.backgroundDrawable.getIntrinsicWidth()) * measuredWidth) * this.parallaxScale));
            int ceil2 = (int) Math.ceil((double) ((((float) this.backgroundDrawable.getIntrinsicHeight()) * measuredWidth) * this.parallaxScale));
            int measuredWidth2 = ((getMeasuredWidth() - ceil) / 2) + ((int) this.translationX);
            i = ((((i - ceil2) + this.keyboardHeight) / 2) + currentActionBarHeight) + ((int) this.translationY);
            canvas.save();
            canvas.clipRect(0, currentActionBarHeight, ceil, getMeasuredHeight() - this.bottomClip);
            this.backgroundDrawable.setBounds(measuredWidth2, i, ceil + measuredWidth2, ceil2 + i);
            this.backgroundDrawable.draw(canvas);
            canvas.restore();
        }
    }
}
