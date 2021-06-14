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
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.view.View;
import java.lang.ref.WeakReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;

public class MotionBackgroundDrawable extends Drawable {
    private BitmapShader bitmapShader;
    private int[] colors = {-12423849, -531317, -7888252, -133430};
    private Bitmap currentBitmap;
    private BitmapShader gradientShader;
    private int intensity = 100;
    private CubicBezierInterpolator interpolator = new CubicBezierInterpolator(0.33d, 0.0d, 0.0d, 1.0d);
    private boolean isPreview;
    private long lastUpdateTime;
    private Bitmap legacyBitmap;
    private Canvas legacyCanvas;
    private Matrix matrix;
    private Paint paint = new Paint(2);
    private Paint paint2 = new Paint(2);
    private WeakReference<View> parentView;
    private Bitmap patternBitmap;
    private Rect patternBounds = new Rect();
    private int phase;
    private float posAnimationProgress = 1.0f;
    private RectF rect = new RectF();
    private boolean rotatingPreview;
    private int roundRadius;
    private int translationY;

    public int getOpacity() {
        return -2;
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public MotionBackgroundDrawable() {
        Bitmap createBitmap = Bitmap.createBitmap(60, 80, Bitmap.Config.ARGB_8888);
        this.currentBitmap = createBitmap;
        Utilities.generateGradient(createBitmap, true, this.phase, this.interpolator.getInterpolation(this.posAnimationProgress), this.currentBitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
        if (Build.VERSION.SDK_INT >= 29) {
            this.paint2.setBlendMode(BlendMode.SOFT_LIGHT);
        }
    }

    public MotionBackgroundDrawable(int i, int i2, int i3, int i4, boolean z) {
        int[] iArr = this.colors;
        iArr[0] = i;
        iArr[1] = i2;
        iArr[2] = i3;
        iArr[3] = i4;
        this.isPreview = z;
        if (Build.VERSION.SDK_INT >= 29) {
            this.paint2.setBlendMode(BlendMode.SOFT_LIGHT);
        }
        Bitmap createBitmap = Bitmap.createBitmap(60, 80, Bitmap.Config.ARGB_8888);
        this.currentBitmap = createBitmap;
        Utilities.generateGradient(createBitmap, true, this.phase, this.interpolator.getInterpolation(this.posAnimationProgress), this.currentBitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
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

    public Bitmap getBitmap() {
        return this.currentBitmap;
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
            return Build.VERSION.SDK_INT < 29 ? Integer.MAX_VALUE : -1;
        }
        if (Build.VERSION.SDK_INT >= 29) {
            return -16777216;
        }
        double[] rgbToHsv = AndroidUtilities.rgbToHsv(i);
        double[] rgbToHsv2 = AndroidUtilities.rgbToHsv(i2);
        double[] rgbToHsv3 = AndroidUtilities.rgbToHsv(i3);
        double[] rgbToHsv4 = i4 != 0 ? AndroidUtilities.rgbToHsv(i4) : null;
        double d = rgbToHsv[0] + rgbToHsv2[0] + rgbToHsv3[0];
        int i5 = 3;
        if (rgbToHsv4 != null) {
            d += rgbToHsv4[0];
            i5 = 4;
        }
        double d2 = (double) i5;
        Double.isNaN(d2);
        double d3 = d / d2;
        if (Math.abs(rgbToHsv[0] - d3) >= 0.19444444444444445d || Math.abs(rgbToHsv2[0] - d3) >= 0.19444444444444445d || Math.abs(rgbToHsv3[0] - d3) >= 0.19444444444444445d) {
            return NUM;
        }
        if (rgbToHsv4 != null && Math.abs(rgbToHsv4[0] - d3) >= 0.19444444444444445d) {
            return NUM;
        }
        int averageColor = AndroidUtilities.getAverageColor(i3, AndroidUtilities.getAverageColor(i, i2));
        if (i4 != 0) {
            averageColor = AndroidUtilities.getAverageColor(i4, averageColor);
        }
        return AndroidUtilities.getPatternColor(averageColor, true);
    }

    public int getPatternColor() {
        int[] iArr = this.colors;
        return getPatternColor(iArr[0], iArr[1], iArr[2], iArr[3]);
    }

    public int getPhase() {
        return this.phase;
    }

    public void rotatePreview() {
        if (this.posAnimationProgress >= 1.0f) {
            this.rotatingPreview = true;
            this.posAnimationProgress = 0.0f;
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
        if (this.posAnimationProgress >= 1.0f) {
            this.rotatingPreview = false;
            this.posAnimationProgress = 0.0f;
            int i = this.phase - 1;
            this.phase = i;
            if (i < 0) {
                this.phase = 7;
            }
            invalidateParent();
        }
    }

    public void setParentView(View view) {
        this.parentView = new WeakReference<>(view);
    }

    public void setColors(int i, int i2, int i3, int i4) {
        setColors(i, i2, i3, i4, true);
    }

    public void setColors(int i, int i2, int i3, int i4, boolean z) {
        int[] iArr = this.colors;
        iArr[0] = i;
        iArr[1] = i2;
        iArr[2] = i3;
        iArr[3] = i4;
        Utilities.generateGradient(this.currentBitmap, true, this.phase, this.interpolator.getInterpolation(this.posAnimationProgress), this.currentBitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
        if (z) {
            invalidateParent();
        }
    }

    private void invalidateParent() {
        WeakReference<View> weakReference = this.parentView;
        if (weakReference != null && weakReference.get() != null) {
            ((View) this.parentView.get()).invalidate();
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

    public void setPatternBitmap(int i, Bitmap bitmap) {
        this.intensity = i;
        this.patternBitmap = bitmap;
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 29) {
            if (i >= 0) {
                this.paint2.setBlendMode(BlendMode.SOFT_LIGHT);
            } else {
                this.paint2.setBlendMode((BlendMode) null);
            }
        }
        if (i >= 0) {
            return;
        }
        if (i2 >= 28) {
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
        this.paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    }

    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        this.patternBounds.set(i, i2, i3, i4);
        if (Build.VERSION.SDK_INT < 28 && this.intensity < 0) {
            int i5 = i3 - i;
            int i6 = i4 - i2;
            Bitmap bitmap = this.legacyBitmap;
            if (bitmap == null || bitmap.getWidth() != i5 || this.legacyBitmap.getHeight() != i6) {
                Bitmap bitmap2 = this.legacyBitmap;
                if (bitmap2 != null) {
                    bitmap2.recycle();
                }
                this.legacyBitmap = Bitmap.createBitmap(i5, i6, Bitmap.Config.ARGB_8888);
                this.legacyCanvas = new Canvas(this.legacyBitmap);
            }
        }
    }

    public void draw(Canvas canvas) {
        float f;
        Rect bounds = getBounds();
        canvas.save();
        float f2 = (float) (this.patternBitmap != null ? bounds.top : this.translationY);
        int width = this.currentBitmap.getWidth();
        int height = this.currentBitmap.getHeight();
        float width2 = (float) bounds.width();
        float height2 = (float) bounds.height();
        float f3 = (float) width;
        float f4 = (float) height;
        float max = Math.max(width2 / f3, height2 / f4);
        float f5 = f3 * max;
        float f6 = f4 * max;
        float f7 = (width2 - f5) / 2.0f;
        float f8 = (height2 - f6) / 2.0f;
        if (this.isPreview) {
            int i = bounds.left;
            f7 += (float) i;
            int i2 = bounds.top;
            f8 += (float) i2;
            canvas.clipRect(i, i2, bounds.right, bounds.bottom);
        }
        if (this.patternBitmap == null || this.intensity >= 0) {
            canvas.translate(0.0f, f2);
            if (this.roundRadius != 0) {
                this.matrix.reset();
                this.matrix.setTranslate(f7, f8);
                float min = 1.0f / Math.min(((float) this.currentBitmap.getWidth()) / ((float) bounds.width()), ((float) this.currentBitmap.getHeight()) / ((float) bounds.height()));
                this.matrix.preScale(min, min);
                this.bitmapShader.setLocalMatrix(this.matrix);
                this.rect.set((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom);
                RectF rectF = this.rect;
                int i3 = this.roundRadius;
                canvas.drawRoundRect(rectF, (float) i3, (float) i3, this.paint);
            } else {
                this.rect.set(f7, f8, f5 + f7, f6 + f8);
                canvas.drawBitmap(this.currentBitmap, (Rect) null, this.rect, this.paint);
            }
            Bitmap bitmap = this.patternBitmap;
            if (bitmap != null) {
                float width3 = (float) bitmap.getWidth();
                float height3 = (float) this.patternBitmap.getHeight();
                float max2 = Math.max(width2 / width3, height2 / height3);
                float f9 = width3 * max2;
                float var_ = height3 * max2;
                float var_ = (width2 - f9) / 2.0f;
                float var_ = (height2 - var_) / 2.0f;
                this.rect.set(var_, var_, f9 + var_, var_ + var_);
                canvas.drawBitmap(this.patternBitmap, (Rect) null, this.rect, this.paint2);
            }
        } else {
            canvas.drawColor(-16777216);
            Bitmap bitmap2 = this.legacyBitmap;
            if (bitmap2 != null) {
                this.rect.set(0.0f, 0.0f, (float) bitmap2.getWidth(), (float) this.legacyBitmap.getHeight());
                this.legacyCanvas.drawBitmap(this.currentBitmap, (Rect) null, this.rect, this.paint);
                float width4 = (float) this.patternBitmap.getWidth();
                float height4 = (float) this.patternBitmap.getHeight();
                float max3 = Math.max(width2 / width4, height2 / height4);
                float var_ = width4 * max3;
                float var_ = height4 * max3;
                float var_ = (width2 - var_) / 2.0f;
                float var_ = (height2 - var_) / 2.0f;
                this.rect.set(var_, var_, var_ + var_, var_ + var_);
                this.legacyCanvas.drawBitmap(this.patternBitmap, (Rect) null, this.rect, this.paint2);
                this.rect.set((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom);
                canvas.drawBitmap(this.legacyBitmap, (Rect) null, this.rect, this.paint);
            } else {
                this.matrix.reset();
                this.matrix.setTranslate(f7, f8 + f2);
                float min2 = 1.0f / Math.min(((float) this.currentBitmap.getWidth()) / ((float) bounds.width()), ((float) this.currentBitmap.getHeight()) / ((float) bounds.height()));
                this.matrix.preScale(min2, min2);
                this.bitmapShader.setLocalMatrix(this.matrix);
                this.matrix.reset();
                float width5 = (float) this.patternBitmap.getWidth();
                float height5 = (float) this.patternBitmap.getHeight();
                float max4 = Math.max(width2 / width5, height2 / height5);
                this.matrix.setTranslate((width2 - (width5 * max4)) / 2.0f, ((height2 - (height5 * max4)) / 2.0f) + f2);
                this.matrix.preScale(max4, max4);
                this.gradientShader.setLocalMatrix(this.matrix);
                this.rect.set((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom);
                RectF rectF2 = this.rect;
                int i4 = this.roundRadius;
                canvas.drawRoundRect(rectF2, (float) i4, (float) i4, this.paint2);
            }
        }
        canvas.restore();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long j = elapsedRealtime - this.lastUpdateTime;
        if (j > 20) {
            j = 17;
        }
        this.lastUpdateTime = elapsedRealtime;
        float var_ = this.posAnimationProgress;
        if (var_ < 1.0f) {
            if (this.rotatingPreview) {
                float interpolation = this.interpolator.getInterpolation(var_);
                char c = interpolation <= 0.25f ? 0 : interpolation <= 0.5f ? 1 : interpolation <= 0.75f ? (char) 2 : 3;
                float var_ = this.posAnimationProgress + (((float) j) / 2000.0f);
                this.posAnimationProgress = var_;
                if (var_ > 1.0f) {
                    this.posAnimationProgress = 1.0f;
                }
                float interpolation2 = this.interpolator.getInterpolation(this.posAnimationProgress);
                if ((c == 0 && interpolation2 > 0.25f) || ((c == 1 && interpolation2 > 0.5f) || (c == 2 && interpolation2 > 0.75f))) {
                    int i5 = this.phase - 1;
                    this.phase = i5;
                    if (i5 < 0) {
                        this.phase = 7;
                    }
                }
                if (interpolation2 > 0.25f) {
                    interpolation2 = interpolation2 <= 0.5f ? interpolation2 - 0.25f : interpolation2 <= 0.75f ? interpolation2 - 0.5f : interpolation2 - 0.75f;
                }
                f = interpolation2 / 0.25f;
            } else {
                float var_ = var_ + (((float) j) / 500.0f);
                this.posAnimationProgress = var_;
                if (var_ > 1.0f) {
                    this.posAnimationProgress = 1.0f;
                }
                f = this.interpolator.getInterpolation(this.posAnimationProgress);
            }
            Bitmap bitmap3 = this.currentBitmap;
            Utilities.generateGradient(bitmap3, true, this.phase, f, bitmap3.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
            invalidateParent();
        }
    }

    public void setAlpha(int i) {
        this.paint.setAlpha(i);
        this.paint2.setAlpha(i);
    }
}
