package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
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
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.Theme;

public class SizeNotifierFrameLayout extends FrameLayout {
    private static DispatchQueue blurQueue;
    protected AdjustPanLayoutHelper adjustPanLayoutHelper;
    private boolean animationInProgress;
    private Drawable backgroundDrawable;
    private int backgroundTranslationY;
    public ArrayList<View> blurBehindViews;
    ValueAnimator blurCrossfade;
    public float blurCrossfadeProgress;
    public boolean blurGeneratingTuskIsRunning;
    public boolean blurIsRunning;
    public Paint blurPaintBottom;
    public Paint blurPaintBottom2;
    public Paint blurPaintTop;
    public Paint blurPaintTop2;
    private int bottomClip;
    int count;
    BlurBitmap currentBitmap;
    private SizeNotifierFrameLayoutDelegate delegate;
    private int emojiHeight;
    private float emojiOffset;
    public boolean invalidateBlur;
    protected int keyboardHeight;
    Matrix matrix;
    public boolean needBlur;
    private boolean occupyStatusBar;
    private Drawable oldBackgroundDrawable;
    private WallpaperParallaxEffect parallaxEffect;
    private float parallaxScale;
    private ActionBarLayout parentLayout;
    private boolean paused;
    private Rect rect;
    private boolean skipBackgroundDrawing;
    SnowflakesEffect snowflakesEffect;
    int times;
    private float translationX;
    private float translationY;
    public ArrayList<BlurBitmap> unusedBitmaps;

    public interface SizeNotifierFrameLayoutDelegate {
        void onSizeChanged(int i, boolean z);
    }

    /* access modifiers changed from: protected */
    public AdjustPanLayoutHelper createAdjustPanLayoutHelper() {
        return null;
    }

    /* access modifiers changed from: protected */
    public void drawList(Canvas canvas, boolean z) {
    }

    /* access modifiers changed from: protected */
    public Theme.ResourcesProvider getResourceProvider() {
        return null;
    }

    /* access modifiers changed from: protected */
    public int getScrollOffset() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public boolean isActionBarVisible() {
        return true;
    }

    public void invalidateBlur() {
        this.invalidateBlur = true;
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
        this.unusedBitmaps = new ArrayList<>(10);
        this.blurBehindViews = new ArrayList<>();
        this.matrix = new Matrix();
        this.blurPaintTop = new Paint();
        this.blurPaintTop2 = new Paint();
        this.blurPaintBottom = new Paint();
        this.blurPaintBottom2 = new Paint();
        setWillNotDraw(false);
        this.parentLayout = actionBarLayout;
        this.adjustPanLayoutHelper = createAdjustPanLayoutHelper();
    }

    public void setBackgroundImage(Drawable drawable, boolean z) {
        if (this.backgroundDrawable != drawable) {
            if (drawable instanceof MotionBackgroundDrawable) {
                ((MotionBackgroundDrawable) drawable).setParentView(this);
            }
            this.backgroundDrawable = drawable;
            if (z) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setBackgroundImage$0(int i, int i2, float f) {
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
            post(new SizeNotifierFrameLayout$$ExternalSyntheticLambda0(this, point.x > point.y));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyHeightChanged$1(boolean z) {
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
        if (this.backgroundDrawable == null || this.skipBackgroundDrawing) {
            super.onDraw(canvas);
            return;
        }
        Drawable newDrawable = getNewDrawable();
        if (!(newDrawable == this.backgroundDrawable || newDrawable == null)) {
            if (Theme.isAnimatingColor()) {
                this.oldBackgroundDrawable = this.backgroundDrawable;
            }
            if (newDrawable instanceof MotionBackgroundDrawable) {
                ((MotionBackgroundDrawable) newDrawable).setParentView(this);
            }
            this.backgroundDrawable = newDrawable;
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
                        checkSnowflake(canvas);
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
                    checkSnowflake(canvas);
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
                    checkSnowflake(canvas);
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
                        checkSnowflake(canvas);
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
                        checkSnowflake(canvas);
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

    private void checkSnowflake(Canvas canvas) {
        if (Theme.canStartHolidayAnimation()) {
            if (this.snowflakesEffect == null) {
                this.snowflakesEffect = new SnowflakesEffect(1);
            }
            this.snowflakesEffect.onDraw(this, canvas);
        }
    }

    public void setSkipBackgroundDrawing(boolean z) {
        this.skipBackgroundDrawing = z;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public Drawable getNewDrawable() {
        return Theme.getCachedWallpaperNonBlocking();
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable drawable) {
        return drawable == getBackgroundImage() || super.verifyDrawable(drawable);
    }

    public void startBlur() {
        final BlurBitmap blurBitmap;
        if (this.blurIsRunning && !this.blurGeneratingTuskIsRunning && this.invalidateBlur && SharedConfig.chatBlurEnabled()) {
            this.invalidateBlur = false;
            this.blurGeneratingTuskIsRunning = true;
            int measuredWidth = getMeasuredWidth();
            int currentActionBarHeight = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight + AndroidUtilities.dp(100.0f);
            float f = (float) currentActionBarHeight;
            int i = ((int) (f / 12.0f)) + 10;
            float f2 = (float) measuredWidth;
            int i2 = (int) (f2 / 12.0f);
            if (this.unusedBitmaps.size() > 0) {
                ArrayList<BlurBitmap> arrayList = this.unusedBitmaps;
                blurBitmap = arrayList.remove(arrayList.size() - 1);
            } else {
                blurBitmap = null;
            }
            if (blurBitmap == null) {
                blurBitmap = new BlurBitmap();
                blurBitmap.topBitmap = Bitmap.createBitmap(i2, i, Bitmap.Config.ARGB_8888);
                blurBitmap.topCanvas = new Canvas(blurBitmap.topBitmap);
                blurBitmap.bottomBitmap = Bitmap.createBitmap(i2, i, Bitmap.Config.ARGB_8888);
                blurBitmap.bottomCanvas = new Canvas(blurBitmap.bottomBitmap);
            }
            blurBitmap.topBitmap.eraseColor(0);
            blurBitmap.bottomBitmap.eraseColor(0);
            float width = ((float) blurBitmap.topBitmap.getWidth()) / f2;
            float height = ((float) (blurBitmap.topBitmap.getHeight() - 10)) / f;
            blurBitmap.topCanvas.save();
            blurBitmap.topCanvas.clipRect(0.0f, height * 10.0f, (float) blurBitmap.topBitmap.getWidth(), (float) blurBitmap.topBitmap.getHeight());
            blurBitmap.topCanvas.scale(width, height);
            blurBitmap.topScaleX = 1.0f / width;
            blurBitmap.topScaleY = 1.0f / height;
            blurBitmap.topCanvas.translate(0.0f, (float) blurBitmap.pixelFixOffset);
            drawList(blurBitmap.topCanvas, true);
            blurBitmap.topCanvas.restore();
            float width2 = ((float) blurBitmap.bottomBitmap.getWidth()) / f2;
            float height2 = ((float) (blurBitmap.bottomBitmap.getHeight() - 10)) / f;
            blurBitmap.bottomOffset = getBottomOffset() - f;
            blurBitmap.bottomCanvas.save();
            blurBitmap.bottomCanvas.clipRect(0.0f, height2 * 10.0f, (float) blurBitmap.bottomBitmap.getWidth(), (float) blurBitmap.bottomBitmap.getHeight());
            blurBitmap.bottomCanvas.scale(width2, height2);
            blurBitmap.bottomCanvas.translate(0.0f, 10.0f - blurBitmap.bottomOffset);
            blurBitmap.bottomScaleX = 1.0f / width2;
            blurBitmap.bottomScaleY = 1.0f / height2;
            drawList(blurBitmap.bottomCanvas, false);
            blurBitmap.bottomCanvas.restore();
            final int max = (int) (((float) Math.max(6, Math.max(currentActionBarHeight, measuredWidth) / 180)) * 2.5f);
            if (blurQueue == null) {
                blurQueue = new DispatchQueue("BlurQueue");
            }
            blurQueue.postRunnable(new Runnable() {
                public void run() {
                    long currentTimeMillis = System.currentTimeMillis();
                    Utilities.stackBlurBitmap(blurBitmap.topBitmap, max);
                    Utilities.stackBlurBitmap(blurBitmap.bottomBitmap, max);
                    SizeNotifierFrameLayout sizeNotifierFrameLayout = SizeNotifierFrameLayout.this;
                    sizeNotifierFrameLayout.times = (int) (((long) sizeNotifierFrameLayout.times) + (System.currentTimeMillis() - currentTimeMillis));
                    SizeNotifierFrameLayout sizeNotifierFrameLayout2 = SizeNotifierFrameLayout.this;
                    int i = sizeNotifierFrameLayout2.count + 1;
                    sizeNotifierFrameLayout2.count = i;
                    if (i > 1000) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("chat blur generating average time");
                        SizeNotifierFrameLayout sizeNotifierFrameLayout3 = SizeNotifierFrameLayout.this;
                        sb.append(((float) sizeNotifierFrameLayout3.times) / ((float) sizeNotifierFrameLayout3.count));
                        FileLog.d(sb.toString());
                        SizeNotifierFrameLayout sizeNotifierFrameLayout4 = SizeNotifierFrameLayout.this;
                        sizeNotifierFrameLayout4.count = 0;
                        sizeNotifierFrameLayout4.times = 0;
                    }
                    AndroidUtilities.runOnUIThread(new SizeNotifierFrameLayout$1$$ExternalSyntheticLambda2(this, blurBitmap));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$run$2(BlurBitmap blurBitmap) {
                    SizeNotifierFrameLayout sizeNotifierFrameLayout = SizeNotifierFrameLayout.this;
                    final BlurBitmap blurBitmap2 = sizeNotifierFrameLayout.currentBitmap;
                    sizeNotifierFrameLayout.blurPaintTop2.setShader(sizeNotifierFrameLayout.blurPaintTop.getShader());
                    SizeNotifierFrameLayout sizeNotifierFrameLayout2 = SizeNotifierFrameLayout.this;
                    sizeNotifierFrameLayout2.blurPaintBottom2.setShader(sizeNotifierFrameLayout2.blurPaintBottom.getShader());
                    Bitmap bitmap = blurBitmap.topBitmap;
                    Shader.TileMode tileMode = Shader.TileMode.CLAMP;
                    SizeNotifierFrameLayout.this.blurPaintTop.setShader(new BitmapShader(bitmap, tileMode, tileMode));
                    Bitmap bitmap2 = blurBitmap.bottomBitmap;
                    Shader.TileMode tileMode2 = Shader.TileMode.CLAMP;
                    SizeNotifierFrameLayout.this.blurPaintBottom.setShader(new BitmapShader(bitmap2, tileMode2, tileMode2));
                    SizeNotifierFrameLayout sizeNotifierFrameLayout3 = SizeNotifierFrameLayout.this;
                    sizeNotifierFrameLayout3.blurCrossfadeProgress = 0.0f;
                    ValueAnimator valueAnimator = sizeNotifierFrameLayout3.blurCrossfade;
                    if (valueAnimator != null) {
                        valueAnimator.cancel();
                    }
                    SizeNotifierFrameLayout.this.blurCrossfade = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    SizeNotifierFrameLayout.this.blurCrossfade.addUpdateListener(new SizeNotifierFrameLayout$1$$ExternalSyntheticLambda0(this));
                    SizeNotifierFrameLayout.this.blurCrossfade.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            SizeNotifierFrameLayout.this.unusedBitmaps.add(blurBitmap2);
                            super.onAnimationEnd(animator);
                        }
                    });
                    SizeNotifierFrameLayout.this.blurCrossfade.setDuration(50);
                    SizeNotifierFrameLayout.this.blurCrossfade.start();
                    for (int i = 0; i < SizeNotifierFrameLayout.this.blurBehindViews.size(); i++) {
                        SizeNotifierFrameLayout.this.blurBehindViews.get(i).invalidate();
                    }
                    SizeNotifierFrameLayout.this.currentBitmap = blurBitmap;
                    AndroidUtilities.runOnUIThread(new SizeNotifierFrameLayout$1$$ExternalSyntheticLambda1(this), 32);
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$run$0(ValueAnimator valueAnimator) {
                    SizeNotifierFrameLayout.this.blurCrossfadeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    for (int i = 0; i < SizeNotifierFrameLayout.this.blurBehindViews.size(); i++) {
                        SizeNotifierFrameLayout.this.blurBehindViews.get(i).invalidate();
                    }
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$run$1() {
                    SizeNotifierFrameLayout sizeNotifierFrameLayout = SizeNotifierFrameLayout.this;
                    sizeNotifierFrameLayout.blurGeneratingTuskIsRunning = false;
                    sizeNotifierFrameLayout.startBlur();
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public float getBottomOffset() {
        return (float) getMeasuredHeight();
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.blurIsRunning) {
            startBlur();
        }
        super.dispatchDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.needBlur && !this.blurIsRunning) {
            this.blurIsRunning = true;
            this.invalidateBlur = true;
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.blurPaintTop.setShader((Shader) null);
        this.blurPaintTop2.setShader((Shader) null);
        this.blurPaintBottom.setShader((Shader) null);
        this.blurPaintBottom2.setShader((Shader) null);
        ValueAnimator valueAnimator = this.blurCrossfade;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        BlurBitmap blurBitmap = this.currentBitmap;
        if (blurBitmap != null) {
            blurBitmap.recycle();
            this.currentBitmap = null;
        }
        for (int i = 0; i < this.unusedBitmaps.size(); i++) {
            if (this.unusedBitmaps.get(i) != null) {
                this.unusedBitmaps.get(i).recycle();
            }
        }
        this.unusedBitmaps.clear();
        this.blurIsRunning = false;
    }

    public void drawBlur(Canvas canvas, float f, Rect rect2, Paint paint, boolean z) {
        if (this.currentBitmap == null || !SharedConfig.chatBlurEnabled()) {
            canvas.drawRect(rect2, paint);
            return;
        }
        Paint paint2 = z ? this.blurPaintTop : this.blurPaintBottom;
        Paint paint3 = z ? this.blurPaintTop2 : this.blurPaintBottom2;
        if (paint2.getShader() != null) {
            this.matrix.reset();
            if (!z) {
                Matrix matrix2 = this.matrix;
                BlurBitmap blurBitmap = this.currentBitmap;
                matrix2.setTranslate(0.0f, ((-f) + blurBitmap.bottomOffset) - ((float) blurBitmap.pixelFixOffset));
                Matrix matrix3 = this.matrix;
                BlurBitmap blurBitmap2 = this.currentBitmap;
                matrix3.preScale(blurBitmap2.bottomScaleX, blurBitmap2.bottomScaleY);
            } else {
                this.matrix.setTranslate(0.0f, -f);
                Matrix matrix4 = this.matrix;
                BlurBitmap blurBitmap3 = this.currentBitmap;
                matrix4.preScale(blurBitmap3.topScaleX, blurBitmap3.topScaleY);
            }
            paint2.getShader().setLocalMatrix(this.matrix);
            if (paint3.getShader() != null) {
                paint3.getShader().setLocalMatrix(this.matrix);
            }
        }
        if (this.blurCrossfadeProgress == 1.0f || paint3.getShader() == null) {
            canvas.drawRect(rect2, paint);
            canvas.drawRect(rect2, paint2);
        } else {
            canvas.drawRect(rect2, paint);
            canvas.drawRect(rect2, paint3);
            canvas.saveLayerAlpha((float) rect2.left, (float) rect2.top, (float) rect2.right, (float) rect2.bottom, (int) (this.blurCrossfadeProgress * 255.0f), 31);
            canvas.drawRect(rect2, paint);
            canvas.drawRect(rect2, paint2);
            canvas.restore();
        }
        paint.setAlpha(Color.alpha(Theme.getColor("chat_BlurAlpha")));
        canvas.drawRect(rect2, paint);
    }

    private static class BlurBitmap {
        Bitmap bottomBitmap;
        Canvas bottomCanvas;
        float bottomOffset;
        float bottomScaleX;
        float bottomScaleY;
        int pixelFixOffset;
        Bitmap topBitmap;
        Canvas topCanvas;
        float topScaleX;
        float topScaleY;

        private BlurBitmap() {
        }

        public void recycle() {
            this.topBitmap.recycle();
            this.bottomBitmap.recycle();
        }
    }
}
