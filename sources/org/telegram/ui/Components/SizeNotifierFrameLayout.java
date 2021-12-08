package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.Theme;

public class SizeNotifierFrameLayout extends FrameLayout {
    protected AdjustPanLayoutHelper adjustPanLayoutHelper;
    private boolean animationInProgress;
    private Drawable backgroundDrawable;
    private int backgroundTranslationY;
    private float bgAngle;
    private int bottomClip;
    private SizeNotifierFrameLayoutDelegate delegate;
    private int emojiHeight;
    private float emojiOffset;
    protected int keyboardHeight;
    private boolean occupyStatusBar;
    private Drawable oldBackgroundDrawable;
    private WallpaperParallaxEffect parallaxEffect;
    private float parallaxScale;
    private ActionBarLayout parentLayout;
    private boolean paused;
    private Rect rect;
    private boolean skipBackgroundDrawing;
    private float translationX;
    private float translationY;

    public interface SizeNotifierFrameLayoutDelegate {
        void onSizeChanged(int i, boolean z);
    }

    public SizeNotifierFrameLayout(Context context) {
        this(context, (ActionBarLayout) null);
    }

    public SizeNotifierFrameLayout(Context context, ActionBarLayout layout) {
        super(context);
        this.rect = new Rect();
        this.occupyStatusBar = true;
        this.parallaxScale = 1.0f;
        this.paused = true;
        setWillNotDraw(false);
        this.parentLayout = layout;
        this.adjustPanLayoutHelper = createAdjustPanLayoutHelper();
    }

    public void setBackgroundImage(Drawable bitmap, boolean motion) {
        if (this.backgroundDrawable != bitmap) {
            if (bitmap instanceof MotionBackgroundDrawable) {
                ((MotionBackgroundDrawable) bitmap).setParentView(this);
            }
            this.backgroundDrawable = bitmap;
            if (motion) {
                if (this.parallaxEffect == null) {
                    WallpaperParallaxEffect wallpaperParallaxEffect = new WallpaperParallaxEffect(getContext());
                    this.parallaxEffect = wallpaperParallaxEffect;
                    wallpaperParallaxEffect.setCallback(new SizeNotifierFrameLayout$$ExternalSyntheticLambda1(this));
                    if (!(getMeasuredWidth() == 0 || getMeasuredHeight() == 0)) {
                        this.parallaxScale = this.parallaxEffect.getScale(getMeasuredWidth(), getMeasuredHeight());
                    }
                }
                if (!this.paused) {
                    this.parallaxEffect.setEnabled(true);
                }
            } else {
                WallpaperParallaxEffect wallpaperParallaxEffect2 = this.parallaxEffect;
                if (wallpaperParallaxEffect2 != null) {
                    wallpaperParallaxEffect2.setEnabled(false);
                    this.parallaxEffect = null;
                    this.parallaxScale = 1.0f;
                    this.translationX = 0.0f;
                    this.translationY = 0.0f;
                }
            }
            invalidate();
        }
    }

    /* renamed from: lambda$setBackgroundImage$0$org-telegram-ui-Components-SizeNotifierFrameLayout  reason: not valid java name */
    public /* synthetic */ void m2609x49b6021f(int offsetX, int offsetY, float angle) {
        this.translationX = (float) offsetX;
        this.translationY = (float) offsetY;
        this.bgAngle = angle;
        invalidate();
    }

    public Drawable getBackgroundImage() {
        return this.backgroundDrawable;
    }

    public void setDelegate(SizeNotifierFrameLayoutDelegate delegate2) {
        this.delegate = delegate2;
    }

    public void setOccupyStatusBar(boolean value) {
        this.occupyStatusBar = value;
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

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        notifyHeightChanged();
    }

    public int measureKeyboardHeight() {
        View rootView = getRootView();
        getWindowVisibleDisplayFrame(this.rect);
        if (this.rect.bottom == 0 && this.rect.top == 0) {
            return 0;
        }
        int max = Math.max(0, ((rootView.getHeight() - (this.rect.top != 0 ? AndroidUtilities.statusBarHeight : 0)) - AndroidUtilities.getViewInset(rootView)) - (this.rect.bottom - this.rect.top));
        this.keyboardHeight = max;
        return max;
    }

    public int getKeyboardHeight() {
        return this.keyboardHeight;
    }

    public void notifyHeightChanged() {
        WallpaperParallaxEffect wallpaperParallaxEffect = this.parallaxEffect;
        if (wallpaperParallaxEffect != null) {
            this.parallaxScale = wallpaperParallaxEffect.getScale(getMeasuredWidth(), getMeasuredHeight());
        }
        if (this.delegate != null) {
            this.keyboardHeight = measureKeyboardHeight();
            post(new SizeNotifierFrameLayout$$ExternalSyntheticLambda0(this, AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y));
        }
    }

    /* renamed from: lambda$notifyHeightChanged$1$org-telegram-ui-Components-SizeNotifierFrameLayout  reason: not valid java name */
    public /* synthetic */ void m2608xe23b7f7f(boolean isWidthGreater) {
        SizeNotifierFrameLayoutDelegate sizeNotifierFrameLayoutDelegate = this.delegate;
        if (sizeNotifierFrameLayoutDelegate != null) {
            sizeNotifierFrameLayoutDelegate.onSizeChanged(this.keyboardHeight, isWidthGreater);
        }
    }

    public void setBottomClip(int value) {
        this.bottomClip = value;
    }

    public void setBackgroundTranslation(int translation) {
        this.backgroundTranslationY = translation;
    }

    public int getBackgroundTranslationY() {
        if (!(this.backgroundDrawable instanceof MotionBackgroundDrawable)) {
            return 0;
        }
        if (this.animationInProgress) {
            return (int) this.emojiOffset;
        }
        int i = this.emojiHeight;
        if (i != 0) {
            return i;
        }
        return this.backgroundTranslationY;
    }

    public int getBackgroundSizeY() {
        int offset = 0;
        Drawable drawable = this.backgroundDrawable;
        if (drawable instanceof MotionBackgroundDrawable) {
            if (((MotionBackgroundDrawable) drawable).hasPattern()) {
                offset = this.backgroundTranslationY != 0 ? 0 : -this.keyboardHeight;
            } else if (this.animationInProgress) {
                offset = (int) this.emojiOffset;
            } else if (this.emojiHeight != 0) {
                offset = this.emojiHeight;
            } else {
                offset = this.backgroundTranslationY;
            }
        }
        return getMeasuredHeight() - offset;
    }

    public int getHeightWithKeyboard() {
        return this.keyboardHeight + getMeasuredHeight();
    }

    public void setEmojiKeyboardHeight(int height) {
        this.emojiHeight = height;
    }

    public void setEmojiOffset(boolean animInProgress, float offset) {
        this.emojiOffset = offset;
        this.animationInProgress = animInProgress;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Drawable newDrawable;
        int a;
        int i;
        int i2;
        Canvas canvas2 = canvas;
        if (this.backgroundDrawable == null || this.skipBackgroundDrawing) {
            super.onDraw(canvas);
            return;
        }
        Drawable newDrawable2 = getNewDrawable();
        if (!(newDrawable2 == this.backgroundDrawable || newDrawable2 == null)) {
            if (Theme.isAnimatingColor()) {
                this.oldBackgroundDrawable = this.backgroundDrawable;
            }
            if (newDrawable2 instanceof MotionBackgroundDrawable) {
                ((MotionBackgroundDrawable) newDrawable2).setParentView(this);
            }
            this.backgroundDrawable = newDrawable2;
        }
        ActionBarLayout actionBarLayout = this.parentLayout;
        float themeAnimationValue = actionBarLayout != null ? actionBarLayout.getThemeAnimationValue() : 1.0f;
        int a2 = 0;
        while (a2 < 2) {
            Drawable drawable = a2 == 0 ? this.oldBackgroundDrawable : this.backgroundDrawable;
            if (drawable == null) {
                newDrawable = newDrawable2;
                a = a2;
            } else {
                if (a2 != 1 || this.oldBackgroundDrawable == null || this.parentLayout == null) {
                    drawable.setAlpha(255);
                } else {
                    drawable.setAlpha((int) (255.0f * themeAnimationValue));
                }
                if (drawable instanceof MotionBackgroundDrawable) {
                    MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) drawable;
                    if (motionBackgroundDrawable.hasPattern()) {
                        int actionBarHeight = (isActionBarVisible() ? ActionBar.getCurrentActionBarHeight() : 0) + ((Build.VERSION.SDK_INT < 21 || !this.occupyStatusBar) ? 0 : AndroidUtilities.statusBarHeight);
                        int viewHeight = getRootView().getMeasuredHeight() - actionBarHeight;
                        float scale = Math.max(((float) getMeasuredWidth()) / ((float) drawable.getIntrinsicWidth()), ((float) viewHeight) / ((float) drawable.getIntrinsicHeight()));
                        int actionBarHeight2 = actionBarHeight;
                        int width = (int) Math.ceil((double) (((float) drawable.getIntrinsicWidth()) * scale * this.parallaxScale));
                        a = a2;
                        int height = (int) Math.ceil((double) (((float) drawable.getIntrinsicHeight()) * scale * this.parallaxScale));
                        int x = ((getMeasuredWidth() - width) / 2) + ((int) this.translationX);
                        int y = this.backgroundTranslationY + ((viewHeight - height) / 2) + actionBarHeight2 + ((int) this.translationY);
                        canvas.save();
                        newDrawable = newDrawable2;
                        int actionBarHeight3 = actionBarHeight2;
                        int actionBarHeight4 = viewHeight;
                        canvas2.clipRect(0, actionBarHeight3, width, getMeasuredHeight() - this.bottomClip);
                        drawable.setBounds(x, y, x + width, y + height);
                        drawable.draw(canvas2);
                        canvas.restore();
                    } else {
                        newDrawable = newDrawable2;
                        a = a2;
                        if (this.bottomClip != 0) {
                            canvas.save();
                            canvas2.clipRect(0, 0, getMeasuredWidth(), getRootView().getMeasuredHeight() - this.bottomClip);
                        }
                        motionBackgroundDrawable.setTranslationY(this.backgroundTranslationY);
                        int bottom = getMeasuredHeight() - this.backgroundTranslationY;
                        if (this.animationInProgress) {
                            bottom = (int) (((float) bottom) - this.emojiOffset);
                        } else {
                            int i3 = this.emojiHeight;
                            if (i3 != 0) {
                                bottom -= i3;
                            }
                        }
                        drawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                        drawable.draw(canvas2);
                        if (this.bottomClip != 0) {
                            canvas.restore();
                        }
                    }
                } else {
                    newDrawable = newDrawable2;
                    a = a2;
                    if (drawable instanceof ColorDrawable) {
                        if (this.bottomClip != 0) {
                            canvas.save();
                            i2 = 0;
                            canvas2.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - this.bottomClip);
                        } else {
                            i2 = 0;
                        }
                        drawable.setBounds(i2, i2, getMeasuredWidth(), getRootView().getMeasuredHeight());
                        drawable.draw(canvas2);
                        if (this.bottomClip != 0) {
                            canvas.restore();
                        }
                    } else if (drawable instanceof GradientDrawable) {
                        if (this.bottomClip != 0) {
                            canvas.save();
                            i = 0;
                            canvas2.clipRect(0, 0, getMeasuredWidth(), getRootView().getMeasuredHeight() - this.bottomClip);
                        } else {
                            i = 0;
                        }
                        drawable.setBounds(i, this.backgroundTranslationY, getMeasuredWidth(), this.backgroundTranslationY + getRootView().getMeasuredHeight());
                        drawable.draw(canvas2);
                        if (this.bottomClip != 0) {
                            canvas.restore();
                        }
                    } else if (drawable instanceof BitmapDrawable) {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                        if (bitmapDrawable.getTileModeX() == Shader.TileMode.REPEAT) {
                            canvas.save();
                            float scale2 = 2.0f / AndroidUtilities.density;
                            canvas2.scale(scale2, scale2);
                            drawable.setBounds(0, 0, (int) Math.ceil((double) (((float) getMeasuredWidth()) / scale2)), (int) Math.ceil((double) (((float) getRootView().getMeasuredHeight()) / scale2)));
                            drawable.draw(canvas2);
                            canvas.restore();
                        } else {
                            int actionBarHeight5 = (isActionBarVisible() ? ActionBar.getCurrentActionBarHeight() : 0) + ((Build.VERSION.SDK_INT < 21 || !this.occupyStatusBar) ? 0 : AndroidUtilities.statusBarHeight);
                            int viewHeight2 = getRootView().getMeasuredHeight() - actionBarHeight5;
                            float scale3 = Math.max(((float) getMeasuredWidth()) / ((float) drawable.getIntrinsicWidth()), ((float) viewHeight2) / ((float) drawable.getIntrinsicHeight()));
                            int width2 = (int) Math.ceil((double) (((float) drawable.getIntrinsicWidth()) * scale3 * this.parallaxScale));
                            int height2 = (int) Math.ceil((double) (((float) drawable.getIntrinsicHeight()) * scale3 * this.parallaxScale));
                            int x2 = ((getMeasuredWidth() - width2) / 2) + ((int) this.translationX);
                            int y2 = this.backgroundTranslationY + ((viewHeight2 - height2) / 2) + actionBarHeight5 + ((int) this.translationY);
                            canvas.save();
                            BitmapDrawable bitmapDrawable2 = bitmapDrawable;
                            canvas2.clipRect(0, actionBarHeight5, width2, getMeasuredHeight() - this.bottomClip);
                            drawable.setBounds(x2, y2, x2 + width2, y2 + height2);
                            drawable.draw(canvas2);
                            canvas.restore();
                        }
                    }
                }
                if (a == 0 && this.oldBackgroundDrawable != null) {
                    if (themeAnimationValue >= 1.0f) {
                        this.oldBackgroundDrawable = null;
                        invalidate();
                    }
                }
            }
            a2 = a + 1;
            newDrawable2 = newDrawable;
        }
    }

    /* access modifiers changed from: protected */
    public boolean isActionBarVisible() {
        return true;
    }

    /* access modifiers changed from: protected */
    public AdjustPanLayoutHelper createAdjustPanLayoutHelper() {
        return null;
    }

    public void setSkipBackgroundDrawing(boolean skipBackgroundDrawing2) {
        this.skipBackgroundDrawing = skipBackgroundDrawing2;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public Drawable getNewDrawable() {
        return Theme.getCachedWallpaperNonBlocking();
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable who) {
        return who == getBackgroundImage() || super.verifyDrawable(who);
    }
}
