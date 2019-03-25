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
    private WallpaperParallaxEffect parallaxEffect;
    private float parallaxScale = 1.0f;
    private boolean paused = true;
    private Rect rect = new Rect();
    private float translationX;
    private float translationY;

    public interface SizeNotifierFrameLayoutDelegate {
        void onSizeChanged(int i, boolean z);
    }

    public SizeNotifierFrameLayout(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public void setBackgroundImage(Drawable bitmap, boolean motion) {
        this.backgroundDrawable = bitmap;
        if (motion) {
            if (this.parallaxEffect == null) {
                this.parallaxEffect = new WallpaperParallaxEffect(getContext());
                this.parallaxEffect.setCallback(new SizeNotifierFrameLayout$$Lambda$0(this));
                if (!(getMeasuredWidth() == 0 || getMeasuredHeight() == 0)) {
                    this.parallaxScale = this.parallaxEffect.getScale(getMeasuredWidth(), getMeasuredHeight());
                }
            }
            if (!this.paused) {
                this.parallaxEffect.setEnabled(true);
            }
        } else if (this.parallaxEffect != null) {
            this.parallaxEffect.setEnabled(false);
            this.parallaxEffect = null;
            this.parallaxScale = 1.0f;
            this.translationX = 0.0f;
            this.translationY = 0.0f;
        }
        invalidate();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$setBackgroundImage$0$SizeNotifierFrameLayout(int offsetX, int offsetY) {
        this.translationX = (float) offsetX;
        this.translationY = (float) offsetY;
        invalidate();
    }

    public Drawable getBackgroundImage() {
        return this.backgroundDrawable;
    }

    public void setDelegate(SizeNotifierFrameLayoutDelegate delegate) {
        this.delegate = delegate;
    }

    public void setOccupyStatusBar(boolean value) {
        this.occupyStatusBar = value;
    }

    public void onPause() {
        if (this.parallaxEffect != null) {
            this.parallaxEffect.setEnabled(false);
        }
        this.paused = true;
    }

    public void onResume() {
        if (this.parallaxEffect != null) {
            this.parallaxEffect.setEnabled(true);
        }
        this.paused = false;
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        notifyHeightChanged();
    }

    public int getKeyboardHeight() {
        View rootView = getRootView();
        getWindowVisibleDisplayFrame(this.rect);
        return ((rootView.getHeight() - (this.rect.top != 0 ? AndroidUtilities.statusBarHeight : 0)) - AndroidUtilities.getViewInset(rootView)) - (this.rect.bottom - this.rect.top);
    }

    public void notifyHeightChanged() {
        if (this.delegate != null) {
            if (this.parallaxEffect != null) {
                this.parallaxScale = this.parallaxEffect.getScale(getMeasuredWidth(), getMeasuredHeight());
            }
            this.keyboardHeight = getKeyboardHeight();
            post(new SizeNotifierFrameLayout$$Lambda$1(this, AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$notifyHeightChanged$1$SizeNotifierFrameLayout(boolean isWidthGreater) {
        if (this.delegate != null) {
            this.delegate.onSizeChanged(this.keyboardHeight, isWidthGreater);
        }
    }

    public void setBottomClip(int value) {
        this.bottomClip = value;
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
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
            float scale;
            if (this.backgroundDrawable.getTileModeX() == TileMode.REPEAT) {
                canvas.save();
                scale = 2.0f / AndroidUtilities.density;
                canvas.scale(scale, scale);
                this.backgroundDrawable.setBounds(0, 0, (int) Math.ceil((double) (((float) getMeasuredWidth()) / scale)), (int) Math.ceil((double) (((float) getMeasuredHeight()) / scale)));
                this.backgroundDrawable.draw(canvas);
                canvas.restore();
                return;
            }
            int currentActionBarHeight = isActionBarVisible() ? ActionBar.getCurrentActionBarHeight() : 0;
            int i = (VERSION.SDK_INT < 21 || !this.occupyStatusBar) ? 0 : AndroidUtilities.statusBarHeight;
            int actionBarHeight = currentActionBarHeight + i;
            int viewHeight = getMeasuredHeight() - actionBarHeight;
            float scaleX = ((float) getMeasuredWidth()) / ((float) this.backgroundDrawable.getIntrinsicWidth());
            float scaleY = ((float) (this.keyboardHeight + viewHeight)) / ((float) this.backgroundDrawable.getIntrinsicHeight());
            if (scaleX < scaleY) {
                scale = scaleY;
            } else {
                scale = scaleX;
            }
            int width = (int) Math.ceil((double) ((((float) this.backgroundDrawable.getIntrinsicWidth()) * scale) * this.parallaxScale));
            int height = (int) Math.ceil((double) ((((float) this.backgroundDrawable.getIntrinsicHeight()) * scale) * this.parallaxScale));
            int x = ((getMeasuredWidth() - width) / 2) + ((int) this.translationX);
            int y = ((((viewHeight - height) + this.keyboardHeight) / 2) + actionBarHeight) + ((int) this.translationY);
            canvas.save();
            canvas.clipRect(0, actionBarHeight, width, getMeasuredHeight() - this.bottomClip);
            this.backgroundDrawable.setBounds(x, y, x + width, y + height);
            this.backgroundDrawable.draw(canvas);
            canvas.restore();
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean isActionBarVisible() {
        return true;
    }
}
