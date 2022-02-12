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
    /* access modifiers changed from: private */
    public boolean animationInProgress;
    /* access modifiers changed from: private */
    public Drawable backgroundDrawable;
    /* access modifiers changed from: private */
    public int backgroundTranslationY;
    protected View backgroundView;
    final BlurBackgroundTask blurBackgroundTask;
    public ArrayList<View> blurBehindViews;
    ValueAnimator blurCrossfade;
    public float blurCrossfadeProgress;
    public boolean blurGeneratingTuskIsRunning;
    public boolean blurIsRunning;
    public Paint blurPaintBottom;
    public Paint blurPaintBottom2;
    public Paint blurPaintTop;
    public Paint blurPaintTop2;
    /* access modifiers changed from: private */
    public int bottomClip;
    int count;
    int count2;
    BlurBitmap currentBitmap;
    private SizeNotifierFrameLayoutDelegate delegate;
    /* access modifiers changed from: private */
    public int emojiHeight;
    /* access modifiers changed from: private */
    public float emojiOffset;
    public boolean invalidateBlur;
    protected int keyboardHeight;
    Matrix matrix;
    Matrix matrix2;
    public boolean needBlur;
    /* access modifiers changed from: private */
    public boolean occupyStatusBar;
    /* access modifiers changed from: private */
    public Drawable oldBackgroundDrawable;
    private WallpaperParallaxEffect parallaxEffect;
    /* access modifiers changed from: private */
    public float parallaxScale;
    /* access modifiers changed from: private */
    public ActionBarLayout parentLayout;
    private boolean paused;
    BlurBitmap prevBitmap;
    private Rect rect;
    /* access modifiers changed from: private */
    public boolean skipBackgroundDrawing;
    SnowflakesEffect snowflakesEffect;
    int times;
    int times2;
    /* access modifiers changed from: private */
    public float translationX;
    /* access modifiers changed from: private */
    public float translationY;
    public ArrayList<BlurBitmap> unusedBitmaps;

    public interface SizeNotifierFrameLayoutDelegate {
        void onSizeChanged(int i, boolean z);
    }

    /* access modifiers changed from: private */
    public void checkLayerType() {
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
        this.matrix2 = new Matrix();
        this.blurPaintTop = new Paint();
        this.blurPaintTop2 = new Paint();
        this.blurPaintBottom = new Paint();
        this.blurPaintBottom2 = new Paint();
        this.blurBackgroundTask = new BlurBackgroundTask();
        setWillNotDraw(false);
        this.parentLayout = actionBarLayout;
        this.adjustPanLayoutHelper = createAdjustPanLayoutHelper();
        AnonymousClass1 r4 = new View(context) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                if (SizeNotifierFrameLayout.this.backgroundDrawable != null && !SizeNotifierFrameLayout.this.skipBackgroundDrawing) {
                    Drawable newDrawable = SizeNotifierFrameLayout.this.getNewDrawable();
                    if (!(newDrawable == SizeNotifierFrameLayout.this.backgroundDrawable || newDrawable == null)) {
                        if (Theme.isAnimatingColor()) {
                            SizeNotifierFrameLayout sizeNotifierFrameLayout = SizeNotifierFrameLayout.this;
                            Drawable unused = sizeNotifierFrameLayout.oldBackgroundDrawable = sizeNotifierFrameLayout.backgroundDrawable;
                        }
                        if (newDrawable instanceof MotionBackgroundDrawable) {
                            ((MotionBackgroundDrawable) newDrawable).setParentView(SizeNotifierFrameLayout.this.backgroundView);
                        }
                        Drawable unused2 = SizeNotifierFrameLayout.this.backgroundDrawable = newDrawable;
                    }
                    SizeNotifierFrameLayout.this.checkLayerType();
                    float themeAnimationValue = SizeNotifierFrameLayout.this.parentLayout != null ? SizeNotifierFrameLayout.this.parentLayout.getThemeAnimationValue() : 1.0f;
                    int i = 0;
                    while (i < 2) {
                        SizeNotifierFrameLayout sizeNotifierFrameLayout2 = SizeNotifierFrameLayout.this;
                        Drawable access$200 = i == 0 ? sizeNotifierFrameLayout2.oldBackgroundDrawable : sizeNotifierFrameLayout2.backgroundDrawable;
                        if (access$200 != null) {
                            if (i != 1 || SizeNotifierFrameLayout.this.oldBackgroundDrawable == null || SizeNotifierFrameLayout.this.parentLayout == null) {
                                access$200.setAlpha(255);
                            } else {
                                access$200.setAlpha((int) (255.0f * themeAnimationValue));
                            }
                            if (access$200 instanceof MotionBackgroundDrawable) {
                                MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) access$200;
                                if (motionBackgroundDrawable.hasPattern()) {
                                    int currentActionBarHeight = (SizeNotifierFrameLayout.this.isActionBarVisible() ? ActionBar.getCurrentActionBarHeight() : 0) + ((Build.VERSION.SDK_INT < 21 || !SizeNotifierFrameLayout.this.occupyStatusBar) ? 0 : AndroidUtilities.statusBarHeight);
                                    int measuredHeight = getRootView().getMeasuredHeight() - currentActionBarHeight;
                                    float max = Math.max(((float) getMeasuredWidth()) / ((float) access$200.getIntrinsicWidth()), ((float) measuredHeight) / ((float) access$200.getIntrinsicHeight()));
                                    int ceil = (int) Math.ceil((double) (((float) access$200.getIntrinsicWidth()) * max * SizeNotifierFrameLayout.this.parallaxScale));
                                    int ceil2 = (int) Math.ceil((double) (((float) access$200.getIntrinsicHeight()) * max * SizeNotifierFrameLayout.this.parallaxScale));
                                    int measuredWidth = ((getMeasuredWidth() - ceil) / 2) + ((int) SizeNotifierFrameLayout.this.translationX);
                                    int access$800 = SizeNotifierFrameLayout.this.backgroundTranslationY + ((measuredHeight - ceil2) / 2) + currentActionBarHeight + ((int) SizeNotifierFrameLayout.this.translationY);
                                    canvas.save();
                                    canvas.clipRect(0, currentActionBarHeight, ceil, getMeasuredHeight() - SizeNotifierFrameLayout.this.bottomClip);
                                    access$200.setBounds(measuredWidth, access$800, ceil + measuredWidth, ceil2 + access$800);
                                    access$200.draw(canvas);
                                    SizeNotifierFrameLayout.this.checkSnowflake(canvas);
                                    canvas.restore();
                                } else {
                                    if (SizeNotifierFrameLayout.this.bottomClip != 0) {
                                        canvas.save();
                                        canvas.clipRect(0, 0, getMeasuredWidth(), getRootView().getMeasuredHeight() - SizeNotifierFrameLayout.this.bottomClip);
                                    }
                                    motionBackgroundDrawable.setTranslationY(SizeNotifierFrameLayout.this.backgroundTranslationY);
                                    int measuredHeight2 = getMeasuredHeight() - SizeNotifierFrameLayout.this.backgroundTranslationY;
                                    if (SizeNotifierFrameLayout.this.animationInProgress) {
                                        measuredHeight2 = (int) (((float) measuredHeight2) - SizeNotifierFrameLayout.this.emojiOffset);
                                    } else if (SizeNotifierFrameLayout.this.emojiHeight != 0) {
                                        measuredHeight2 -= SizeNotifierFrameLayout.this.emojiHeight;
                                    }
                                    access$200.setBounds(0, 0, getMeasuredWidth(), measuredHeight2);
                                    access$200.draw(canvas);
                                    if (SizeNotifierFrameLayout.this.bottomClip != 0) {
                                        canvas.restore();
                                    }
                                }
                            } else if (access$200 instanceof ColorDrawable) {
                                if (SizeNotifierFrameLayout.this.bottomClip != 0) {
                                    canvas.save();
                                    canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - SizeNotifierFrameLayout.this.bottomClip);
                                }
                                access$200.setBounds(0, 0, getMeasuredWidth(), getRootView().getMeasuredHeight());
                                access$200.draw(canvas);
                                SizeNotifierFrameLayout.this.checkSnowflake(canvas);
                                if (SizeNotifierFrameLayout.this.bottomClip != 0) {
                                    canvas.restore();
                                }
                            } else if (access$200 instanceof GradientDrawable) {
                                if (SizeNotifierFrameLayout.this.bottomClip != 0) {
                                    canvas.save();
                                    canvas.clipRect(0, 0, getMeasuredWidth(), getRootView().getMeasuredHeight() - SizeNotifierFrameLayout.this.bottomClip);
                                }
                                access$200.setBounds(0, SizeNotifierFrameLayout.this.backgroundTranslationY, getMeasuredWidth(), SizeNotifierFrameLayout.this.backgroundTranslationY + getRootView().getMeasuredHeight());
                                access$200.draw(canvas);
                                SizeNotifierFrameLayout.this.checkSnowflake(canvas);
                                if (SizeNotifierFrameLayout.this.bottomClip != 0) {
                                    canvas.restore();
                                }
                            } else if (access$200 instanceof BitmapDrawable) {
                                if (((BitmapDrawable) access$200).getTileModeX() == Shader.TileMode.REPEAT) {
                                    canvas.save();
                                    float f = 2.0f / AndroidUtilities.density;
                                    canvas.scale(f, f);
                                    access$200.setBounds(0, 0, (int) Math.ceil((double) (((float) getMeasuredWidth()) / f)), (int) Math.ceil((double) (((float) getRootView().getMeasuredHeight()) / f)));
                                    access$200.draw(canvas);
                                    SizeNotifierFrameLayout.this.checkSnowflake(canvas);
                                    canvas.restore();
                                } else {
                                    int currentActionBarHeight2 = (SizeNotifierFrameLayout.this.isActionBarVisible() ? ActionBar.getCurrentActionBarHeight() : 0) + ((Build.VERSION.SDK_INT < 21 || !SizeNotifierFrameLayout.this.occupyStatusBar) ? 0 : AndroidUtilities.statusBarHeight);
                                    int measuredHeight3 = getRootView().getMeasuredHeight() - currentActionBarHeight2;
                                    float max2 = Math.max(((float) getMeasuredWidth()) / ((float) access$200.getIntrinsicWidth()), ((float) measuredHeight3) / ((float) access$200.getIntrinsicHeight()));
                                    int ceil3 = (int) Math.ceil((double) (((float) access$200.getIntrinsicWidth()) * max2 * SizeNotifierFrameLayout.this.parallaxScale));
                                    int ceil4 = (int) Math.ceil((double) (((float) access$200.getIntrinsicHeight()) * max2 * SizeNotifierFrameLayout.this.parallaxScale));
                                    int measuredWidth2 = ((getMeasuredWidth() - ceil3) / 2) + ((int) SizeNotifierFrameLayout.this.translationX);
                                    int access$8002 = SizeNotifierFrameLayout.this.backgroundTranslationY + ((measuredHeight3 - ceil4) / 2) + currentActionBarHeight2 + ((int) SizeNotifierFrameLayout.this.translationY);
                                    canvas.save();
                                    canvas.clipRect(0, currentActionBarHeight2, ceil3, getMeasuredHeight() - SizeNotifierFrameLayout.this.bottomClip);
                                    access$200.setBounds(measuredWidth2, access$8002, ceil3 + measuredWidth2, ceil4 + access$8002);
                                    access$200.draw(canvas);
                                    SizeNotifierFrameLayout.this.checkSnowflake(canvas);
                                    canvas.restore();
                                }
                            }
                            if (i == 0 && SizeNotifierFrameLayout.this.oldBackgroundDrawable != null && themeAnimationValue >= 1.0f) {
                                Drawable unused3 = SizeNotifierFrameLayout.this.oldBackgroundDrawable = null;
                                SizeNotifierFrameLayout.this.backgroundView.invalidate();
                            }
                        }
                        i++;
                    }
                }
            }
        };
        this.backgroundView = r4;
        addView(r4, LayoutHelper.createFrame(-1, -1.0f));
    }

    public void setBackgroundImage(Drawable drawable, boolean z) {
        if (this.backgroundDrawable != drawable) {
            if (drawable instanceof MotionBackgroundDrawable) {
                ((MotionBackgroundDrawable) drawable).setParentView(this.backgroundView);
            }
            this.backgroundDrawable = drawable;
            checkLayerType();
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
            this.backgroundView.invalidate();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setBackgroundImage$0(int i, int i2, float f) {
        this.translationX = (float) i;
        this.translationY = (float) i2;
        this.backgroundView.invalidate();
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
        if (i != this.bottomClip) {
            this.bottomClip = i;
            this.backgroundView.invalidate();
        }
    }

    public void setBackgroundTranslation(int i) {
        if (i != this.backgroundTranslationY) {
            this.backgroundTranslationY = i;
            this.backgroundView.invalidate();
        }
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

    /* access modifiers changed from: private */
    public void checkSnowflake(Canvas canvas) {
        if (Theme.canStartHolidayAnimation()) {
            if (this.snowflakesEffect == null) {
                this.snowflakesEffect = new SnowflakesEffect(1);
            }
            this.snowflakesEffect.onDraw(this, canvas);
        }
    }

    public void setSkipBackgroundDrawing(boolean z) {
        if (this.skipBackgroundDrawing != z) {
            this.skipBackgroundDrawing = z;
            this.backgroundView.invalidate();
        }
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
        BlurBitmap blurBitmap;
        if (this.blurIsRunning && !this.blurGeneratingTuskIsRunning && this.invalidateBlur && SharedConfig.chatBlurEnabled() && Color.alpha(Theme.getColor("chat_BlurAlpha")) != 0) {
            this.invalidateBlur = false;
            this.blurGeneratingTuskIsRunning = true;
            int measuredWidth = getMeasuredWidth();
            int currentActionBarHeight = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight + AndroidUtilities.dp(100.0f);
            float f = (float) currentActionBarHeight;
            int i = ((int) (f / 12.0f)) + 22;
            float f2 = (float) measuredWidth;
            int i2 = (int) (f2 / 12.0f);
            long currentTimeMillis = System.currentTimeMillis();
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
            float height = ((float) (blurBitmap.topBitmap.getHeight() - 22)) / f;
            blurBitmap.topCanvas.save();
            blurBitmap.pixelFixOffset = getScrollOffset() % 12;
            float f3 = height * 10.0f;
            blurBitmap.topCanvas.clipRect(0.0f, f3, (float) blurBitmap.topBitmap.getWidth(), (float) blurBitmap.topBitmap.getHeight());
            blurBitmap.topCanvas.scale(width, height);
            blurBitmap.topCanvas.translate(0.0f, f3 + ((float) blurBitmap.pixelFixOffset));
            blurBitmap.topScaleX = 1.0f / width;
            blurBitmap.topScaleY = 1.0f / height;
            drawList(blurBitmap.topCanvas, true);
            blurBitmap.topCanvas.restore();
            float width2 = ((float) blurBitmap.bottomBitmap.getWidth()) / f2;
            float height2 = ((float) (blurBitmap.bottomBitmap.getHeight() - 22)) / f;
            blurBitmap.bottomOffset = getBottomOffset() - f;
            blurBitmap.bottomCanvas.save();
            float f4 = 10.0f * height2;
            blurBitmap.bottomCanvas.clipRect(0.0f, f4, (float) blurBitmap.bottomBitmap.getWidth(), (float) blurBitmap.bottomBitmap.getHeight());
            blurBitmap.bottomCanvas.scale(width2, height2);
            blurBitmap.bottomCanvas.translate(0.0f, (f4 - blurBitmap.bottomOffset) + ((float) blurBitmap.pixelFixOffset));
            blurBitmap.bottomScaleX = 1.0f / width2;
            blurBitmap.bottomScaleY = 1.0f / height2;
            drawList(blurBitmap.bottomCanvas, false);
            blurBitmap.bottomCanvas.restore();
            this.times2 = (int) (((long) this.times2) + (System.currentTimeMillis() - currentTimeMillis));
            int i3 = this.count2 + 1;
            this.count2 = i3;
            if (i3 >= 20) {
                this.count2 = 0;
                this.times2 = 0;
            }
            if (blurQueue == null) {
                blurQueue = new DispatchQueue("BlurQueue");
            }
            this.blurBackgroundTask.radius = (int) (((float) Math.max(6, Math.max(currentActionBarHeight, measuredWidth) / 180)) * 2.5f);
            BlurBackgroundTask blurBackgroundTask2 = this.blurBackgroundTask;
            blurBackgroundTask2.finalBitmap = blurBitmap;
            blurQueue.postRunnable(blurBackgroundTask2);
        }
    }

    private class BlurBackgroundTask implements Runnable {
        BlurBitmap finalBitmap;
        int radius;

        private BlurBackgroundTask() {
        }

        public void run() {
            long currentTimeMillis = System.currentTimeMillis();
            Utilities.stackBlurBitmap(this.finalBitmap.topBitmap, this.radius);
            Utilities.stackBlurBitmap(this.finalBitmap.bottomBitmap, this.radius);
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
            AndroidUtilities.runOnUIThread(new SizeNotifierFrameLayout$BlurBackgroundTask$$ExternalSyntheticLambda1(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$run$2() {
            SizeNotifierFrameLayout sizeNotifierFrameLayout = SizeNotifierFrameLayout.this;
            final BlurBitmap blurBitmap = sizeNotifierFrameLayout.currentBitmap;
            sizeNotifierFrameLayout.prevBitmap = blurBitmap;
            sizeNotifierFrameLayout.blurPaintTop2.setShader(sizeNotifierFrameLayout.blurPaintTop.getShader());
            SizeNotifierFrameLayout sizeNotifierFrameLayout2 = SizeNotifierFrameLayout.this;
            sizeNotifierFrameLayout2.blurPaintBottom2.setShader(sizeNotifierFrameLayout2.blurPaintBottom.getShader());
            Bitmap bitmap = this.finalBitmap.topBitmap;
            Shader.TileMode tileMode = Shader.TileMode.CLAMP;
            SizeNotifierFrameLayout.this.blurPaintTop.setShader(new BitmapShader(bitmap, tileMode, tileMode));
            Bitmap bitmap2 = this.finalBitmap.bottomBitmap;
            Shader.TileMode tileMode2 = Shader.TileMode.CLAMP;
            SizeNotifierFrameLayout.this.blurPaintBottom.setShader(new BitmapShader(bitmap2, tileMode2, tileMode2));
            SizeNotifierFrameLayout sizeNotifierFrameLayout3 = SizeNotifierFrameLayout.this;
            sizeNotifierFrameLayout3.blurCrossfadeProgress = 0.0f;
            ValueAnimator valueAnimator = sizeNotifierFrameLayout3.blurCrossfade;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            SizeNotifierFrameLayout.this.blurCrossfade = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            SizeNotifierFrameLayout.this.blurCrossfade.addUpdateListener(new SizeNotifierFrameLayout$BlurBackgroundTask$$ExternalSyntheticLambda0(this));
            SizeNotifierFrameLayout.this.blurCrossfade.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    SizeNotifierFrameLayout sizeNotifierFrameLayout = SizeNotifierFrameLayout.this;
                    sizeNotifierFrameLayout.blurCrossfadeProgress = 1.0f;
                    sizeNotifierFrameLayout.unusedBitmaps.add(blurBitmap);
                    super.onAnimationEnd(animator);
                }
            });
            SizeNotifierFrameLayout.this.blurCrossfade.setDuration(50);
            SizeNotifierFrameLayout.this.blurCrossfade.start();
            for (int i = 0; i < SizeNotifierFrameLayout.this.blurBehindViews.size(); i++) {
                SizeNotifierFrameLayout.this.blurBehindViews.get(i).invalidate();
            }
            SizeNotifierFrameLayout.this.currentBitmap = this.finalBitmap;
            AndroidUtilities.runOnUIThread(new SizeNotifierFrameLayout$BlurBackgroundTask$$ExternalSyntheticLambda2(this), 32);
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
        int alpha = Color.alpha(Theme.getColor("chat_BlurAlpha"));
        if (this.currentBitmap == null || !SharedConfig.chatBlurEnabled() || alpha == 0) {
            canvas.drawRect(rect2, paint);
            return;
        }
        Paint paint2 = z ? this.blurPaintTop : this.blurPaintBottom;
        Paint paint3 = z ? this.blurPaintTop2 : this.blurPaintBottom2;
        if (paint2.getShader() != null) {
            this.matrix.reset();
            this.matrix2.reset();
            if (!z) {
                Matrix matrix3 = this.matrix;
                float f2 = -f;
                BlurBitmap blurBitmap = this.currentBitmap;
                matrix3.setTranslate(0.0f, ((blurBitmap.bottomOffset + f2) - ((float) blurBitmap.pixelFixOffset)) - 22.0f);
                Matrix matrix4 = this.matrix;
                BlurBitmap blurBitmap2 = this.currentBitmap;
                matrix4.preScale(blurBitmap2.bottomScaleX, blurBitmap2.bottomScaleY);
                BlurBitmap blurBitmap3 = this.prevBitmap;
                if (blurBitmap3 != null) {
                    this.matrix2.setTranslate(0.0f, ((f2 + blurBitmap3.bottomOffset) - ((float) blurBitmap3.pixelFixOffset)) - 22.0f);
                    Matrix matrix5 = this.matrix2;
                    BlurBitmap blurBitmap4 = this.prevBitmap;
                    matrix5.preScale(blurBitmap4.bottomScaleX, blurBitmap4.bottomScaleY);
                }
            } else {
                float f3 = -f;
                this.matrix.setTranslate(0.0f, (f3 - ((float) this.currentBitmap.pixelFixOffset)) - 22.0f);
                Matrix matrix6 = this.matrix;
                BlurBitmap blurBitmap5 = this.currentBitmap;
                matrix6.preScale(blurBitmap5.topScaleX, blurBitmap5.topScaleY);
                BlurBitmap blurBitmap6 = this.prevBitmap;
                if (blurBitmap6 != null) {
                    this.matrix.setTranslate(0.0f, (f3 - ((float) blurBitmap6.pixelFixOffset)) - 22.0f);
                    Matrix matrix7 = this.matrix;
                    BlurBitmap blurBitmap7 = this.prevBitmap;
                    matrix7.preScale(blurBitmap7.topScaleX, blurBitmap7.topScaleY);
                }
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
        paint.setAlpha(alpha);
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
