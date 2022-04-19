package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ComposeShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import java.lang.ref.WeakReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;

public class MotionBackgroundDrawable extends Drawable {
    private static boolean errorWhileGenerateLegacyBitmap = false;
    private static float legacyBitmapScale = 0.7f;
    private static final boolean useLegacyBitmap;
    private static final boolean useSoftLight;
    private int alpha;
    private float backgroundAlpha;
    private BitmapShader bitmapShader;
    private int[] colors;
    private Bitmap currentBitmap;
    private boolean fastAnimation;
    private Canvas gradientCanvas;
    private GradientDrawable gradientDrawable;
    private Bitmap gradientFromBitmap;
    private Canvas gradientFromCanvas;
    private BitmapShader gradientShader;
    private Bitmap[] gradientToBitmap;
    private int intensity;
    private final CubicBezierInterpolator interpolator;
    private boolean invalidateLegacy;
    private boolean isIndeterminateAnimation;
    private boolean isPreview;
    private long lastUpdateTime;
    private Bitmap legacyBitmap;
    private Bitmap legacyBitmap2;
    private int legacyBitmapColor;
    private ColorFilter legacyBitmapColorFilter;
    private Canvas legacyCanvas;
    private Canvas legacyCanvas2;
    private Matrix matrix;
    private Paint overrideBitmapPaint;
    private Paint paint;
    private Paint paint2;
    private Paint paint3;
    private WeakReference<View> parentView;
    private float patternAlpha;
    private Bitmap patternBitmap;
    private Rect patternBounds;
    private ColorFilter patternColorFilter;
    private int phase;
    public float posAnimationProgress;
    private boolean postInvalidateParent;
    private RectF rect;
    private boolean rotatingPreview;
    private boolean rotationBack;
    private int roundRadius;
    private int translationY;
    private Runnable updateAnimationRunnable;

    public int getOpacity() {
        return -2;
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    static {
        int i = Build.VERSION.SDK_INT;
        boolean z = true;
        useLegacyBitmap = i < 28;
        if (i < 29) {
            z = false;
        }
        useSoftLight = z;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        updateAnimation(true);
    }

    public MotionBackgroundDrawable() {
        this.colors = new int[]{-12423849, -531317, -7888252, -133430};
        this.interpolator = new CubicBezierInterpolator(0.33d, 0.0d, 0.0d, 1.0d);
        this.posAnimationProgress = 1.0f;
        this.rect = new RectF();
        this.gradientToBitmap = new Bitmap[3];
        this.paint = new Paint(2);
        this.paint2 = new Paint(2);
        this.paint3 = new Paint();
        this.intensity = 100;
        this.gradientDrawable = new GradientDrawable();
        this.updateAnimationRunnable = new MotionBackgroundDrawable$$ExternalSyntheticLambda0(this);
        this.patternBounds = new Rect();
        this.patternAlpha = 1.0f;
        this.backgroundAlpha = 1.0f;
        this.alpha = 255;
        init();
    }

    public MotionBackgroundDrawable(int i, int i2, int i3, int i4, boolean z) {
        this(i, i2, i3, i4, 0, z);
    }

    public MotionBackgroundDrawable(int i, int i2, int i3, int i4, int i5, boolean z) {
        this.colors = new int[]{-12423849, -531317, -7888252, -133430};
        this.interpolator = new CubicBezierInterpolator(0.33d, 0.0d, 0.0d, 1.0d);
        this.posAnimationProgress = 1.0f;
        this.rect = new RectF();
        this.gradientToBitmap = new Bitmap[3];
        this.paint = new Paint(2);
        this.paint2 = new Paint(2);
        this.paint3 = new Paint();
        this.intensity = 100;
        this.gradientDrawable = new GradientDrawable();
        this.updateAnimationRunnable = new MotionBackgroundDrawable$$ExternalSyntheticLambda0(this);
        this.patternBounds = new Rect();
        this.patternAlpha = 1.0f;
        this.backgroundAlpha = 1.0f;
        this.alpha = 255;
        this.isPreview = z;
        setColors(i, i2, i3, i4, i5, false);
        init();
    }

    @SuppressLint({"NewApi"})
    private void init() {
        this.currentBitmap = Bitmap.createBitmap(60, 80, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < 3; i++) {
            this.gradientToBitmap[i] = Bitmap.createBitmap(60, 80, Bitmap.Config.ARGB_8888);
        }
        this.gradientCanvas = new Canvas(this.currentBitmap);
        this.gradientFromBitmap = Bitmap.createBitmap(60, 80, Bitmap.Config.ARGB_8888);
        this.gradientFromCanvas = new Canvas(this.gradientFromBitmap);
        Utilities.generateGradient(this.currentBitmap, true, this.phase, this.interpolator.getInterpolation(this.posAnimationProgress), this.currentBitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
        if (useSoftLight) {
            this.paint2.setBlendMode(BlendMode.SOFT_LIGHT);
        }
    }

    public void setRoundRadius(int i) {
        this.roundRadius = i;
        this.matrix = new Matrix();
        Bitmap bitmap = this.currentBitmap;
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        BitmapShader bitmapShader2 = new BitmapShader(bitmap, tileMode, tileMode);
        this.bitmapShader = bitmapShader2;
        this.paint.setShader(bitmapShader2);
        invalidateParent();
    }

    public BitmapShader getBitmapShader() {
        return this.bitmapShader;
    }

    public Bitmap getBitmap() {
        return this.currentBitmap;
    }

    public int getIntensity() {
        return this.intensity;
    }

    public static boolean isDark(int i, int i2, int i3, int i4) {
        int averageColor = AndroidUtilities.getAverageColor(i, i2);
        if (i3 != 0) {
            averageColor = AndroidUtilities.getAverageColor(averageColor, i3);
        }
        if (i4 != 0) {
            averageColor = AndroidUtilities.getAverageColor(averageColor, i4);
        }
        return AndroidUtilities.RGBtoHSB(Color.red(averageColor), Color.green(averageColor), Color.blue(averageColor))[2] < 0.3f;
    }

    public void setBounds(Rect rect2) {
        super.setBounds(rect2);
        this.patternBounds.set(rect2);
    }

    public static int getPatternColor(int i, int i2, int i3, int i4) {
        if (isDark(i, i2, i3, i4)) {
            return !useSoftLight ? Integer.MAX_VALUE : -1;
        }
        if (useSoftLight) {
            return -16777216;
        }
        int averageColor = AndroidUtilities.getAverageColor(i3, AndroidUtilities.getAverageColor(i, i2));
        if (i4 != 0) {
            averageColor = AndroidUtilities.getAverageColor(i4, averageColor);
        }
        return (AndroidUtilities.getPatternColor(averageColor, true) & 16777215) | NUM;
    }

    public int getPatternColor() {
        int[] iArr = this.colors;
        return getPatternColor(iArr[0], iArr[1], iArr[2], iArr[3]);
    }

    public int getPhase() {
        return this.phase;
    }

    public void setPostInvalidateParent(boolean z) {
        this.postInvalidateParent = z;
    }

    public void rotatePreview(boolean z) {
        if (this.posAnimationProgress >= 1.0f) {
            this.rotatingPreview = true;
            this.posAnimationProgress = 0.0f;
            this.rotationBack = z;
            invalidateParent();
        }
    }

    public void setPhase(int i) {
        this.phase = i;
        if (i < 0) {
            this.phase = 0;
        } else if (i > 7) {
            this.phase = 7;
        }
        Utilities.generateGradient(this.currentBitmap, true, this.phase, this.interpolator.getInterpolation(this.posAnimationProgress), this.currentBitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
    }

    public void switchToNextPosition() {
        switchToNextPosition(false);
    }

    public void switchToNextPosition(boolean z) {
        if (this.posAnimationProgress >= 1.0f) {
            this.rotatingPreview = false;
            this.rotationBack = false;
            this.fastAnimation = z;
            this.posAnimationProgress = 0.0f;
            int i = this.phase - 1;
            this.phase = i;
            if (i < 0) {
                this.phase = 7;
            }
            invalidateParent();
            this.gradientFromCanvas.drawBitmap(this.currentBitmap, 0.0f, 0.0f, (Paint) null);
            generateNextGradient();
        }
    }

    private void generateNextGradient() {
        int i = 0;
        if (useLegacyBitmap && this.intensity < 0) {
            try {
                if (this.legacyBitmap != null) {
                    Bitmap bitmap = this.legacyBitmap2;
                    if (bitmap != null && bitmap.getHeight() == this.legacyBitmap.getHeight()) {
                        if (this.legacyBitmap2.getWidth() == this.legacyBitmap.getWidth()) {
                            this.legacyBitmap2.eraseColor(0);
                            this.legacyCanvas2.drawBitmap(this.legacyBitmap, 0.0f, 0.0f, (Paint) null);
                        }
                    }
                    Bitmap bitmap2 = this.legacyBitmap2;
                    if (bitmap2 != null) {
                        bitmap2.recycle();
                    }
                    this.legacyBitmap2 = Bitmap.createBitmap(this.legacyBitmap.getWidth(), this.legacyBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    this.legacyCanvas2 = new Canvas(this.legacyBitmap2);
                    this.legacyCanvas2.drawBitmap(this.legacyBitmap, 0.0f, 0.0f, (Paint) null);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                Bitmap bitmap3 = this.legacyBitmap2;
                if (bitmap3 != null) {
                    bitmap3.recycle();
                    this.legacyBitmap2 = null;
                }
            }
            Bitmap bitmap4 = this.currentBitmap;
            Utilities.generateGradient(bitmap4, true, this.phase, 1.0f, bitmap4.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
            this.invalidateLegacy = true;
        }
        while (i < 3) {
            int i2 = i + 1;
            Utilities.generateGradient(this.gradientToBitmap[i], true, this.phase, ((float) i2) / 3.0f, this.currentBitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
            i = i2;
        }
    }

    public void switchToPrevPosition(boolean z) {
        if (this.posAnimationProgress >= 1.0f) {
            this.rotatingPreview = false;
            this.fastAnimation = z;
            this.rotationBack = true;
            this.posAnimationProgress = 0.0f;
            invalidateParent();
            Utilities.generateGradient(this.gradientFromBitmap, true, this.phase, 0.0f, this.currentBitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
            generateNextGradient();
        }
    }

    public int[] getColors() {
        return this.colors;
    }

    public void setParentView(View view) {
        this.parentView = new WeakReference<>(view);
    }

    public void setColors(int i, int i2, int i3, int i4) {
        setColors(i, i2, i3, i4, 0, true);
    }

    public void setColors(int i, int i2, int i3, int i4, Bitmap bitmap) {
        int[] iArr = this.colors;
        iArr[0] = i;
        iArr[1] = i2;
        iArr[2] = i3;
        iArr[3] = i4;
        Utilities.generateGradient(bitmap, true, this.phase, this.interpolator.getInterpolation(this.posAnimationProgress), this.currentBitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
    }

    public void setColors(int i, int i2, int i3, int i4, int i5, boolean z) {
        int i6 = i;
        int i7 = i2;
        int i8 = i3;
        int i9 = i4;
        if (this.isPreview && i8 == 0 && i9 == 0) {
            this.gradientDrawable = new GradientDrawable(BackgroundGradientDrawable.getGradientOrientation(i5), new int[]{i6, i7});
        } else {
            this.gradientDrawable = null;
        }
        int[] iArr = this.colors;
        if (iArr[0] != i6 || iArr[1] != i7 || iArr[2] != i8 || iArr[3] != i9) {
            iArr[0] = i6;
            iArr[1] = i7;
            iArr[2] = i8;
            iArr[3] = i9;
            Bitmap bitmap = this.currentBitmap;
            if (bitmap != null) {
                Utilities.generateGradient(bitmap, true, this.phase, this.interpolator.getInterpolation(this.posAnimationProgress), this.currentBitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
                if (z) {
                    invalidateParent();
                }
            }
        }
    }

    private void invalidateParent() {
        invalidateSelf();
        WeakReference<View> weakReference = this.parentView;
        if (!(weakReference == null || weakReference.get() == null)) {
            ((View) this.parentView.get()).invalidate();
        }
        if (this.postInvalidateParent) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.invalidateMotionBackground, new Object[0]);
            updateAnimation(false);
            AndroidUtilities.cancelRunOnUIThread(this.updateAnimationRunnable);
            AndroidUtilities.runOnUIThread(this.updateAnimationRunnable, 16);
        }
    }

    public boolean hasPattern() {
        return this.patternBitmap != null;
    }

    public int getIntrinsicWidth() {
        Bitmap bitmap = this.patternBitmap;
        if (bitmap != null) {
            return bitmap.getWidth();
        }
        return super.getIntrinsicWidth();
    }

    public int getIntrinsicHeight() {
        Bitmap bitmap = this.patternBitmap;
        if (bitmap != null) {
            return bitmap.getHeight();
        }
        return super.getIntrinsicHeight();
    }

    public void setTranslationY(int i) {
        this.translationY = i;
    }

    public void setPatternBitmap(int i) {
        setPatternBitmap(i, this.patternBitmap);
    }

    @SuppressLint({"NewApi"})
    public void setPatternBitmap(int i, Bitmap bitmap) {
        this.intensity = i;
        this.patternBitmap = bitmap;
        this.invalidateLegacy = true;
        if (bitmap != null) {
            if (useSoftLight) {
                if (i >= 0) {
                    this.paint2.setBlendMode(BlendMode.SOFT_LIGHT);
                } else {
                    this.paint2.setBlendMode((BlendMode) null);
                }
            }
            if (i < 0) {
                if (!useLegacyBitmap) {
                    Bitmap bitmap2 = this.currentBitmap;
                    Shader.TileMode tileMode = Shader.TileMode.CLAMP;
                    this.bitmapShader = new BitmapShader(bitmap2, tileMode, tileMode);
                    Bitmap bitmap3 = this.patternBitmap;
                    Shader.TileMode tileMode2 = Shader.TileMode.CLAMP;
                    this.gradientShader = new BitmapShader(bitmap3, tileMode2, tileMode2);
                    this.paint2.setShader(new ComposeShader(this.bitmapShader, this.gradientShader, PorterDuff.Mode.DST_IN));
                    this.matrix = new Matrix();
                    return;
                }
                createLegacyBitmap();
                if (!errorWhileGenerateLegacyBitmap) {
                    this.paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                } else {
                    this.paint2.setXfermode((Xfermode) null);
                }
            } else if (useLegacyBitmap) {
                this.paint2.setXfermode((Xfermode) null);
            }
        }
    }

    public void setPatternColorFilter(int i) {
        this.patternColorFilter = new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN);
        invalidateParent();
    }

    public void setPatternAlpha(float f) {
        this.patternAlpha = f;
        invalidateParent();
    }

    public void setBackgroundAlpha(float f) {
        this.backgroundAlpha = f;
        invalidateParent();
    }

    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        this.patternBounds.set(i, i2, i3, i4);
        createLegacyBitmap();
    }

    private void createLegacyBitmap() {
        if (useLegacyBitmap && this.intensity < 0 && !errorWhileGenerateLegacyBitmap) {
            int width = (int) (((float) this.patternBounds.width()) * legacyBitmapScale);
            int height = (int) (((float) this.patternBounds.height()) * legacyBitmapScale);
            if (width > 0 && height > 0) {
                Bitmap bitmap = this.legacyBitmap;
                if (bitmap == null || bitmap.getWidth() != width || this.legacyBitmap.getHeight() != height) {
                    Bitmap bitmap2 = this.legacyBitmap;
                    if (bitmap2 != null) {
                        bitmap2.recycle();
                    }
                    Bitmap bitmap3 = this.legacyBitmap2;
                    if (bitmap3 != null) {
                        bitmap3.recycle();
                        this.legacyBitmap2 = null;
                    }
                    try {
                        this.legacyBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        this.legacyCanvas = new Canvas(this.legacyBitmap);
                        this.invalidateLegacy = true;
                    } catch (Exception e) {
                        Bitmap bitmap4 = this.legacyBitmap;
                        if (bitmap4 != null) {
                            bitmap4.recycle();
                            this.legacyBitmap = null;
                        }
                        FileLog.e((Throwable) e);
                        errorWhileGenerateLegacyBitmap = true;
                        this.paint2.setXfermode((Xfermode) null);
                    }
                }
            }
        }
    }

    public void drawBackground(Canvas canvas) {
        Rect bounds = getBounds();
        canvas.save();
        float f = (float) (this.patternBitmap != null ? bounds.top : this.translationY);
        int width = this.currentBitmap.getWidth();
        int height = this.currentBitmap.getHeight();
        float width2 = (float) bounds.width();
        float height2 = (float) bounds.height();
        float f2 = (float) width;
        float f3 = (float) height;
        float max = Math.max(width2 / f2, height2 / f3);
        float f4 = f2 * max;
        float f5 = f3 * max;
        float f6 = (width2 - f4) / 2.0f;
        float f7 = (height2 - f5) / 2.0f;
        if (this.isPreview) {
            int i = bounds.left;
            f6 += (float) i;
            int i2 = bounds.top;
            f7 += (float) i2;
            canvas.clipRect(i, i2, bounds.right, bounds.bottom);
        }
        if (this.intensity < 0) {
            canvas.drawColor(ColorUtils.setAlphaComponent(-16777216, (int) (((float) this.alpha) * this.backgroundAlpha)));
        } else if (this.roundRadius != 0) {
            this.matrix.reset();
            this.matrix.setTranslate(f6, f7);
            float min = 1.0f / Math.min(((float) this.currentBitmap.getWidth()) / ((float) bounds.width()), ((float) this.currentBitmap.getHeight()) / ((float) bounds.height()));
            this.matrix.preScale(min, min);
            this.bitmapShader.setLocalMatrix(this.matrix);
            this.rect.set((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom);
            int alpha2 = this.paint.getAlpha();
            this.paint.setAlpha((int) (((float) alpha2) * this.backgroundAlpha));
            RectF rectF = this.rect;
            int i3 = this.roundRadius;
            canvas.drawRoundRect(rectF, (float) i3, (float) i3, this.paint);
            this.paint.setAlpha(alpha2);
        } else {
            canvas.translate(0.0f, f);
            GradientDrawable gradientDrawable2 = this.gradientDrawable;
            if (gradientDrawable2 != null) {
                gradientDrawable2.setBounds((int) f6, (int) f7, (int) (f6 + f4), (int) (f7 + f5));
                this.gradientDrawable.setAlpha((int) (this.backgroundAlpha * 255.0f));
                this.gradientDrawable.draw(canvas);
            } else {
                this.rect.set(f6, f7, f4 + f6, f5 + f7);
                Paint paint4 = this.overrideBitmapPaint;
                if (paint4 == null) {
                    paint4 = this.paint;
                }
                int alpha3 = paint4.getAlpha();
                paint4.setAlpha((int) (((float) alpha3) * this.backgroundAlpha));
                canvas.drawBitmap(this.currentBitmap, (Rect) null, this.rect, paint4);
                paint4.setAlpha(alpha3);
            }
        }
        canvas.restore();
        updateAnimation(true);
    }

    public void drawPattern(Canvas canvas) {
        Rect bounds = getBounds();
        canvas.save();
        float f = (float) (this.patternBitmap != null ? bounds.top : this.translationY);
        int width = this.currentBitmap.getWidth();
        int height = this.currentBitmap.getHeight();
        float width2 = (float) bounds.width();
        float height2 = (float) bounds.height();
        float f2 = (float) width;
        float f3 = (float) height;
        float max = Math.max(width2 / f2, height2 / f3);
        float f4 = (width2 - (f2 * max)) / 2.0f;
        float f5 = (height2 - (f3 * max)) / 2.0f;
        if (this.isPreview) {
            int i = bounds.left;
            f4 += (float) i;
            int i2 = bounds.top;
            f5 += (float) i2;
            canvas.clipRect(i, i2, bounds.right, bounds.bottom);
        }
        if (this.intensity < 0) {
            Bitmap bitmap = this.patternBitmap;
            if (bitmap != null) {
                if (!useLegacyBitmap) {
                    if (this.matrix == null) {
                        this.matrix = new Matrix();
                    }
                    this.matrix.reset();
                    this.matrix.setTranslate(f4, f5 + f);
                    float min = 1.0f / Math.min(((float) this.currentBitmap.getWidth()) / ((float) bounds.width()), ((float) this.currentBitmap.getHeight()) / ((float) bounds.height()));
                    this.matrix.preScale(min, min);
                    this.bitmapShader.setLocalMatrix(this.matrix);
                    this.matrix.reset();
                    float width3 = (float) this.patternBitmap.getWidth();
                    float height3 = (float) this.patternBitmap.getHeight();
                    float max2 = Math.max(width2 / width3, height2 / height3);
                    this.matrix.setTranslate((width2 - (width3 * max2)) / 2.0f, ((height2 - (height3 * max2)) / 2.0f) + f);
                    this.matrix.preScale(max2, max2);
                    this.gradientShader.setLocalMatrix(this.matrix);
                    this.paint2.setColorFilter((ColorFilter) null);
                    this.paint2.setAlpha((int) ((((float) Math.abs(this.intensity)) / 100.0f) * ((float) this.alpha) * this.patternAlpha));
                    this.rect.set((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom);
                    RectF rectF = this.rect;
                    int i3 = this.roundRadius;
                    canvas.drawRoundRect(rectF, (float) i3, (float) i3, this.paint2);
                } else if (errorWhileGenerateLegacyBitmap) {
                    float width4 = (float) bitmap.getWidth();
                    float height4 = (float) this.patternBitmap.getHeight();
                    float max3 = Math.max(width2 / width4, height2 / height4);
                    float f6 = width4 * max3;
                    float f7 = height4 * max3;
                    float f8 = (width2 - f6) / 2.0f;
                    float f9 = (height2 - f7) / 2.0f;
                    this.rect.set(f8, f9, f6 + f8, f7 + f9);
                    int[] iArr = this.colors;
                    int averageColor = AndroidUtilities.getAverageColor(iArr[2], AndroidUtilities.getAverageColor(iArr[0], iArr[1]));
                    int[] iArr2 = this.colors;
                    if (iArr2[3] != 0) {
                        averageColor = AndroidUtilities.getAverageColor(iArr2[3], averageColor);
                    }
                    if (this.legacyBitmapColorFilter == null || averageColor != this.legacyBitmapColor) {
                        this.legacyBitmapColor = averageColor;
                        this.legacyBitmapColorFilter = new PorterDuffColorFilter(averageColor, PorterDuff.Mode.SRC_IN);
                    }
                    this.paint2.setColorFilter(this.legacyBitmapColorFilter);
                    this.paint2.setAlpha((int) ((((float) Math.abs(this.intensity)) / 100.0f) * ((float) this.alpha) * this.patternAlpha));
                    canvas.translate(0.0f, f);
                    canvas.drawBitmap(this.patternBitmap, (Rect) null, this.rect, this.paint2);
                } else {
                    Bitmap bitmap2 = this.legacyBitmap;
                    if (bitmap2 != null) {
                        if (this.invalidateLegacy) {
                            this.rect.set(0.0f, 0.0f, (float) bitmap2.getWidth(), (float) this.legacyBitmap.getHeight());
                            int alpha2 = this.paint.getAlpha();
                            this.paint.setAlpha(255);
                            this.legacyCanvas.drawBitmap(this.currentBitmap, (Rect) null, this.rect, this.paint);
                            this.paint.setAlpha(alpha2);
                            float width5 = (float) this.patternBitmap.getWidth();
                            float height5 = (float) this.patternBitmap.getHeight();
                            float max4 = Math.max(width2 / width5, height2 / height5);
                            float var_ = width5 * max4;
                            float var_ = height5 * max4;
                            float var_ = (width2 - var_) / 2.0f;
                            float var_ = (height2 - var_) / 2.0f;
                            this.rect.set(var_, var_, var_ + var_, var_ + var_);
                            this.paint2.setColorFilter((ColorFilter) null);
                            this.paint2.setAlpha((int) ((((float) Math.abs(this.intensity)) / 100.0f) * 255.0f));
                            this.legacyCanvas.save();
                            Canvas canvas2 = this.legacyCanvas;
                            float var_ = legacyBitmapScale;
                            canvas2.scale(var_, var_);
                            this.legacyCanvas.drawBitmap(this.patternBitmap, (Rect) null, this.rect, this.paint2);
                            this.legacyCanvas.restore();
                            this.invalidateLegacy = false;
                        }
                        this.rect.set((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom);
                        if (this.legacyBitmap2 != null) {
                            float var_ = this.posAnimationProgress;
                            if (var_ != 1.0f) {
                                this.paint.setAlpha((int) (((float) this.alpha) * this.patternAlpha * (1.0f - var_)));
                                canvas.drawBitmap(this.legacyBitmap2, (Rect) null, this.rect, this.paint);
                                this.paint.setAlpha((int) (((float) this.alpha) * this.patternAlpha * this.posAnimationProgress));
                                canvas.drawBitmap(this.legacyBitmap, (Rect) null, this.rect, this.paint);
                                this.paint.setAlpha(this.alpha);
                            }
                        }
                        canvas.drawBitmap(this.legacyBitmap, (Rect) null, this.rect, this.paint);
                    }
                }
            }
        } else {
            Bitmap bitmap3 = this.patternBitmap;
            if (bitmap3 != null) {
                float width6 = (float) bitmap3.getWidth();
                float height6 = (float) this.patternBitmap.getHeight();
                float max5 = Math.max(width2 / width6, height2 / height6);
                float var_ = width6 * max5;
                float var_ = height6 * max5;
                float var_ = (width2 - var_) / 2.0f;
                float var_ = (height2 - var_) / 2.0f;
                this.rect.set(var_, var_, var_ + var_, var_ + var_);
                this.paint2.setColorFilter(this.patternColorFilter);
                this.paint2.setAlpha((int) ((((float) Math.abs(this.intensity)) / 100.0f) * ((float) this.alpha) * this.patternAlpha));
                canvas.drawBitmap(this.patternBitmap, (Rect) null, this.rect, this.paint2);
            }
        }
        canvas.restore();
        updateAnimation(true);
    }

    public void draw(Canvas canvas) {
        Canvas canvas2 = canvas;
        Rect bounds = getBounds();
        canvas.save();
        float f = (float) (this.patternBitmap != null ? bounds.top : this.translationY);
        int width = this.currentBitmap.getWidth();
        int height = this.currentBitmap.getHeight();
        float width2 = (float) bounds.width();
        float height2 = (float) bounds.height();
        float f2 = (float) width;
        float f3 = (float) height;
        float max = Math.max(width2 / f2, height2 / f3);
        float f4 = f2 * max;
        float f5 = f3 * max;
        float f6 = (width2 - f4) / 2.0f;
        float f7 = (height2 - f5) / 2.0f;
        if (this.isPreview) {
            int i = bounds.left;
            f6 += (float) i;
            int i2 = bounds.top;
            f7 += (float) i2;
            canvas2.clipRect(i, i2, bounds.right, bounds.bottom);
        }
        if (this.intensity < 0) {
            canvas2.drawColor(ColorUtils.setAlphaComponent(-16777216, (int) (((float) this.alpha) * this.backgroundAlpha)));
            Bitmap bitmap = this.patternBitmap;
            if (bitmap != null) {
                if (!useLegacyBitmap) {
                    if (this.matrix == null) {
                        this.matrix = new Matrix();
                    }
                    this.matrix.reset();
                    this.matrix.setTranslate(f6, f7 + f);
                    float min = 1.0f / Math.min(((float) this.currentBitmap.getWidth()) / ((float) bounds.width()), ((float) this.currentBitmap.getHeight()) / ((float) bounds.height()));
                    this.matrix.preScale(min, min);
                    this.bitmapShader.setLocalMatrix(this.matrix);
                    this.matrix.reset();
                    float width3 = (float) this.patternBitmap.getWidth();
                    float height3 = (float) this.patternBitmap.getHeight();
                    float max2 = Math.max(width2 / width3, height2 / height3);
                    this.matrix.setTranslate((width2 - (width3 * max2)) / 2.0f, ((height2 - (height3 * max2)) / 2.0f) + f);
                    this.matrix.preScale(max2, max2);
                    this.gradientShader.setLocalMatrix(this.matrix);
                    this.paint2.setColorFilter((ColorFilter) null);
                    this.paint2.setAlpha((int) ((((float) Math.abs(this.intensity)) / 100.0f) * ((float) this.alpha) * this.patternAlpha));
                    this.rect.set((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom);
                    RectF rectF = this.rect;
                    int i3 = this.roundRadius;
                    canvas2.drawRoundRect(rectF, (float) i3, (float) i3, this.paint2);
                } else if (errorWhileGenerateLegacyBitmap) {
                    float width4 = (float) bitmap.getWidth();
                    float height4 = (float) this.patternBitmap.getHeight();
                    float max3 = Math.max(width2 / width4, height2 / height4);
                    float f8 = width4 * max3;
                    float f9 = height4 * max3;
                    float var_ = (width2 - f8) / 2.0f;
                    float var_ = (height2 - f9) / 2.0f;
                    this.rect.set(var_, var_, f8 + var_, f9 + var_);
                    int[] iArr = this.colors;
                    int averageColor = AndroidUtilities.getAverageColor(iArr[2], AndroidUtilities.getAverageColor(iArr[0], iArr[1]));
                    int[] iArr2 = this.colors;
                    if (iArr2[3] != 0) {
                        averageColor = AndroidUtilities.getAverageColor(iArr2[3], averageColor);
                    }
                    if (this.legacyBitmapColorFilter == null || averageColor != this.legacyBitmapColor) {
                        this.legacyBitmapColor = averageColor;
                        this.legacyBitmapColorFilter = new PorterDuffColorFilter(averageColor, PorterDuff.Mode.SRC_IN);
                    }
                    this.paint2.setColorFilter(this.legacyBitmapColorFilter);
                    this.paint2.setAlpha((int) ((((float) Math.abs(this.intensity)) / 100.0f) * ((float) this.alpha) * this.patternAlpha));
                    canvas2.translate(0.0f, f);
                    canvas2.drawBitmap(this.patternBitmap, (Rect) null, this.rect, this.paint2);
                } else {
                    Bitmap bitmap2 = this.legacyBitmap;
                    if (bitmap2 != null) {
                        if (this.invalidateLegacy) {
                            this.rect.set(0.0f, 0.0f, (float) bitmap2.getWidth(), (float) this.legacyBitmap.getHeight());
                            int alpha2 = this.paint.getAlpha();
                            this.paint.setAlpha(255);
                            this.legacyCanvas.drawBitmap(this.currentBitmap, (Rect) null, this.rect, this.paint);
                            this.paint.setAlpha(alpha2);
                            float width5 = (float) this.patternBitmap.getWidth();
                            float height5 = (float) this.patternBitmap.getHeight();
                            float max4 = Math.max(width2 / width5, height2 / height5);
                            float var_ = width5 * max4;
                            float var_ = height5 * max4;
                            float var_ = (width2 - var_) / 2.0f;
                            float var_ = (height2 - var_) / 2.0f;
                            this.rect.set(var_, var_, var_ + var_, var_ + var_);
                            this.paint2.setColorFilter((ColorFilter) null);
                            this.paint2.setAlpha((int) ((((float) Math.abs(this.intensity)) / 100.0f) * 255.0f));
                            this.legacyCanvas.save();
                            Canvas canvas3 = this.legacyCanvas;
                            float var_ = legacyBitmapScale;
                            canvas3.scale(var_, var_);
                            this.legacyCanvas.drawBitmap(this.patternBitmap, (Rect) null, this.rect, this.paint2);
                            this.legacyCanvas.restore();
                            this.invalidateLegacy = false;
                        }
                        this.rect.set((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom);
                        if (this.legacyBitmap2 != null) {
                            float var_ = this.posAnimationProgress;
                            if (var_ != 1.0f) {
                                this.paint.setAlpha((int) (((float) this.alpha) * this.patternAlpha * (1.0f - var_)));
                                canvas2.drawBitmap(this.legacyBitmap2, (Rect) null, this.rect, this.paint);
                                this.paint.setAlpha((int) (((float) this.alpha) * this.patternAlpha * this.posAnimationProgress));
                                canvas2.drawBitmap(this.legacyBitmap, (Rect) null, this.rect, this.paint);
                                this.paint.setAlpha(this.alpha);
                            }
                        }
                        canvas2.drawBitmap(this.legacyBitmap, (Rect) null, this.rect, this.paint);
                    }
                }
            }
        } else {
            if (this.roundRadius != 0) {
                this.matrix.reset();
                this.matrix.setTranslate(f6, f7);
                float min2 = 1.0f / Math.min(((float) this.currentBitmap.getWidth()) / ((float) bounds.width()), ((float) this.currentBitmap.getHeight()) / ((float) bounds.height()));
                this.matrix.preScale(min2, min2);
                this.bitmapShader.setLocalMatrix(this.matrix);
                this.rect.set((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom);
                RectF rectF2 = this.rect;
                int i4 = this.roundRadius;
                canvas2.drawRoundRect(rectF2, (float) i4, (float) i4, this.paint);
            } else {
                canvas2.translate(0.0f, f);
                GradientDrawable gradientDrawable2 = this.gradientDrawable;
                if (gradientDrawable2 != null) {
                    gradientDrawable2.setBounds((int) f6, (int) f7, (int) (f6 + f4), (int) (f7 + f5));
                    this.gradientDrawable.setAlpha((int) (this.backgroundAlpha * 255.0f));
                    this.gradientDrawable.draw(canvas2);
                } else {
                    this.rect.set(f6, f7, f4 + f6, f5 + f7);
                    Paint paint4 = this.overrideBitmapPaint;
                    if (paint4 == null) {
                        paint4 = this.paint;
                    }
                    int alpha3 = paint4.getAlpha();
                    paint4.setAlpha((int) (((float) alpha3) * this.backgroundAlpha));
                    canvas2.drawBitmap(this.currentBitmap, (Rect) null, this.rect, paint4);
                    paint4.setAlpha(alpha3);
                }
            }
            Bitmap bitmap3 = this.patternBitmap;
            if (bitmap3 != null) {
                float width6 = (float) bitmap3.getWidth();
                float height6 = (float) this.patternBitmap.getHeight();
                float max5 = Math.max(width2 / width6, height2 / height6);
                float var_ = width6 * max5;
                float var_ = height6 * max5;
                float var_ = (width2 - var_) / 2.0f;
                float var_ = (height2 - var_) / 2.0f;
                this.rect.set(var_, var_, var_ + var_, var_ + var_);
                this.paint2.setColorFilter(this.patternColorFilter);
                this.paint2.setAlpha((int) ((((float) Math.abs(this.intensity)) / 100.0f) * ((float) this.alpha) * this.patternAlpha));
                canvas2.drawBitmap(this.patternBitmap, (Rect) null, this.rect, this.paint2);
            }
        }
        canvas.restore();
        updateAnimation(true);
    }

    public void updateAnimation(boolean z) {
        float f;
        float f2;
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long j = elapsedRealtime - this.lastUpdateTime;
        if (j > 20) {
            j = 17;
        }
        this.lastUpdateTime = elapsedRealtime;
        if (j > 1) {
            boolean z2 = this.isIndeterminateAnimation;
            if (z2 && this.posAnimationProgress == 1.0f) {
                this.posAnimationProgress = 0.0f;
            }
            float f3 = this.posAnimationProgress;
            if (f3 < 1.0f) {
                boolean z3 = this.postInvalidateParent || this.rotatingPreview;
                if (z2) {
                    float f4 = f3 + (((float) j) / 12000.0f);
                    this.posAnimationProgress = f4;
                    if (f4 >= 1.0f) {
                        this.posAnimationProgress = 0.0f;
                    }
                    float f5 = this.posAnimationProgress;
                    int i = (int) (f5 / 0.125f);
                    this.phase = i;
                    f = 1.0f - ((f5 - (((float) i) * 0.125f)) / 0.125f);
                    z3 = true;
                } else {
                    if (this.rotatingPreview) {
                        float interpolation = this.interpolator.getInterpolation(f3);
                        char c = interpolation <= 0.25f ? 0 : interpolation <= 0.5f ? 1 : interpolation <= 0.75f ? (char) 2 : 3;
                        float f6 = this.posAnimationProgress + (((float) j) / (this.rotationBack ? 1000.0f : 2000.0f));
                        this.posAnimationProgress = f6;
                        if (f6 > 1.0f) {
                            this.posAnimationProgress = 1.0f;
                        }
                        float interpolation2 = this.interpolator.getInterpolation(this.posAnimationProgress);
                        if ((c == 0 && interpolation2 > 0.25f) || ((c == 1 && interpolation2 > 0.5f) || (c == 2 && interpolation2 > 0.75f))) {
                            if (this.rotationBack) {
                                int i2 = this.phase + 1;
                                this.phase = i2;
                                if (i2 > 7) {
                                    this.phase = 0;
                                }
                            } else {
                                int i3 = this.phase - 1;
                                this.phase = i3;
                                if (i3 < 0) {
                                    this.phase = 7;
                                }
                            }
                        }
                        if (interpolation2 > 0.25f) {
                            interpolation2 = interpolation2 <= 0.5f ? interpolation2 - 0.25f : interpolation2 <= 0.75f ? interpolation2 - 0.5f : interpolation2 - 0.75f;
                        }
                        float f7 = interpolation2 / 0.25f;
                        if (this.rotationBack) {
                            f2 = 1.0f - f7;
                            if (this.posAnimationProgress >= 1.0f) {
                                int i4 = this.phase + 1;
                                this.phase = i4;
                                if (i4 > 7) {
                                    this.phase = 0;
                                }
                                f2 = 1.0f;
                            }
                        } else {
                            f2 = f7;
                        }
                    } else {
                        float f8 = f3 + (((float) j) / (this.fastAnimation ? 300.0f : 500.0f));
                        this.posAnimationProgress = f8;
                        if (f8 > 1.0f) {
                            this.posAnimationProgress = 1.0f;
                        }
                        f2 = this.interpolator.getInterpolation(this.posAnimationProgress);
                        if (this.rotationBack) {
                            f2 = 1.0f - f2;
                            if (this.posAnimationProgress >= 1.0f) {
                                int i5 = this.phase + 1;
                                this.phase = i5;
                                if (i5 > 7) {
                                    this.phase = 0;
                                }
                                f = 1.0f;
                            }
                        }
                    }
                    f = f2;
                }
                if (z3) {
                    Bitmap bitmap = this.currentBitmap;
                    Utilities.generateGradient(bitmap, true, this.phase, f, bitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
                    this.invalidateLegacy = true;
                } else if (!useLegacyBitmap || this.intensity >= 0) {
                    if (f != 1.0f) {
                        int i6 = (int) (f / 0.33333334f);
                        if (i6 == 0) {
                            this.gradientCanvas.drawBitmap(this.gradientFromBitmap, 0.0f, 0.0f, (Paint) null);
                        } else {
                            this.gradientCanvas.drawBitmap(this.gradientToBitmap[i6 - 1], 0.0f, 0.0f, (Paint) null);
                        }
                        this.paint3.setAlpha((int) (((f - (((float) i6) * 0.33333334f)) / 0.33333334f) * 255.0f));
                        this.gradientCanvas.drawBitmap(this.gradientToBitmap[i6], 0.0f, 0.0f, this.paint3);
                    } else {
                        this.gradientCanvas.drawBitmap(this.gradientToBitmap[2], 0.0f, 0.0f, this.paint3);
                    }
                }
                if (z) {
                    invalidateParent();
                }
            }
        }
    }

    public void setAlpha(int i) {
        this.alpha = i;
        this.paint.setAlpha(i);
        this.paint2.setAlpha(i);
    }

    public boolean isOneColor() {
        int[] iArr = this.colors;
        return iArr[0] == iArr[1] && iArr[0] == iArr[2] && iArr[0] == iArr[3];
    }

    public void setIndeterminateAnimation(boolean z) {
        this.isIndeterminateAnimation = z;
    }
}
