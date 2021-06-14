package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
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
import org.telegram.ui.Components.WallpaperParallaxEffect;

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
    private float translationX;
    private float translationY;

    public interface SizeNotifierFrameLayoutDelegate {
        void onSizeChanged(int i, boolean z);
    }

    /* access modifiers changed from: protected */
    public AdjustPanLayoutHelper createAdjustPanLayoutHelper() {
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean isActionBarVisible() {
        return true;
    }

    public SizeNotifierFrameLayout(Context context) {
        this(context, (ActionBarLayout) null);
    }

    public SizeNotifierFrameLayout(Context context, ActionBarLayout actionBarLayout) {
        super(context);
        this.rect = new Rect();
        this.occupyStatusBar = true;
        this.parallaxScale = 1.0f;
        this.paused = true;
        setWillNotDraw(false);
        this.parentLayout = actionBarLayout;
        this.adjustPanLayoutHelper = createAdjustPanLayoutHelper();
    }

    public void setBackgroundImage(Drawable drawable, boolean z) {
        if (drawable instanceof MotionBackgroundDrawable) {
            ((MotionBackgroundDrawable) drawable).setParentView(this);
        }
        this.backgroundDrawable = drawable;
        if (z) {
            if (this.parallaxEffect == null) {
                WallpaperParallaxEffect wallpaperParallaxEffect = new WallpaperParallaxEffect(getContext());
                this.parallaxEffect = wallpaperParallaxEffect;
                wallpaperParallaxEffect.setCallback(new WallpaperParallaxEffect.Callback() {
                    public final void onOffsetsChanged(int i, int i2, float f) {
                        SizeNotifierFrameLayout.this.lambda$setBackgroundImage$0$SizeNotifierFrameLayout(i, i2, f);
                    }
                });
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$setBackgroundImage$0 */
    public /* synthetic */ void lambda$setBackgroundImage$0$SizeNotifierFrameLayout(int i, int i2, float f) {
        this.translationX = (float) i;
        this.translationY = (float) i2;
        this.bgAngle = f;
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

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        notifyHeightChanged();
    }

    public int measureKeyboardHeight() {
        View rootView = getRootView();
        getWindowVisibleDisplayFrame(this.rect);
        Rect rect2 = this.rect;
        if (rect2.bottom == 0 && rect2.top == 0) {
            return 0;
        }
        int height = (rootView.getHeight() - (this.rect.top != 0 ? AndroidUtilities.statusBarHeight : 0)) - AndroidUtilities.getViewInset(rootView);
        Rect rect3 = this.rect;
        int max = Math.max(0, height - (rect3.bottom - rect3.top));
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
            Point point = AndroidUtilities.displaySize;
            post(new Runnable(point.x > point.y) {
                public final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SizeNotifierFrameLayout.this.lambda$notifyHeightChanged$1$SizeNotifierFrameLayout(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$notifyHeightChanged$1 */
    public /* synthetic */ void lambda$notifyHeightChanged$1$SizeNotifierFrameLayout(boolean z) {
        SizeNotifierFrameLayoutDelegate sizeNotifierFrameLayoutDelegate = this.delegate;
        if (sizeNotifierFrameLayoutDelegate != null) {
            sizeNotifierFrameLayoutDelegate.onSizeChanged(this.keyboardHeight, z);
        }
    }

    public void setBottomClip(int i) {
        this.bottomClip = i;
    }

    public void setBackgroundTranslation(int i) {
        this.backgroundTranslationY = i;
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
        Drawable drawable = this.backgroundDrawable;
        int i = 0;
        if (drawable instanceof MotionBackgroundDrawable) {
            if (!((MotionBackgroundDrawable) drawable).hasPattern()) {
                if (this.animationInProgress) {
                    i = (int) this.emojiOffset;
                } else {
                    i = this.emojiHeight;
                    if (i == 0) {
                        i = this.backgroundTranslationY;
                    }
                }
            } else if (this.backgroundTranslationY == 0) {
                i = -this.keyboardHeight;
            }
        }
        return getMeasuredHeight() - i;
    }

    public int getHeightWithKeyboard() {
        return this.keyboardHeight + getMeasuredHeight();
    }

    public void setEmojiKeyboardHeight(int i) {
        this.emojiHeight = i;
    }

    public void setEmojiOffset(boolean z, float f) {
        this.emojiOffset = f;
        this.animationInProgress = z;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.backgroundDrawable == null) {
            super.onDraw(canvas);
            return;
        }
        Drawable cachedWallpaperNonBlocking = Theme.getCachedWallpaperNonBlocking();
        if (!(cachedWallpaperNonBlocking == this.backgroundDrawable || cachedWallpaperNonBlocking == null)) {
            if (Theme.isAnimatingColor()) {
                this.oldBackgroundDrawable = this.backgroundDrawable;
            }
            if (cachedWallpaperNonBlocking instanceof MotionBackgroundDrawable) {
                ((MotionBackgroundDrawable) cachedWallpaperNonBlocking).setParentView(this);
            }
            this.backgroundDrawable = cachedWallpaperNonBlocking;
        }
        ActionBarLayout actionBarLayout = this.parentLayout;
        float themeAnimationValue = actionBarLayout != null ? actionBarLayout.getThemeAnimationValue() : 1.0f;
        int i = 0;
        while (i < 2) {
            Drawable drawable = i == 0 ? this.oldBackgroundDrawable : this.backgroundDrawable;
            if (drawable != null) {
                if (i != 1 || this.oldBackgroundDrawable == null || this.parentLayout == null) {
                    drawable.setAlpha(255);
                } else {
                    drawable.setAlpha((int) (255.0f * themeAnimationValue));
                }
                if (drawable instanceof MotionBackgroundDrawable) {
                    MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) drawable;
                    if (motionBackgroundDrawable.hasPattern()) {
                        int currentActionBarHeight = (isActionBarVisible() ? ActionBar.getCurrentActionBarHeight() : 0) + ((Build.VERSION.SDK_INT < 21 || !this.occupyStatusBar) ? 0 : AndroidUtilities.statusBarHeight);
                        int measuredHeight = getRootView().getMeasuredHeight() - currentActionBarHeight;
                        float max = Math.max(((float) getMeasuredWidth()) / ((float) drawable.getIntrinsicWidth()), ((float) measuredHeight) / ((float) drawable.getIntrinsicHeight()));
                        int ceil = (int) Math.ceil((double) (((float) drawable.getIntrinsicWidth()) * max * this.parallaxScale));
                        int ceil2 = (int) Math.ceil((double) (((float) drawable.getIntrinsicHeight()) * max * this.parallaxScale));
                        int measuredWidth = ((getMeasuredWidth() - ceil) / 2) + ((int) this.translationX);
                        int i2 = this.backgroundTranslationY + ((measuredHeight - ceil2) / 2) + currentActionBarHeight + ((int) this.translationY);
                        canvas.save();
                        canvas.clipRect(0, currentActionBarHeight, ceil, getMeasuredHeight() - this.bottomClip);
                        drawable.setBounds(measuredWidth, i2, ceil + measuredWidth, ceil2 + i2);
                        drawable.draw(canvas);
                        canvas.restore();
                    } else {
                        if (this.bottomClip != 0) {
                            canvas.save();
                            canvas.clipRect(0, 0, getMeasuredWidth(), getRootView().getMeasuredHeight() - this.bottomClip);
                        }
                        motionBackgroundDrawable.setTranslationY(this.backgroundTranslationY);
                        int measuredHeight2 = getMeasuredHeight() - this.backgroundTranslationY;
                        if (this.animationInProgress) {
                            measuredHeight2 = (int) (((float) measuredHeight2) - this.emojiOffset);
                        } else {
                            int i3 = this.emojiHeight;
                            if (i3 != 0) {
                                measuredHeight2 -= i3;
                            }
                        }
                        drawable.setBounds(0, 0, getMeasuredWidth(), measuredHeight2);
                        drawable.draw(canvas);
                        if (this.bottomClip != 0) {
                            canvas.restore();
                        }
                    }
                } else if (drawable instanceof ColorDrawable) {
                    if (this.bottomClip != 0) {
                        canvas.save();
                        canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - this.bottomClip);
                    }
                    drawable.setBounds(0, 0, getMeasuredWidth(), getRootView().getMeasuredHeight());
                    drawable.draw(canvas);
                    if (this.bottomClip != 0) {
                        canvas.restore();
                    }
                } else if (drawable instanceof GradientDrawable) {
                    if (this.bottomClip != 0) {
                        canvas.save();
                        canvas.clipRect(0, 0, getMeasuredWidth(), getRootView().getMeasuredHeight() - this.bottomClip);
                    }
                    drawable.setBounds(0, this.backgroundTranslationY, getMeasuredWidth(), this.backgroundTranslationY + getRootView().getMeasuredHeight());
                    drawable.draw(canvas);
                    if (this.bottomClip != 0) {
                        canvas.restore();
                    }
                } else if (drawable instanceof BitmapDrawable) {
                    if (((BitmapDrawable) drawable).getTileModeX() == Shader.TileMode.REPEAT) {
                        canvas.save();
                        float f = 2.0f / AndroidUtilities.density;
                        canvas.scale(f, f);
                        drawable.setBounds(0, 0, (int) Math.ceil((double) (((float) getMeasuredWidth()) / f)), (int) Math.ceil((double) (((float) getRootView().getMeasuredHeight()) / f)));
                        drawable.draw(canvas);
                        canvas.restore();
                    } else {
                        int currentActionBarHeight2 = (isActionBarVisible() ? ActionBar.getCurrentActionBarHeight() : 0) + ((Build.VERSION.SDK_INT < 21 || !this.occupyStatusBar) ? 0 : AndroidUtilities.statusBarHeight);
                        int measuredHeight3 = getRootView().getMeasuredHeight() - currentActionBarHeight2;
                        float max2 = Math.max(((float) getMeasuredWidth()) / ((float) drawable.getIntrinsicWidth()), ((float) measuredHeight3) / ((float) drawable.getIntrinsicHeight()));
                        int ceil3 = (int) Math.ceil((double) (((float) drawable.getIntrinsicWidth()) * max2 * this.parallaxScale));
                        int ceil4 = (int) Math.ceil((double) (((float) drawable.getIntrinsicHeight()) * max2 * this.parallaxScale));
                        int measuredWidth2 = ((getMeasuredWidth() - ceil3) / 2) + ((int) this.translationX);
                        int i4 = this.backgroundTranslationY + ((measuredHeight3 - ceil4) / 2) + currentActionBarHeight2 + ((int) this.translationY);
                        canvas.save();
                        canvas.clipRect(0, currentActionBarHeight2, ceil3, getMeasuredHeight() - this.bottomClip);
                        drawable.setBounds(measuredWidth2, i4, ceil3 + measuredWidth2, ceil4 + i4);
                        drawable.draw(canvas);
                        canvas.restore();
                    }
                }
                if (i == 0 && this.oldBackgroundDrawable != null && themeAnimationValue >= 1.0f) {
                    this.oldBackgroundDrawable = null;
                    invalidate();
                }
            }
            i++;
        }
    }
}
