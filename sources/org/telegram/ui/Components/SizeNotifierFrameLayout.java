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
import org.telegram.ui.BlurSettingsBottomSheet;

public class SizeNotifierFrameLayout extends FrameLayout {
    private static DispatchQueue blurQueue;
    private final float DOWN_SCALE;
    private final int TOP_CLIP_OFFSET;
    protected AdjustPanLayoutHelper adjustPanLayoutHelper;
    /* access modifiers changed from: private */
    public boolean animationInProgress;
    /* access modifiers changed from: private */
    public Drawable backgroundDrawable;
    /* access modifiers changed from: private */
    public int backgroundTranslationY;
    protected View backgroundView;
    private float bgAngle;
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
    public boolean needBlurBottom;
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
    float saturation;
    private Paint selectedBlurPaint;
    private Paint selectedBlurPaint2;
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

    public void invalidateBlur() {
        this.invalidateBlur = true;
        invalidate();
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
        this.unusedBitmaps = new ArrayList<>(10);
        this.blurBehindViews = new ArrayList<>();
        this.matrix = new Matrix();
        this.matrix2 = new Matrix();
        this.blurPaintTop = new Paint();
        this.blurPaintTop2 = new Paint();
        this.blurPaintBottom = new Paint();
        this.blurPaintBottom2 = new Paint();
        this.DOWN_SCALE = 12.0f;
        this.TOP_CLIP_OFFSET = 34;
        this.blurBackgroundTask = new BlurBackgroundTask();
        setWillNotDraw(false);
        this.parentLayout = layout;
        this.adjustPanLayoutHelper = createAdjustPanLayoutHelper();
        AnonymousClass1 r0 = new View(context) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                Drawable newDrawable;
                int a;
                int i;
                Canvas canvas2 = canvas;
                if (SizeNotifierFrameLayout.this.backgroundDrawable != null && !SizeNotifierFrameLayout.this.skipBackgroundDrawing) {
                    Drawable newDrawable2 = SizeNotifierFrameLayout.this.getNewDrawable();
                    if (!(newDrawable2 == SizeNotifierFrameLayout.this.backgroundDrawable || newDrawable2 == null)) {
                        if (Theme.isAnimatingColor()) {
                            SizeNotifierFrameLayout sizeNotifierFrameLayout = SizeNotifierFrameLayout.this;
                            Drawable unused = sizeNotifierFrameLayout.oldBackgroundDrawable = sizeNotifierFrameLayout.backgroundDrawable;
                        }
                        if (newDrawable2 instanceof MotionBackgroundDrawable) {
                            ((MotionBackgroundDrawable) newDrawable2).setParentView(SizeNotifierFrameLayout.this.backgroundView);
                        }
                        Drawable unused2 = SizeNotifierFrameLayout.this.backgroundDrawable = newDrawable2;
                    }
                    float themeAnimationValue = SizeNotifierFrameLayout.this.parentLayout != null ? SizeNotifierFrameLayout.this.parentLayout.getThemeAnimationValue() : 1.0f;
                    int a2 = 0;
                    while (a2 < 2) {
                        SizeNotifierFrameLayout sizeNotifierFrameLayout2 = SizeNotifierFrameLayout.this;
                        Drawable drawable = a2 == 0 ? sizeNotifierFrameLayout2.oldBackgroundDrawable : sizeNotifierFrameLayout2.backgroundDrawable;
                        if (drawable == null) {
                            newDrawable = newDrawable2;
                            a = a2;
                        } else {
                            if (a2 != 1 || SizeNotifierFrameLayout.this.oldBackgroundDrawable == null || SizeNotifierFrameLayout.this.parentLayout == null) {
                                drawable.setAlpha(255);
                            } else {
                                drawable.setAlpha((int) (255.0f * themeAnimationValue));
                            }
                            if (drawable instanceof MotionBackgroundDrawable) {
                                MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) drawable;
                                if (motionBackgroundDrawable.hasPattern()) {
                                    int actionBarHeight = (SizeNotifierFrameLayout.this.isActionBarVisible() ? ActionBar.getCurrentActionBarHeight() : 0) + ((Build.VERSION.SDK_INT < 21 || !SizeNotifierFrameLayout.this.occupyStatusBar) ? 0 : AndroidUtilities.statusBarHeight);
                                    int viewHeight = getRootView().getMeasuredHeight() - actionBarHeight;
                                    float scale = Math.max(((float) getMeasuredWidth()) / ((float) drawable.getIntrinsicWidth()), ((float) viewHeight) / ((float) drawable.getIntrinsicHeight()));
                                    int actionBarHeight2 = actionBarHeight;
                                    int width = (int) Math.ceil((double) (((float) drawable.getIntrinsicWidth()) * scale * SizeNotifierFrameLayout.this.parallaxScale));
                                    a = a2;
                                    int height = (int) Math.ceil((double) (((float) drawable.getIntrinsicHeight()) * scale * SizeNotifierFrameLayout.this.parallaxScale));
                                    int x = ((getMeasuredWidth() - width) / 2) + ((int) SizeNotifierFrameLayout.this.translationX);
                                    int y = SizeNotifierFrameLayout.this.backgroundTranslationY + ((viewHeight - height) / 2) + actionBarHeight2 + ((int) SizeNotifierFrameLayout.this.translationY);
                                    canvas.save();
                                    newDrawable = newDrawable2;
                                    int actionBarHeight3 = actionBarHeight2;
                                    int actionBarHeight4 = viewHeight;
                                    canvas2.clipRect(0, actionBarHeight3, width, getMeasuredHeight() - SizeNotifierFrameLayout.this.bottomClip);
                                    drawable.setBounds(x, y, x + width, y + height);
                                    drawable.draw(canvas2);
                                    SizeNotifierFrameLayout.this.checkSnowflake(canvas2);
                                    canvas.restore();
                                } else {
                                    newDrawable = newDrawable2;
                                    a = a2;
                                    if (SizeNotifierFrameLayout.this.bottomClip != 0) {
                                        canvas.save();
                                        canvas2.clipRect(0, 0, getMeasuredWidth(), getRootView().getMeasuredHeight() - SizeNotifierFrameLayout.this.bottomClip);
                                    }
                                    motionBackgroundDrawable.setTranslationY(SizeNotifierFrameLayout.this.backgroundTranslationY);
                                    int bottom = (int) (((float) (getRootView().getMeasuredHeight() - SizeNotifierFrameLayout.this.backgroundTranslationY)) + SizeNotifierFrameLayout.this.translationY);
                                    if (SizeNotifierFrameLayout.this.animationInProgress) {
                                        bottom = (int) (((float) bottom) - SizeNotifierFrameLayout.this.emojiOffset);
                                    } else if (SizeNotifierFrameLayout.this.emojiHeight != 0) {
                                        bottom -= SizeNotifierFrameLayout.this.emojiHeight;
                                    }
                                    drawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                                    drawable.draw(canvas2);
                                    if (SizeNotifierFrameLayout.this.bottomClip != 0) {
                                        canvas.restore();
                                    }
                                }
                            } else {
                                newDrawable = newDrawable2;
                                a = a2;
                                if (drawable instanceof ColorDrawable) {
                                    if (SizeNotifierFrameLayout.this.bottomClip != 0) {
                                        canvas.save();
                                        i = 0;
                                        canvas2.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - SizeNotifierFrameLayout.this.bottomClip);
                                    } else {
                                        i = 0;
                                    }
                                    drawable.setBounds(i, i, getMeasuredWidth(), getRootView().getMeasuredHeight());
                                    drawable.draw(canvas2);
                                    SizeNotifierFrameLayout.this.checkSnowflake(canvas2);
                                    if (SizeNotifierFrameLayout.this.bottomClip != 0) {
                                        canvas.restore();
                                    }
                                } else if (drawable instanceof GradientDrawable) {
                                    if (SizeNotifierFrameLayout.this.bottomClip != 0) {
                                        canvas.save();
                                        canvas2.clipRect(0, 0, getMeasuredWidth(), getRootView().getMeasuredHeight() - SizeNotifierFrameLayout.this.bottomClip);
                                    }
                                    drawable.setBounds(0, SizeNotifierFrameLayout.this.backgroundTranslationY, getMeasuredWidth(), SizeNotifierFrameLayout.this.backgroundTranslationY + getRootView().getMeasuredHeight());
                                    drawable.draw(canvas2);
                                    SizeNotifierFrameLayout.this.checkSnowflake(canvas2);
                                    if (SizeNotifierFrameLayout.this.bottomClip != 0) {
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
                                        SizeNotifierFrameLayout.this.checkSnowflake(canvas2);
                                        canvas.restore();
                                    } else {
                                        int actionBarHeight5 = (SizeNotifierFrameLayout.this.isActionBarVisible() ? ActionBar.getCurrentActionBarHeight() : 0) + ((Build.VERSION.SDK_INT < 21 || !SizeNotifierFrameLayout.this.occupyStatusBar) ? 0 : AndroidUtilities.statusBarHeight);
                                        int viewHeight2 = getRootView().getMeasuredHeight() - actionBarHeight5;
                                        float scale3 = Math.max(((float) getMeasuredWidth()) / ((float) drawable.getIntrinsicWidth()), ((float) viewHeight2) / ((float) drawable.getIntrinsicHeight()));
                                        int width2 = (int) Math.ceil((double) (((float) drawable.getIntrinsicWidth()) * scale3 * SizeNotifierFrameLayout.this.parallaxScale));
                                        int height2 = (int) Math.ceil((double) (((float) drawable.getIntrinsicHeight()) * scale3 * SizeNotifierFrameLayout.this.parallaxScale));
                                        int x2 = ((getMeasuredWidth() - width2) / 2) + ((int) SizeNotifierFrameLayout.this.translationX);
                                        int y2 = SizeNotifierFrameLayout.this.backgroundTranslationY + ((viewHeight2 - height2) / 2) + actionBarHeight5 + ((int) SizeNotifierFrameLayout.this.translationY);
                                        canvas.save();
                                        BitmapDrawable bitmapDrawable2 = bitmapDrawable;
                                        canvas2.clipRect(0, actionBarHeight5, width2, getMeasuredHeight() - SizeNotifierFrameLayout.this.bottomClip);
                                        drawable.setBounds(x2, y2, x2 + width2, y2 + height2);
                                        drawable.draw(canvas2);
                                        SizeNotifierFrameLayout.this.checkSnowflake(canvas2);
                                        canvas.restore();
                                    }
                                }
                            }
                            if (a == 0 && SizeNotifierFrameLayout.this.oldBackgroundDrawable != null) {
                                if (themeAnimationValue >= 1.0f) {
                                    Drawable unused3 = SizeNotifierFrameLayout.this.oldBackgroundDrawable = null;
                                    SizeNotifierFrameLayout.this.backgroundView.invalidate();
                                }
                            }
                        }
                        a2 = a + 1;
                        newDrawable2 = newDrawable;
                    }
                }
            }
        };
        this.backgroundView = r0;
        addView(r0, LayoutHelper.createFrame(-1, -1.0f));
    }

    public void setBackgroundImage(Drawable bitmap, boolean motion) {
        if (this.backgroundDrawable != bitmap) {
            if (bitmap instanceof MotionBackgroundDrawable) {
                ((MotionBackgroundDrawable) bitmap).setParentView(this.backgroundView);
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
            this.backgroundView.invalidate();
        }
    }

    /* renamed from: lambda$setBackgroundImage$0$org-telegram-ui-Components-SizeNotifierFrameLayout  reason: not valid java name */
    public /* synthetic */ void m1410x49b6021f(int offsetX, int offsetY, float angle) {
        this.translationX = (float) offsetX;
        this.translationY = (float) offsetY;
        this.bgAngle = angle;
        this.backgroundView.invalidate();
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
    public /* synthetic */ void m1409xe23b7f7f(boolean isWidthGreater) {
        SizeNotifierFrameLayoutDelegate sizeNotifierFrameLayoutDelegate = this.delegate;
        if (sizeNotifierFrameLayoutDelegate != null) {
            sizeNotifierFrameLayoutDelegate.onSizeChanged(this.keyboardHeight, isWidthGreater);
        }
    }

    public void setBottomClip(int value) {
        if (value != this.bottomClip) {
            this.bottomClip = value;
            this.backgroundView.invalidate();
        }
    }

    public void setBackgroundTranslation(int translation) {
        if (translation != this.backgroundTranslationY) {
            this.backgroundTranslationY = translation;
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
        if (this.emojiHeight != height) {
            this.emojiHeight = height;
            this.backgroundView.invalidate();
        }
    }

    public void setEmojiOffset(boolean animInProgress, float offset) {
        if (this.emojiOffset != offset || this.animationInProgress != animInProgress) {
            this.emojiOffset = offset;
            this.animationInProgress = animInProgress;
            this.backgroundView.invalidate();
        }
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

    /* access modifiers changed from: protected */
    public boolean isActionBarVisible() {
        return true;
    }

    /* access modifiers changed from: protected */
    public AdjustPanLayoutHelper createAdjustPanLayoutHelper() {
        return null;
    }

    public void setSkipBackgroundDrawing(boolean skipBackgroundDrawing2) {
        if (this.skipBackgroundDrawing != skipBackgroundDrawing2) {
            this.skipBackgroundDrawing = skipBackgroundDrawing2;
            this.backgroundView.invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public Drawable getNewDrawable() {
        return Theme.getCachedWallpaperNonBlocking();
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable who) {
        return who == getBackgroundImage() || super.verifyDrawable(who);
    }

    public void startBlur() {
        int blurAlpha;
        if (this.blurIsRunning && !this.blurGeneratingTuskIsRunning && this.invalidateBlur && SharedConfig.chatBlurEnabled() && (blurAlpha = Color.alpha(Theme.getColor("chat_BlurAlpha"))) != 255) {
            int lastW = getMeasuredWidth();
            int lastH = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight + AndroidUtilities.dp(100.0f);
            if (lastW == 0) {
            } else if (lastH == 0) {
                int i = blurAlpha;
            } else {
                this.invalidateBlur = false;
                this.blurGeneratingTuskIsRunning = true;
                int bitmapH = ((int) (((float) lastH) / 12.0f)) + 34;
                int bitmapW = (int) (((float) lastW) / 12.0f);
                long time = System.currentTimeMillis();
                BlurBitmap bitmap = null;
                if (this.unusedBitmaps.size() > 0) {
                    ArrayList<BlurBitmap> arrayList = this.unusedBitmaps;
                    bitmap = arrayList.remove(arrayList.size() - 1);
                }
                if (bitmap == null) {
                    bitmap = new BlurBitmap();
                    bitmap.topBitmap = Bitmap.createBitmap(bitmapW, bitmapH, Bitmap.Config.ARGB_8888);
                    bitmap.topCanvas = new Canvas(bitmap.topBitmap);
                    if (this.needBlurBottom) {
                        bitmap.bottomBitmap = Bitmap.createBitmap(bitmapW, bitmapH, Bitmap.Config.ARGB_8888);
                        bitmap.bottomCanvas = new Canvas(bitmap.bottomBitmap);
                    }
                } else {
                    bitmap.topBitmap.eraseColor(0);
                    if (bitmap.bottomBitmap != null) {
                        bitmap.bottomBitmap.eraseColor(0);
                    }
                }
                BlurBitmap finalBitmap = bitmap;
                float sX = ((float) finalBitmap.topBitmap.getWidth()) / ((float) lastW);
                float sY = ((float) (finalBitmap.topBitmap.getHeight() - 34)) / ((float) lastH);
                finalBitmap.topCanvas.save();
                finalBitmap.pixelFixOffset = getScrollOffset() % 24;
                int i2 = blurAlpha;
                finalBitmap.topCanvas.clipRect(1.0f, sY * 10.0f, (float) finalBitmap.topBitmap.getWidth(), (float) (finalBitmap.topBitmap.getHeight() - 1));
                finalBitmap.topCanvas.scale(sX, sY);
                finalBitmap.topCanvas.translate(0.0f, (sY * 10.0f) + ((float) finalBitmap.pixelFixOffset));
                finalBitmap.topScaleX = 1.0f / sX;
                finalBitmap.topScaleY = 1.0f / sY;
                drawList(finalBitmap.topCanvas, true);
                finalBitmap.topCanvas.restore();
                if (this.needBlurBottom) {
                    float sX2 = ((float) finalBitmap.bottomBitmap.getWidth()) / ((float) lastW);
                    float sY2 = ((float) (finalBitmap.bottomBitmap.getHeight() - 34)) / ((float) lastH);
                    finalBitmap.needBlurBottom = true;
                    finalBitmap.bottomOffset = getBottomOffset() - ((float) lastH);
                    finalBitmap.drawnLisetTranslationY = getBottomOffset();
                    finalBitmap.bottomCanvas.save();
                    finalBitmap.bottomCanvas.clipRect(1.0f, sY2 * 10.0f, (float) finalBitmap.bottomBitmap.getWidth(), (float) (finalBitmap.bottomBitmap.getHeight() - 1));
                    finalBitmap.bottomCanvas.scale(sX2, sY2);
                    finalBitmap.bottomCanvas.translate(0.0f, ((sY2 * 10.0f) - finalBitmap.bottomOffset) + ((float) finalBitmap.pixelFixOffset));
                    finalBitmap.bottomScaleX = 1.0f / sX2;
                    finalBitmap.bottomScaleY = 1.0f / sY2;
                    drawList(finalBitmap.bottomCanvas, false);
                    finalBitmap.bottomCanvas.restore();
                } else {
                    finalBitmap.needBlurBottom = false;
                }
                this.times2 = (int) (((long) this.times2) + (System.currentTimeMillis() - time));
                int i3 = this.count2 + 1;
                this.count2 = i3;
                if (i3 >= 20) {
                    this.count2 = 0;
                    this.times2 = 0;
                }
                if (blurQueue == null) {
                    blurQueue = new DispatchQueue("BlurQueue");
                }
                this.blurBackgroundTask.radius = (int) (((float) ((int) (((float) Math.max(6, Math.max(lastH, lastW) / 180)) * 2.5f))) * BlurSettingsBottomSheet.blurRadius);
                this.blurBackgroundTask.finalBitmap = finalBitmap;
                blurQueue.postRunnable(this.blurBackgroundTask);
            }
        }
    }

    private class BlurBackgroundTask implements Runnable {
        BlurBitmap finalBitmap;
        int radius;

        private BlurBackgroundTask() {
        }

        public void run() {
            long time = System.currentTimeMillis();
            Utilities.stackBlurBitmap(this.finalBitmap.topBitmap, this.radius);
            if (this.finalBitmap.needBlurBottom && this.finalBitmap.bottomBitmap != null) {
                Utilities.stackBlurBitmap(this.finalBitmap.bottomBitmap, this.radius);
            }
            SizeNotifierFrameLayout sizeNotifierFrameLayout = SizeNotifierFrameLayout.this;
            sizeNotifierFrameLayout.times = (int) (((long) sizeNotifierFrameLayout.times) + (System.currentTimeMillis() - time));
            SizeNotifierFrameLayout.this.count++;
            if (SizeNotifierFrameLayout.this.count > 1000) {
                FileLog.d("chat blur generating average time" + (((float) SizeNotifierFrameLayout.this.times) / ((float) SizeNotifierFrameLayout.this.count)));
                SizeNotifierFrameLayout.this.count = 0;
                SizeNotifierFrameLayout.this.times = 0;
            }
            AndroidUtilities.runOnUIThread(new SizeNotifierFrameLayout$BlurBackgroundTask$$ExternalSyntheticLambda2(this));
        }

        /* renamed from: lambda$run$2$org-telegram-ui-Components-SizeNotifierFrameLayout$BlurBackgroundTask  reason: not valid java name */
        public /* synthetic */ void m1413xa0CLASSNAME() {
            if (!SizeNotifierFrameLayout.this.blurIsRunning) {
                BlurBitmap blurBitmap = this.finalBitmap;
                if (blurBitmap != null) {
                    blurBitmap.recycle();
                }
                SizeNotifierFrameLayout.this.blurGeneratingTuskIsRunning = false;
                return;
            }
            SizeNotifierFrameLayout sizeNotifierFrameLayout = SizeNotifierFrameLayout.this;
            sizeNotifierFrameLayout.prevBitmap = sizeNotifierFrameLayout.currentBitmap;
            final BlurBitmap oldBitmap = SizeNotifierFrameLayout.this.currentBitmap;
            SizeNotifierFrameLayout.this.blurPaintTop2.setShader(SizeNotifierFrameLayout.this.blurPaintTop.getShader());
            SizeNotifierFrameLayout.this.blurPaintBottom2.setShader(SizeNotifierFrameLayout.this.blurPaintBottom.getShader());
            SizeNotifierFrameLayout.this.blurPaintTop.setShader(new BitmapShader(this.finalBitmap.topBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            if (this.finalBitmap.needBlurBottom && this.finalBitmap.bottomBitmap != null) {
                SizeNotifierFrameLayout.this.blurPaintBottom.setShader(new BitmapShader(this.finalBitmap.bottomBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            }
            if (SizeNotifierFrameLayout.this.blurCrossfade != null) {
                SizeNotifierFrameLayout.this.blurCrossfade.cancel();
            }
            SizeNotifierFrameLayout.this.blurCrossfadeProgress = 0.0f;
            SizeNotifierFrameLayout.this.blurCrossfade = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            SizeNotifierFrameLayout.this.blurCrossfade.addUpdateListener(new SizeNotifierFrameLayout$BlurBackgroundTask$$ExternalSyntheticLambda0(this));
            SizeNotifierFrameLayout.this.blurCrossfade.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    SizeNotifierFrameLayout.this.blurCrossfadeProgress = 1.0f;
                    SizeNotifierFrameLayout.this.unusedBitmaps.add(oldBitmap);
                    SizeNotifierFrameLayout.this.blurPaintTop2.setShader((Shader) null);
                    SizeNotifierFrameLayout.this.blurPaintBottom2.setShader((Shader) null);
                    SizeNotifierFrameLayout.this.invalidateBlurredViews();
                    super.onAnimationEnd(animation);
                }
            });
            SizeNotifierFrameLayout.this.blurCrossfade.setDuration(50);
            SizeNotifierFrameLayout.this.blurCrossfade.start();
            SizeNotifierFrameLayout.this.invalidateBlurredViews();
            SizeNotifierFrameLayout.this.currentBitmap = this.finalBitmap;
            AndroidUtilities.runOnUIThread(new SizeNotifierFrameLayout$BlurBackgroundTask$$ExternalSyntheticLambda1(this), 16);
        }

        /* renamed from: lambda$run$0$org-telegram-ui-Components-SizeNotifierFrameLayout$BlurBackgroundTask  reason: not valid java name */
        public /* synthetic */ void m1411xda6var_(ValueAnimator valueAnimator) {
            SizeNotifierFrameLayout.this.blurCrossfadeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            SizeNotifierFrameLayout.this.invalidateBlurredViews();
        }

        /* renamed from: lambda$run$1$org-telegram-ui-Components-SizeNotifierFrameLayout$BlurBackgroundTask  reason: not valid java name */
        public /* synthetic */ void m1412xbd9aCLASSNAME() {
            SizeNotifierFrameLayout.this.blurGeneratingTuskIsRunning = false;
            SizeNotifierFrameLayout.this.startBlur();
        }
    }

    public void invalidateBlurredViews() {
        for (int i = 0; i < this.blurBehindViews.size(); i++) {
            this.blurBehindViews.get(i).invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public float getBottomOffset() {
        return (float) getMeasuredHeight();
    }

    /* access modifiers changed from: protected */
    public float getListTranslationY() {
        return 0.0f;
    }

    /* access modifiers changed from: protected */
    public Theme.ResourcesProvider getResourceProvider() {
        return null;
    }

    /* access modifiers changed from: protected */
    public void drawList(Canvas blurCanvas, boolean top) {
    }

    /* access modifiers changed from: protected */
    public int getScrollOffset() {
        return 0;
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

    public void drawBlurRect(Canvas canvas, float y, Rect rectTmp, Paint blurScrimPaint, boolean top) {
        int blurAlpha = Color.alpha(Theme.getColor("chat_BlurAlpha"));
        if (this.currentBitmap == null || !SharedConfig.chatBlurEnabled()) {
            canvas.drawRect(rectTmp, blurScrimPaint);
            return;
        }
        updateBlurShaderPosition(y, top);
        blurScrimPaint.setAlpha(255);
        if (this.blurCrossfadeProgress == 1.0f || this.selectedBlurPaint2.getShader() == null) {
            canvas.drawRect(rectTmp, blurScrimPaint);
            canvas.drawRect(rectTmp, this.selectedBlurPaint);
        } else {
            canvas.drawRect(rectTmp, blurScrimPaint);
            canvas.drawRect(rectTmp, this.selectedBlurPaint2);
            canvas.saveLayerAlpha((float) rectTmp.left, (float) rectTmp.top, (float) rectTmp.right, (float) rectTmp.bottom, (int) (this.blurCrossfadeProgress * 255.0f), 31);
            canvas.drawRect(rectTmp, blurScrimPaint);
            canvas.drawRect(rectTmp, this.selectedBlurPaint);
            canvas.restore();
        }
        blurScrimPaint.setAlpha(blurAlpha);
        canvas.drawRect(rectTmp, blurScrimPaint);
    }

    public void drawBlurCircle(Canvas canvas, float viewY, float cx, float cy, float radius, Paint blurScrimPaint, boolean top) {
        Canvas canvas2 = canvas;
        float f = cx;
        float f2 = cy;
        float f3 = radius;
        Paint paint = blurScrimPaint;
        int blurAlpha = Color.alpha(Theme.getColor("chat_BlurAlpha"));
        if (this.currentBitmap == null) {
            float f4 = viewY;
            boolean z = top;
        } else if (!SharedConfig.chatBlurEnabled()) {
            float f5 = viewY;
            boolean z2 = top;
        } else {
            updateBlurShaderPosition(viewY, top);
            paint.setAlpha(255);
            if (this.blurCrossfadeProgress == 1.0f || this.selectedBlurPaint2.getShader() == null) {
                canvas2.drawCircle(f, f2, f3, paint);
                canvas2.drawCircle(f, f2, f3, this.selectedBlurPaint);
            } else {
                canvas2.drawCircle(f, f2, f3, paint);
                canvas2.drawCircle(f, f2, f3, this.selectedBlurPaint2);
                Canvas canvas3 = canvas;
                canvas3.saveLayerAlpha(f - f3, f2 - f3, f + f3, f2 + f3, (int) (this.blurCrossfadeProgress * 255.0f), 31);
                canvas2.drawCircle(f, f2, f3, paint);
                canvas2.drawCircle(f, f2, f3, this.selectedBlurPaint);
                canvas.restore();
            }
            paint.setAlpha(blurAlpha);
            canvas2.drawCircle(f, f2, f3, paint);
            return;
        }
        canvas2.drawCircle(f, f2, f3, paint);
    }

    private void updateBlurShaderPosition(float viewY, boolean top) {
        this.selectedBlurPaint = top ? this.blurPaintTop : this.blurPaintBottom;
        this.selectedBlurPaint2 = top ? this.blurPaintTop2 : this.blurPaintBottom2;
        if (top) {
            viewY += getTranslationY();
        }
        if (this.selectedBlurPaint.getShader() != null) {
            this.matrix.reset();
            this.matrix2.reset();
            if (!top) {
                this.matrix.setTranslate(0.0f, ((((-viewY) + this.currentBitmap.bottomOffset) - ((float) this.currentBitmap.pixelFixOffset)) - 34.0f) - (this.currentBitmap.drawnLisetTranslationY - (getBottomOffset() + getListTranslationY())));
                this.matrix.preScale(this.currentBitmap.bottomScaleX, this.currentBitmap.bottomScaleY);
                BlurBitmap blurBitmap = this.prevBitmap;
                if (blurBitmap != null) {
                    this.matrix2.setTranslate(0.0f, ((((-viewY) + blurBitmap.bottomOffset) - ((float) this.prevBitmap.pixelFixOffset)) - 34.0f) - (this.prevBitmap.drawnLisetTranslationY - (getBottomOffset() + getListTranslationY())));
                    this.matrix2.preScale(this.prevBitmap.bottomScaleX, this.prevBitmap.bottomScaleY);
                }
            } else {
                this.matrix.setTranslate(0.0f, ((-viewY) - ((float) this.currentBitmap.pixelFixOffset)) - 34.0f);
                this.matrix.preScale(this.currentBitmap.topScaleX, this.currentBitmap.topScaleY);
                BlurBitmap blurBitmap2 = this.prevBitmap;
                if (blurBitmap2 != null) {
                    this.matrix2.setTranslate(0.0f, ((-viewY) - ((float) blurBitmap2.pixelFixOffset)) - 34.0f);
                    this.matrix2.preScale(this.prevBitmap.topScaleX, this.prevBitmap.topScaleY);
                }
            }
            this.selectedBlurPaint.getShader().setLocalMatrix(this.matrix);
            if (this.selectedBlurPaint2.getShader() != null) {
                this.selectedBlurPaint2.getShader().setLocalMatrix(this.matrix);
            }
        }
    }

    /* access modifiers changed from: protected */
    public float getBottomTranslation() {
        return 0.0f;
    }

    private static class BlurBitmap {
        Bitmap bottomBitmap;
        Canvas bottomCanvas;
        float bottomOffset;
        float bottomScaleX;
        float bottomScaleY;
        float drawnLisetTranslationY;
        public boolean needBlurBottom;
        int pixelFixOffset;
        Bitmap topBitmap;
        Canvas topCanvas;
        float topScaleX;
        float topScaleY;

        private BlurBitmap() {
        }

        public void recycle() {
            this.topBitmap.recycle();
            Bitmap bitmap = this.bottomBitmap;
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }
}
