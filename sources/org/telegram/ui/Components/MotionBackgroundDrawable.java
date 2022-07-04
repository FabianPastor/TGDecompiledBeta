package org.telegram.ui.Components;

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
    private static final int ANIMATION_CACHE_BITMAPS_COUNT = 3;
    private static boolean errorWhileGenerateLegacyBitmap = false;
    private static float legacyBitmapScale = 0.7f;
    private static final boolean useLegacyBitmap = (Build.VERSION.SDK_INT < 28);
    private static final boolean useSoftLight;
    private int alpha;
    private float backgroundAlpha;
    private BitmapShader bitmapShader;
    private int[] colors;
    private Bitmap currentBitmap;
    private boolean disableGradientShaderScaling;
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

    static {
        boolean z = true;
        if (Build.VERSION.SDK_INT < 29) {
            z = false;
        }
        useSoftLight = z;
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-MotionBackgroundDrawable  reason: not valid java name */
    public /* synthetic */ void m1117lambda$new$0$orgtelegramuiComponentsMotionBackgroundDrawable() {
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

    public MotionBackgroundDrawable(int c1, int c2, int c3, int c4, boolean preview) {
        this(c1, c2, c3, c4, 0, preview);
    }

    public MotionBackgroundDrawable(int c1, int c2, int c3, int c4, int rotation, boolean preview) {
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
        this.isPreview = preview;
        setColors(c1, c2, c3, c4, rotation, false);
        init();
    }

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

    public void setRoundRadius(int rad) {
        this.roundRadius = rad;
        this.matrix = new Matrix();
        BitmapShader bitmapShader2 = new BitmapShader(this.currentBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
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

    public static boolean isDark(int color1, int color2, int color3, int color4) {
        int averageColor = AndroidUtilities.getAverageColor(color1, color2);
        if (color3 != 0) {
            averageColor = AndroidUtilities.getAverageColor(averageColor, color3);
        }
        if (color4 != 0) {
            averageColor = AndroidUtilities.getAverageColor(averageColor, color4);
        }
        return AndroidUtilities.RGBtoHSB(Color.red(averageColor), Color.green(averageColor), Color.blue(averageColor))[2] < 0.3f;
    }

    public void setBounds(Rect bounds) {
        super.setBounds(bounds);
        this.patternBounds.set(bounds);
    }

    public void setPatternBounds(int left, int top, int right, int bottom) {
        this.patternBounds.set(left, top, right, bottom);
    }

    public static int getPatternColor(int color1, int color2, int color3, int color4) {
        if (isDark(color1, color2, color3, color4)) {
            return !useSoftLight ? Integer.MAX_VALUE : -1;
        }
        if (useSoftLight) {
            return -16777216;
        }
        int averageColor = AndroidUtilities.getAverageColor(color3, AndroidUtilities.getAverageColor(color1, color2));
        if (color4 != 0) {
            averageColor = AndroidUtilities.getAverageColor(color4, averageColor);
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

    public void setPostInvalidateParent(boolean value) {
        this.postInvalidateParent = value;
    }

    public void rotatePreview(boolean back) {
        if (this.posAnimationProgress >= 1.0f) {
            this.rotatingPreview = true;
            this.posAnimationProgress = 0.0f;
            this.rotationBack = back;
            invalidateParent();
        }
    }

    public void setPhase(int value) {
        this.phase = value;
        if (value < 0) {
            this.phase = 0;
        } else if (value > 7) {
            this.phase = 7;
        }
        Utilities.generateGradient(this.currentBitmap, true, this.phase, this.interpolator.getInterpolation(this.posAnimationProgress), this.currentBitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
    }

    public void switchToNextPosition() {
        switchToNextPosition(false);
    }

    public void switchToNextPosition(boolean fast) {
        if (this.posAnimationProgress >= 1.0f) {
            this.rotatingPreview = false;
            this.rotationBack = false;
            this.fastAnimation = fast;
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
        for (int i = 0; i < 3; i++) {
            Utilities.generateGradient(this.gradientToBitmap[i], true, this.phase, ((float) (i + 1)) / 3.0f, this.currentBitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
        }
    }

    public void switchToPrevPosition(boolean fast) {
        if (this.posAnimationProgress >= 1.0f) {
            this.rotatingPreview = false;
            this.fastAnimation = fast;
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

    public void setColors(int c1, int c2, int c3, int c4) {
        setColors(c1, c2, c3, c4, 0, true);
    }

    public void setColors(int c1, int c2, int c3, int c4, Bitmap bitmap) {
        int[] iArr = this.colors;
        iArr[0] = c1;
        iArr[1] = c2;
        iArr[2] = c3;
        iArr[3] = c4;
        Utilities.generateGradient(bitmap, true, this.phase, this.interpolator.getInterpolation(this.posAnimationProgress), this.currentBitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
    }

    public void setColors(int c1, int c2, int c3, int c4, int rotation, boolean invalidate) {
        int i = c1;
        int i2 = c2;
        int i3 = c3;
        int i4 = c4;
        if (this.isPreview && i3 == 0 && i4 == 0) {
            this.gradientDrawable = new GradientDrawable(BackgroundGradientDrawable.getGradientOrientation(rotation), new int[]{i, i2});
        } else {
            this.gradientDrawable = null;
        }
        int[] iArr = this.colors;
        if (iArr[0] != i || iArr[1] != i2 || iArr[2] != i3 || iArr[3] != i4) {
            iArr[0] = i;
            iArr[1] = i2;
            iArr[2] = i3;
            iArr[3] = i4;
            Bitmap bitmap = this.currentBitmap;
            if (bitmap != null) {
                Utilities.generateGradient(bitmap, true, this.phase, this.interpolator.getInterpolation(this.posAnimationProgress), this.currentBitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
                if (invalidate) {
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

    public void setTranslationY(int y) {
        this.translationY = y;
    }

    public void setPatternBitmap(int intensity2) {
        setPatternBitmap(intensity2, this.patternBitmap, true);
    }

    public void setPatternBitmap(int intensity2, Bitmap bitmap) {
        setPatternBitmap(intensity2, bitmap, true);
    }

    public void setPatternBitmap(int intensity2, Bitmap bitmap, boolean doNotScale) {
        this.intensity = intensity2;
        this.patternBitmap = bitmap;
        this.invalidateLegacy = true;
        if (bitmap != null) {
            if (useSoftLight) {
                if (intensity2 >= 0) {
                    this.paint2.setBlendMode(BlendMode.SOFT_LIGHT);
                } else {
                    this.paint2.setBlendMode((BlendMode) null);
                }
            }
            if (intensity2 < 0) {
                if (!useLegacyBitmap) {
                    this.bitmapShader = new BitmapShader(this.currentBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    this.gradientShader = new BitmapShader(this.patternBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                    this.disableGradientShaderScaling = doNotScale;
                    this.paint2.setShader(new ComposeShader(this.bitmapShader, this.gradientShader, PorterDuff.Mode.DST_IN));
                    this.paint2.setFilterBitmap(true);
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

    public void setPatternColorFilter(int color) {
        this.patternColorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        invalidateParent();
    }

    public void setPatternAlpha(float alpha2) {
        this.patternAlpha = alpha2;
        invalidateParent();
    }

    public void setBackgroundAlpha(float alpha2) {
        this.backgroundAlpha = alpha2;
        invalidateParent();
    }

    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        this.patternBounds.set(left, top, right, bottom);
        createLegacyBitmap();
    }

    private void createLegacyBitmap() {
        if (useLegacyBitmap && this.intensity < 0 && !errorWhileGenerateLegacyBitmap) {
            int w = (int) (((float) this.patternBounds.width()) * legacyBitmapScale);
            int h = (int) (((float) this.patternBounds.height()) * legacyBitmapScale);
            if (w > 0 && h > 0) {
                Bitmap bitmap = this.legacyBitmap;
                if (bitmap == null || bitmap.getWidth() != w || this.legacyBitmap.getHeight() != h) {
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
                        this.legacyBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
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
        Canvas canvas2 = canvas;
        Rect bounds = getBounds();
        canvas.save();
        float tr = (float) (this.patternBitmap != null ? bounds.top : this.translationY);
        int bitmapWidth = this.currentBitmap.getWidth();
        int bitmapHeight = this.currentBitmap.getHeight();
        float w = (float) bounds.width();
        float h = (float) bounds.height();
        float maxScale = Math.max(w / ((float) bitmapWidth), h / ((float) bitmapHeight));
        float width = ((float) bitmapWidth) * maxScale;
        float height = ((float) bitmapHeight) * maxScale;
        float x = (w - width) / 2.0f;
        float y = (h - height) / 2.0f;
        if (this.isPreview) {
            x += (float) bounds.left;
            y += (float) bounds.top;
            int i = bitmapWidth;
            canvas2.clipRect(bounds.left, bounds.top, bounds.right, bounds.bottom);
        }
        if (this.intensity < 0) {
            canvas2.drawColor(ColorUtils.setAlphaComponent(-16777216, (int) (((float) this.alpha) * this.backgroundAlpha)));
            int i2 = bitmapHeight;
            float f = w;
            float f2 = h;
        } else if (this.roundRadius != 0) {
            this.matrix.reset();
            this.matrix.setTranslate(x, y);
            float scaleW = ((float) this.currentBitmap.getWidth()) / ((float) bounds.width());
            float scale = 1.0f / Math.min(scaleW, ((float) this.currentBitmap.getHeight()) / ((float) bounds.height()));
            this.matrix.preScale(scale, scale);
            float f3 = scaleW;
            this.bitmapShader.setLocalMatrix(this.matrix);
            int i3 = bitmapHeight;
            float f4 = w;
            float f5 = h;
            this.rect.set((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom);
            int wasAlpha = this.paint.getAlpha();
            this.paint.setAlpha((int) (((float) wasAlpha) * this.backgroundAlpha));
            RectF rectF = this.rect;
            int i4 = this.roundRadius;
            canvas2.drawRoundRect(rectF, (float) i4, (float) i4, this.paint);
            this.paint.setAlpha(wasAlpha);
        } else {
            float f6 = w;
            float f7 = h;
            canvas2.translate(0.0f, tr);
            GradientDrawable gradientDrawable2 = this.gradientDrawable;
            if (gradientDrawable2 != null) {
                gradientDrawable2.setBounds((int) x, (int) y, (int) (x + width), (int) (y + height));
                this.gradientDrawable.setAlpha((int) (this.backgroundAlpha * 255.0f));
                this.gradientDrawable.draw(canvas2);
            } else {
                this.rect.set(x, y, x + width, y + height);
                Paint bitmapPaint = this.overrideBitmapPaint;
                if (bitmapPaint == null) {
                    bitmapPaint = this.paint;
                }
                int wasAlpha2 = bitmapPaint.getAlpha();
                bitmapPaint.setAlpha((int) (((float) wasAlpha2) * this.backgroundAlpha));
                canvas2.drawBitmap(this.currentBitmap, (Rect) null, this.rect, bitmapPaint);
                bitmapPaint.setAlpha(wasAlpha2);
            }
        }
        canvas.restore();
        updateAnimation(true);
    }

    public void drawPattern(Canvas canvas) {
        int bitmapWidth;
        float maxScale;
        int bitmapHeight;
        int bitmapWidth2;
        Canvas canvas2 = canvas;
        Rect bounds = getBounds();
        canvas.save();
        float tr = (float) (this.patternBitmap != null ? bounds.top : this.translationY);
        int bitmapWidth3 = this.currentBitmap.getWidth();
        int bitmapHeight2 = this.currentBitmap.getHeight();
        float w = (float) bounds.width();
        float h = (float) bounds.height();
        float maxScale2 = Math.max(w / ((float) bitmapWidth3), h / ((float) bitmapHeight2));
        float x = (w - (((float) bitmapWidth3) * maxScale2)) / 2.0f;
        float y = (h - (((float) bitmapHeight2) * maxScale2)) / 2.0f;
        if (this.isPreview) {
            x += (float) bounds.left;
            y += (float) bounds.top;
            bitmapWidth = bitmapWidth3;
            canvas2.clipRect(bounds.left, bounds.top, bounds.right, bounds.bottom);
        } else {
            bitmapWidth = bitmapWidth3;
        }
        if (this.intensity < 0) {
            Bitmap bitmap = this.patternBitmap;
            if (bitmap != null) {
                if (!useLegacyBitmap) {
                    float f = maxScale2;
                    if (this.matrix == null) {
                        this.matrix = new Matrix();
                    }
                    this.matrix.reset();
                    this.matrix.setTranslate(x, y + tr);
                    float scaleW = ((float) this.currentBitmap.getWidth()) / ((float) bounds.width());
                    float scaleH = ((float) this.currentBitmap.getHeight()) / ((float) bounds.height());
                    float scale = 1.0f / Math.min(scaleW, scaleH);
                    this.matrix.preScale(scale, scale);
                    this.bitmapShader.setLocalMatrix(this.matrix);
                    this.matrix.reset();
                    int bitmapWidth4 = this.patternBitmap.getWidth();
                    int bitmapHeight3 = this.patternBitmap.getHeight();
                    float f2 = scaleW;
                    float maxScale3 = Math.max(w / ((float) bitmapWidth4), h / ((float) bitmapHeight3));
                    float f3 = scaleH;
                    float f4 = scale;
                    this.matrix.setTranslate((float) ((int) ((w - (((float) bitmapWidth4) * maxScale3)) / 2.0f)), (float) ((int) (((h - (((float) bitmapHeight3) * maxScale3)) / 2.0f) + tr)));
                    if (!this.disableGradientShaderScaling || maxScale3 > 1.4f || maxScale3 < 0.8f) {
                        this.matrix.preScale(maxScale3, maxScale3);
                    }
                    this.gradientShader.setLocalMatrix(this.matrix);
                    this.paint2.setColorFilter((ColorFilter) null);
                    this.paint2.setAlpha((int) ((((float) Math.abs(this.intensity)) / 100.0f) * ((float) this.alpha) * this.patternAlpha));
                    float f5 = tr;
                    float maxScale4 = maxScale3;
                    this.rect.set((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom);
                    RectF rectF = this.rect;
                    int i = this.roundRadius;
                    canvas2.drawRoundRect(rectF, (float) i, (float) i, this.paint2);
                    int i2 = bitmapWidth4;
                    int i3 = bitmapHeight3;
                    float f6 = maxScale4;
                } else if (errorWhileGenerateLegacyBitmap) {
                    int bitmapWidth5 = bitmap.getWidth();
                    int bitmapHeight4 = this.patternBitmap.getHeight();
                    float maxScale5 = Math.max(w / ((float) bitmapWidth5), h / ((float) bitmapHeight4));
                    float width = ((float) bitmapWidth5) * maxScale5;
                    float height = ((float) bitmapHeight4) * maxScale5;
                    float x2 = (w - width) / 2.0f;
                    float y2 = (h - height) / 2.0f;
                    this.rect.set(x2, y2, x2 + width, y2 + height);
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
                    canvas2.translate(0.0f, tr);
                    canvas2.drawBitmap(this.patternBitmap, (Rect) null, this.rect, this.paint2);
                    float f7 = tr;
                    int i4 = bitmapWidth5;
                    int i5 = bitmapHeight4;
                } else {
                    Bitmap bitmap2 = this.legacyBitmap;
                    if (bitmap2 != null) {
                        if (this.invalidateLegacy) {
                            this.rect.set(0.0f, 0.0f, (float) bitmap2.getWidth(), (float) this.legacyBitmap.getHeight());
                            int oldAlpha = this.paint.getAlpha();
                            this.paint.setAlpha(255);
                            int i6 = bitmapHeight2;
                            float f8 = maxScale2;
                            this.legacyCanvas.drawBitmap(this.currentBitmap, (Rect) null, this.rect, this.paint);
                            this.paint.setAlpha(oldAlpha);
                            int bitmapWidth6 = this.patternBitmap.getWidth();
                            int bitmapHeight5 = this.patternBitmap.getHeight();
                            float maxScale6 = Math.max(w / ((float) bitmapWidth6), h / ((float) bitmapHeight5));
                            float width2 = ((float) bitmapWidth6) * maxScale6;
                            float height2 = ((float) bitmapHeight5) * maxScale6;
                            float x3 = (w - width2) / 2.0f;
                            float y3 = (h - height2) / 2.0f;
                            int i7 = oldAlpha;
                            this.rect.set(x3, y3, x3 + width2, y3 + height2);
                            this.paint2.setColorFilter((ColorFilter) null);
                            this.paint2.setAlpha((int) ((((float) Math.abs(this.intensity)) / 100.0f) * 255.0f));
                            this.legacyCanvas.save();
                            Canvas canvas3 = this.legacyCanvas;
                            float f9 = legacyBitmapScale;
                            canvas3.scale(f9, f9);
                            int bitmapHeight6 = bitmapHeight5;
                            this.legacyCanvas.drawBitmap(this.patternBitmap, (Rect) null, this.rect, this.paint2);
                            this.legacyCanvas.restore();
                            this.invalidateLegacy = false;
                            float var_ = maxScale6;
                            bitmapWidth2 = bitmapWidth6;
                            bitmapHeight2 = bitmapHeight6;
                        } else {
                            float var_ = maxScale2;
                            bitmapWidth2 = bitmapWidth;
                        }
                        int bitmapWidth7 = bitmapWidth2;
                        int bitmapHeight7 = bitmapHeight2;
                        this.rect.set((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom);
                        if (this.legacyBitmap2 != null) {
                            float var_ = this.posAnimationProgress;
                            if (var_ != 1.0f) {
                                this.paint.setAlpha((int) (((float) this.alpha) * this.patternAlpha * (1.0f - var_)));
                                canvas2.drawBitmap(this.legacyBitmap2, (Rect) null, this.rect, this.paint);
                                this.paint.setAlpha((int) (((float) this.alpha) * this.patternAlpha * this.posAnimationProgress));
                                canvas2.drawBitmap(this.legacyBitmap, (Rect) null, this.rect, this.paint);
                                this.paint.setAlpha(this.alpha);
                                float var_ = tr;
                                int i8 = bitmapWidth7;
                                int i9 = bitmapHeight7;
                            }
                        }
                        canvas2.drawBitmap(this.legacyBitmap, (Rect) null, this.rect, this.paint);
                        float var_ = tr;
                        int i82 = bitmapWidth7;
                        int i92 = bitmapHeight7;
                    } else {
                        bitmapHeight = bitmapHeight2;
                        maxScale = maxScale2;
                        float var_ = tr;
                    }
                }
                canvas.restore();
                updateAnimation(true);
            }
            bitmapHeight = bitmapHeight2;
            maxScale = maxScale2;
        } else {
            bitmapHeight = bitmapHeight2;
            maxScale = maxScale2;
            Bitmap bitmap3 = this.patternBitmap;
            if (bitmap3 != null) {
                int bitmapWidth8 = bitmap3.getWidth();
                int bitmapHeight8 = this.patternBitmap.getHeight();
                float maxScale7 = Math.max(w / ((float) bitmapWidth8), h / ((float) bitmapHeight8));
                float width3 = ((float) bitmapWidth8) * maxScale7;
                float height3 = ((float) bitmapHeight8) * maxScale7;
                float x4 = (w - width3) / 2.0f;
                float y4 = (h - height3) / 2.0f;
                this.rect.set(x4, y4, x4 + width3, y4 + height3);
                this.paint2.setColorFilter(this.patternColorFilter);
                this.paint2.setAlpha((int) ((((float) Math.abs(this.intensity)) / 100.0f) * ((float) this.alpha) * this.patternAlpha));
                canvas2.drawBitmap(this.patternBitmap, (Rect) null, this.rect, this.paint2);
                canvas.restore();
                updateAnimation(true);
            }
        }
        int i10 = bitmapHeight;
        float var_ = maxScale;
        canvas.restore();
        updateAnimation(true);
    }

    public void draw(Canvas canvas) {
        int bitmapWidth;
        float maxScale;
        int bitmapHeight;
        int bitmapWidth2;
        Canvas canvas2 = canvas;
        Rect bounds = getBounds();
        canvas.save();
        float tr = (float) (this.patternBitmap != null ? bounds.top : this.translationY);
        int bitmapWidth3 = this.currentBitmap.getWidth();
        int bitmapHeight2 = this.currentBitmap.getHeight();
        float w = (float) bounds.width();
        float h = (float) bounds.height();
        float maxScale2 = Math.max(w / ((float) bitmapWidth3), h / ((float) bitmapHeight2));
        float width = ((float) bitmapWidth3) * maxScale2;
        float height = ((float) bitmapHeight2) * maxScale2;
        float x = (w - width) / 2.0f;
        float y = (h - height) / 2.0f;
        if (this.isPreview) {
            x += (float) bounds.left;
            y += (float) bounds.top;
            bitmapWidth = bitmapWidth3;
            canvas2.clipRect(bounds.left, bounds.top, bounds.right, bounds.bottom);
        } else {
            bitmapWidth = bitmapWidth3;
        }
        if (this.intensity < 0) {
            canvas2.drawColor(ColorUtils.setAlphaComponent(-16777216, (int) (((float) this.alpha) * this.backgroundAlpha)));
            Bitmap bitmap = this.patternBitmap;
            if (bitmap != null) {
                if (!useLegacyBitmap) {
                    float f = maxScale2;
                    if (this.matrix == null) {
                        this.matrix = new Matrix();
                    }
                    this.matrix.reset();
                    this.matrix.setTranslate(x, y + tr);
                    float scaleW = ((float) this.currentBitmap.getWidth()) / ((float) bounds.width());
                    float scaleH = ((float) this.currentBitmap.getHeight()) / ((float) bounds.height());
                    float scale = 1.0f / Math.min(scaleW, scaleH);
                    this.matrix.preScale(scale, scale);
                    this.bitmapShader.setLocalMatrix(this.matrix);
                    this.matrix.reset();
                    int bitmapWidth4 = this.patternBitmap.getWidth();
                    int bitmapHeight3 = this.patternBitmap.getHeight();
                    float f2 = scaleW;
                    float maxScale3 = Math.max(w / ((float) bitmapWidth4), h / ((float) bitmapHeight3));
                    float width2 = ((float) bitmapWidth4) * maxScale3;
                    float f3 = scaleH;
                    float f4 = scale;
                    this.matrix.setTranslate((float) ((int) ((w - width2) / 2.0f)), (float) ((int) (((h - (((float) bitmapHeight3) * maxScale3)) / 2.0f) + tr)));
                    if (!this.disableGradientShaderScaling || maxScale3 > 1.4f || maxScale3 < 0.8f) {
                        this.matrix.preScale(maxScale3, maxScale3);
                    }
                    this.gradientShader.setLocalMatrix(this.matrix);
                    this.paint2.setColorFilter((ColorFilter) null);
                    this.paint2.setAlpha((int) ((((float) Math.abs(this.intensity)) / 100.0f) * ((float) this.alpha) * this.patternAlpha));
                    float maxScale4 = maxScale3;
                    this.rect.set((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom);
                    RectF rectF = this.rect;
                    int i = this.roundRadius;
                    canvas2.drawRoundRect(rectF, (float) i, (float) i, this.paint2);
                    Rect rect2 = bounds;
                    int i2 = bitmapWidth4;
                    int i3 = bitmapHeight3;
                    float f5 = maxScale4;
                    float f6 = width2;
                } else if (errorWhileGenerateLegacyBitmap) {
                    int bitmapWidth5 = bitmap.getWidth();
                    int bitmapHeight4 = this.patternBitmap.getHeight();
                    float maxScale5 = Math.max(w / ((float) bitmapWidth5), h / ((float) bitmapHeight4));
                    float width3 = ((float) bitmapWidth5) * maxScale5;
                    float height2 = ((float) bitmapHeight4) * maxScale5;
                    float x2 = (w - width3) / 2.0f;
                    float y2 = (h - height2) / 2.0f;
                    this.rect.set(x2, y2, x2 + width3, y2 + height2);
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
                    canvas2.translate(0.0f, tr);
                    canvas2.drawBitmap(this.patternBitmap, (Rect) null, this.rect, this.paint2);
                    int i4 = bitmapWidth5;
                    int i5 = bitmapHeight4;
                    Rect rect3 = bounds;
                } else {
                    Bitmap bitmap2 = this.legacyBitmap;
                    if (bitmap2 != null) {
                        if (this.invalidateLegacy) {
                            this.rect.set(0.0f, 0.0f, (float) bitmap2.getWidth(), (float) this.legacyBitmap.getHeight());
                            int oldAlpha = this.paint.getAlpha();
                            this.paint.setAlpha(255);
                            int i6 = bitmapHeight2;
                            float f7 = maxScale2;
                            this.legacyCanvas.drawBitmap(this.currentBitmap, (Rect) null, this.rect, this.paint);
                            this.paint.setAlpha(oldAlpha);
                            int bitmapWidth6 = this.patternBitmap.getWidth();
                            int bitmapHeight5 = this.patternBitmap.getHeight();
                            float maxScale6 = Math.max(w / ((float) bitmapWidth6), h / ((float) bitmapHeight5));
                            float width4 = ((float) bitmapWidth6) * maxScale6;
                            float height3 = ((float) bitmapHeight5) * maxScale6;
                            float x3 = (w - width4) / 2.0f;
                            float y3 = (h - height3) / 2.0f;
                            int i7 = oldAlpha;
                            this.rect.set(x3, y3, x3 + width4, y3 + height3);
                            this.paint2.setColorFilter((ColorFilter) null);
                            this.paint2.setAlpha((int) ((((float) Math.abs(this.intensity)) / 100.0f) * 255.0f));
                            this.legacyCanvas.save();
                            Canvas canvas3 = this.legacyCanvas;
                            float f8 = legacyBitmapScale;
                            canvas3.scale(f8, f8);
                            int bitmapHeight6 = bitmapHeight5;
                            this.legacyCanvas.drawBitmap(this.patternBitmap, (Rect) null, this.rect, this.paint2);
                            this.legacyCanvas.restore();
                            this.invalidateLegacy = false;
                            float f9 = maxScale6;
                            bitmapWidth2 = bitmapWidth6;
                            bitmapHeight2 = bitmapHeight6;
                        } else {
                            int i8 = bitmapHeight2;
                            float var_ = maxScale2;
                            bitmapWidth2 = bitmapWidth;
                        }
                        int bitmapWidth7 = bitmapWidth2;
                        int bitmapHeight7 = bitmapHeight2;
                        this.rect.set((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom);
                        if (this.legacyBitmap2 != null) {
                            float var_ = this.posAnimationProgress;
                            if (var_ != 1.0f) {
                                this.paint.setAlpha((int) (((float) this.alpha) * this.patternAlpha * (1.0f - var_)));
                                canvas2.drawBitmap(this.legacyBitmap2, (Rect) null, this.rect, this.paint);
                                this.paint.setAlpha((int) (((float) this.alpha) * this.patternAlpha * this.posAnimationProgress));
                                canvas2.drawBitmap(this.legacyBitmap, (Rect) null, this.rect, this.paint);
                                this.paint.setAlpha(this.alpha);
                                int i9 = bitmapWidth7;
                                int i10 = bitmapHeight7;
                                Rect rect4 = bounds;
                            }
                        }
                        canvas2.drawBitmap(this.legacyBitmap, (Rect) null, this.rect, this.paint);
                        int i92 = bitmapWidth7;
                        int i102 = bitmapHeight7;
                        Rect rect42 = bounds;
                    } else {
                        bitmapHeight = bitmapHeight2;
                        maxScale = maxScale2;
                        Rect rect5 = bounds;
                    }
                }
                canvas.restore();
                updateAnimation(true);
            }
            bitmapHeight = bitmapHeight2;
            maxScale = maxScale2;
            Rect rect6 = bounds;
        } else {
            bitmapHeight = bitmapHeight2;
            maxScale = maxScale2;
            if (this.roundRadius != 0) {
                this.matrix.reset();
                this.matrix.setTranslate(x, y);
                float scaleW2 = ((float) this.currentBitmap.getWidth()) / ((float) bounds.width());
                float scaleH2 = ((float) this.currentBitmap.getHeight()) / ((float) bounds.height());
                float scale2 = 1.0f / Math.min(scaleW2, scaleH2);
                this.matrix.preScale(scale2, scale2);
                this.bitmapShader.setLocalMatrix(this.matrix);
                float var_ = scaleW2;
                float var_ = scaleH2;
                this.rect.set((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom);
                RectF rectF2 = this.rect;
                int i11 = this.roundRadius;
                canvas2.drawRoundRect(rectF2, (float) i11, (float) i11, this.paint);
            } else {
                canvas2.translate(0.0f, tr);
                GradientDrawable gradientDrawable2 = this.gradientDrawable;
                if (gradientDrawable2 != null) {
                    gradientDrawable2.setBounds((int) x, (int) y, (int) (x + width), (int) (y + height));
                    this.gradientDrawable.setAlpha((int) (this.backgroundAlpha * 255.0f));
                    this.gradientDrawable.draw(canvas2);
                } else {
                    this.rect.set(x, y, x + width, y + height);
                    Paint bitmapPaint = this.overrideBitmapPaint;
                    if (bitmapPaint == null) {
                        bitmapPaint = this.paint;
                    }
                    int wasAlpha = bitmapPaint.getAlpha();
                    bitmapPaint.setAlpha((int) (((float) wasAlpha) * this.backgroundAlpha));
                    canvas2.drawBitmap(this.currentBitmap, (Rect) null, this.rect, bitmapPaint);
                    bitmapPaint.setAlpha(wasAlpha);
                }
            }
            Bitmap bitmap3 = this.patternBitmap;
            if (bitmap3 != null) {
                int bitmapWidth8 = bitmap3.getWidth();
                int bitmapHeight8 = this.patternBitmap.getHeight();
                float maxScale7 = Math.max(w / ((float) bitmapWidth8), h / ((float) bitmapHeight8));
                float width5 = ((float) bitmapWidth8) * maxScale7;
                float height4 = ((float) bitmapHeight8) * maxScale7;
                float x4 = (w - width5) / 2.0f;
                float y4 = (h - height4) / 2.0f;
                this.rect.set(x4, y4, x4 + width5, y4 + height4);
                this.paint2.setColorFilter(this.patternColorFilter);
                this.paint2.setAlpha((int) ((((float) Math.abs(this.intensity)) / 100.0f) * ((float) this.alpha) * this.patternAlpha));
                Rect rect7 = bounds;
                canvas2.drawBitmap(this.patternBitmap, (Rect) null, this.rect, this.paint2);
                canvas.restore();
                updateAnimation(true);
            }
        }
        int i12 = bitmapWidth;
        int i13 = bitmapHeight;
        float var_ = maxScale;
        canvas.restore();
        updateAnimation(true);
    }

    public void updateAnimation(boolean invalidate) {
        float progress;
        int stageBefore;
        long newTime = SystemClock.elapsedRealtime();
        long dt = newTime - this.lastUpdateTime;
        if (dt > 20) {
            dt = 17;
        }
        this.lastUpdateTime = newTime;
        if (dt > 1) {
            boolean z = this.isIndeterminateAnimation;
            if (z && this.posAnimationProgress == 1.0f) {
                this.posAnimationProgress = 0.0f;
            }
            float f = this.posAnimationProgress;
            if (f < 1.0f) {
                boolean isNeedGenerateGradient = this.postInvalidateParent || this.rotatingPreview;
                if (z) {
                    float f2 = f + (((float) dt) / 12000.0f);
                    this.posAnimationProgress = f2;
                    if (f2 >= 1.0f) {
                        this.posAnimationProgress = 0.0f;
                    }
                    float f3 = this.posAnimationProgress;
                    int i = (int) (f3 / 0.125f);
                    this.phase = i;
                    progress = 1.0f - ((f3 - (((float) i) * 0.125f)) / 0.125f);
                    isNeedGenerateGradient = true;
                } else if (this.rotatingPreview) {
                    float progressBefore = this.interpolator.getInterpolation(f);
                    if (progressBefore <= 0.25f) {
                        stageBefore = 0;
                    } else if (progressBefore <= 0.5f) {
                        stageBefore = 1;
                    } else if (progressBefore <= 0.75f) {
                        stageBefore = 2;
                    } else {
                        stageBefore = 3;
                    }
                    float f4 = this.posAnimationProgress + (((float) dt) / (this.rotationBack ? 1000.0f : 2000.0f));
                    this.posAnimationProgress = f4;
                    if (f4 > 1.0f) {
                        this.posAnimationProgress = 1.0f;
                    }
                    float progress2 = this.interpolator.getInterpolation(this.posAnimationProgress);
                    if ((stageBefore == 0 && progress2 > 0.25f) || ((stageBefore == 1 && progress2 > 0.5f) || (stageBefore == 2 && progress2 > 0.75f))) {
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
                    if (progress2 <= 0.25f) {
                        progress = progress2 / 0.25f;
                    } else if (progress2 <= 0.5f) {
                        progress = (progress2 - 0.25f) / 0.25f;
                    } else if (progress2 <= 0.75f) {
                        progress = (progress2 - 0.5f) / 0.25f;
                    } else {
                        progress = (progress2 - 0.75f) / 0.25f;
                    }
                    if (this.rotationBack) {
                        float f5 = progress;
                        progress = 1.0f - progress;
                        if (this.posAnimationProgress >= 1.0f) {
                            int i4 = this.phase + 1;
                            this.phase = i4;
                            if (i4 > 7) {
                                this.phase = 0;
                            }
                            progress = 1.0f;
                        }
                    }
                } else {
                    float f6 = f + (((float) dt) / (this.fastAnimation ? 300.0f : 500.0f));
                    this.posAnimationProgress = f6;
                    if (f6 > 1.0f) {
                        this.posAnimationProgress = 1.0f;
                    }
                    progress = this.interpolator.getInterpolation(this.posAnimationProgress);
                    if (this.rotationBack) {
                        progress = 1.0f - progress;
                        if (this.posAnimationProgress >= 1.0f) {
                            int i5 = this.phase + 1;
                            this.phase = i5;
                            if (i5 > 7) {
                                this.phase = 0;
                            }
                            progress = 1.0f;
                        }
                    }
                }
                if (isNeedGenerateGradient) {
                    Bitmap bitmap = this.currentBitmap;
                    Utilities.generateGradient(bitmap, true, this.phase, progress, bitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
                    this.invalidateLegacy = true;
                } else if (!useLegacyBitmap || this.intensity >= 0) {
                    if (progress != 1.0f) {
                        int i6 = (int) (progress / 0.33333334f);
                        if (i6 == 0) {
                            this.gradientCanvas.drawBitmap(this.gradientFromBitmap, 0.0f, 0.0f, (Paint) null);
                        } else {
                            this.gradientCanvas.drawBitmap(this.gradientToBitmap[i6 - 1], 0.0f, 0.0f, (Paint) null);
                        }
                        this.paint3.setAlpha((int) (255.0f * ((progress - (((float) i6) * 0.33333334f)) / 0.33333334f)));
                        this.gradientCanvas.drawBitmap(this.gradientToBitmap[i6], 0.0f, 0.0f, this.paint3);
                    } else {
                        this.gradientCanvas.drawBitmap(this.gradientToBitmap[2], 0.0f, 0.0f, this.paint3);
                    }
                }
                if (invalidate) {
                    invalidateParent();
                }
            }
        }
    }

    public void setAlpha(int alpha2) {
        this.alpha = alpha2;
        this.paint.setAlpha(alpha2);
        this.paint2.setAlpha(alpha2);
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public int getOpacity() {
        return -2;
    }

    public boolean isOneColor() {
        int[] iArr = this.colors;
        return iArr[0] == iArr[1] && iArr[0] == iArr[2] && iArr[0] == iArr[3];
    }

    public void setIndeterminateAnimation(boolean isIndeterminateAnimation2) {
        this.isIndeterminateAnimation = isIndeterminateAnimation2;
    }

    public void setOverrideBitmapPaint(Paint overrideBitmapPaint2) {
        this.overrideBitmapPaint = overrideBitmapPaint2;
    }
}
