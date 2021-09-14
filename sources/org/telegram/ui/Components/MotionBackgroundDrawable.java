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
import android.view.View;
import androidx.core.graphics.ColorUtils;
import java.lang.ref.WeakReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;

public class MotionBackgroundDrawable extends Drawable {
    private int alpha = 255;
    private BitmapShader bitmapShader;
    private int[] colors = {-12423849, -531317, -7888252, -133430};
    private Bitmap currentBitmap;
    private boolean fastAnimation;
    private Canvas gradientCanvas;
    private Bitmap gradientFromBitmap;
    private Canvas gradientFromCanvas;
    private BitmapShader gradientShader;
    private Bitmap[] gradientToBitmap = new Bitmap[3];
    private int intensity = 100;
    private final CubicBezierInterpolator interpolator = new CubicBezierInterpolator(0.33d, 0.0d, 0.0d, 1.0d);
    private boolean isPreview;
    private long lastUpdateTime;
    private Bitmap legacyBitmap;
    private Canvas legacyCanvas;
    private Matrix matrix;
    private Paint paint = new Paint(2);
    private Paint paint2 = new Paint(2);
    private Paint paint3 = new Paint();
    private WeakReference<View> parentView;
    private float patternAlpha = 1.0f;
    private Bitmap patternBitmap;
    private Rect patternBounds = new Rect();
    private ColorFilter patternColorFilter;
    private float patternIntensity = 1.0f;
    private int phase;
    private float posAnimationProgress = 1.0f;
    private boolean postInvalidateParent;
    private RectF rect = new RectF();
    private boolean rotatingPreview;
    private boolean rotationBack;
    private int roundRadius;
    private int translationY;
    private Runnable updateAnimationRunnable = new MotionBackgroundDrawable$$ExternalSyntheticLambda0(this);

    public int getOpacity() {
        return -2;
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public MotionBackgroundDrawable() {
        init();
    }

    public MotionBackgroundDrawable(int i, int i2, int i3, int i4, boolean z) {
        int[] iArr = this.colors;
        iArr[0] = i;
        iArr[1] = i2;
        iArr[2] = i3;
        iArr[3] = i4;
        this.isPreview = z;
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
        if (Build.VERSION.SDK_INT >= 29) {
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
            return Build.VERSION.SDK_INT < 29 ? Integer.MAX_VALUE : -1;
        }
        if (Build.VERSION.SDK_INT >= 29) {
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
        setColors(i, i2, i3, i4, true);
    }

    public void setColors(int i, int i2, int i3, int i4, Bitmap bitmap) {
        int[] iArr = this.colors;
        iArr[0] = i;
        iArr[1] = i2;
        iArr[2] = i3;
        iArr[3] = i4;
        Utilities.generateGradient(bitmap, true, this.phase, this.interpolator.getInterpolation(this.posAnimationProgress), this.currentBitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
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
        invalidateSelf();
        WeakReference<View> weakReference = this.parentView;
        if (!(weakReference == null || weakReference.get() == null)) {
            ((View) this.parentView.get()).invalidate();
        }
        if (this.postInvalidateParent) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.invalidateMotionBackground, new Object[0]);
            updateAnimation();
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
        Bitmap bitmap = this.patternBitmap;
        if (bitmap != null) {
            setPatternBitmap(i, bitmap);
        } else {
            this.intensity = i;
        }
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

    public void setPatternIntensity(float f) {
        this.patternIntensity = f;
        invalidateParent();
    }

    public void setPatternAlpha(float f) {
        this.patternAlpha = f;
        invalidateParent();
    }

    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        this.patternBounds.set(i, i2, i3, i4);
        if (Build.VERSION.SDK_INT < 28 && this.intensity < 0) {
            int i5 = i3 - i;
            int i6 = i4 - i2;
            if (i5 > 0 && i6 > 0) {
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
    }

    public void draw(Canvas canvas) {
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
        if (this.patternBitmap == null || this.intensity >= 0) {
            if (this.roundRadius != 0) {
                this.matrix.reset();
                this.matrix.setTranslate(f6, f7);
                float min = 1.0f / Math.min(((float) this.currentBitmap.getWidth()) / ((float) bounds.width()), ((float) this.currentBitmap.getHeight()) / ((float) bounds.height()));
                this.matrix.preScale(min, min);
                this.bitmapShader.setLocalMatrix(this.matrix);
                this.rect.set((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom);
                RectF rectF = this.rect;
                int i3 = this.roundRadius;
                canvas.drawRoundRect(rectF, (float) i3, (float) i3, this.paint);
            } else {
                canvas.translate(0.0f, f);
                this.rect.set(f6, f7, f4 + f6, f5 + f7);
                canvas.drawBitmap(this.currentBitmap, (Rect) null, this.rect, this.paint);
            }
            Bitmap bitmap = this.patternBitmap;
            if (bitmap != null) {
                float width3 = (float) bitmap.getWidth();
                float height3 = (float) this.patternBitmap.getHeight();
                float max2 = Math.max(width2 / width3, height2 / height3);
                float f8 = width3 * max2;
                float f9 = height3 * max2;
                float var_ = (width2 - f8) / 2.0f;
                float var_ = (height2 - f9) / 2.0f;
                this.rect.set(var_, var_, f8 + var_, f9 + var_);
                int alpha2 = this.paint2.getAlpha();
                ColorFilter colorFilter = this.patternColorFilter;
                if (colorFilter != null) {
                    this.paint2.setColorFilter(colorFilter);
                }
                float var_ = this.patternIntensity;
                if (!(var_ == 1.0f && this.patternAlpha == 1.0f)) {
                    this.paint2.setAlpha((int) (var_ * this.patternAlpha * 255.0f));
                }
                canvas.drawBitmap(this.patternBitmap, (Rect) null, this.rect, this.paint2);
                if (this.paint2.getAlpha() != alpha2) {
                    this.paint2.setAlpha(alpha2);
                }
            }
        } else {
            canvas.drawColor(ColorUtils.setAlphaComponent(-16777216, this.alpha));
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
                this.matrix.setTranslate(f6, f7 + f);
                float min2 = 1.0f / Math.min(((float) this.currentBitmap.getWidth()) / ((float) bounds.width()), ((float) this.currentBitmap.getHeight()) / ((float) bounds.height()));
                this.matrix.preScale(min2, min2);
                this.bitmapShader.setLocalMatrix(this.matrix);
                this.matrix.reset();
                float width5 = (float) this.patternBitmap.getWidth();
                float height5 = (float) this.patternBitmap.getHeight();
                float max4 = Math.max(width2 / width5, height2 / height5);
                this.matrix.setTranslate((width2 - (width5 * max4)) / 2.0f, ((height2 - (height5 * max4)) / 2.0f) + f);
                this.matrix.preScale(max4, max4);
                this.gradientShader.setLocalMatrix(this.matrix);
                int alpha3 = this.paint2.getAlpha();
                float var_ = this.patternIntensity;
                if (!(var_ == 1.0f && this.patternAlpha == 1.0f)) {
                    this.paint2.setAlpha((int) (Math.abs(var_) * this.patternAlpha * 255.0f));
                }
                this.rect.set((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom);
                RectF rectF2 = this.rect;
                int i4 = this.roundRadius;
                canvas.drawRoundRect(rectF2, (float) i4, (float) i4, this.paint2);
                if (this.paint2.getAlpha() != alpha3) {
                    this.paint2.setAlpha(alpha3);
                }
            }
        }
        canvas.restore();
        updateAnimation();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x00fb  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0143  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateAnimation() {
        /*
            r17 = this;
            r0 = r17
            long r1 = android.os.SystemClock.elapsedRealtime()
            long r3 = r0.lastUpdateTime
            long r3 = r1 - r3
            r5 = 20
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 <= 0) goto L_0x0012
            r3 = 17
        L_0x0012:
            r0.lastUpdateTime = r1
            r1 = 1
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 > 0) goto L_0x001b
            return
        L_0x001b:
            float r1 = r0.posAnimationProgress
            r2 = 1065353216(0x3var_, float:1.0)
            int r5 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r5 >= 0) goto L_0x0162
            boolean r5 = r0.rotatingPreview
            r6 = 2
            r7 = 7
            r8 = 0
            r9 = 1
            if (r5 == 0) goto L_0x00bd
            org.telegram.ui.Components.CubicBezierInterpolator r5 = r0.interpolator
            float r1 = r5.getInterpolation(r1)
            r5 = 1061158912(0x3var_, float:0.75)
            r10 = 1056964608(0x3var_, float:0.5)
            r11 = 1048576000(0x3e800000, float:0.25)
            int r12 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r12 > 0) goto L_0x003d
            r1 = 0
            goto L_0x004a
        L_0x003d:
            int r12 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r12 > 0) goto L_0x0043
            r1 = 1
            goto L_0x004a
        L_0x0043:
            int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r1 > 0) goto L_0x0049
            r1 = 2
            goto L_0x004a
        L_0x0049:
            r1 = 3
        L_0x004a:
            float r12 = r0.posAnimationProgress
            float r3 = (float) r3
            boolean r4 = r0.rotationBack
            if (r4 == 0) goto L_0x0054
            r4 = 1148846080(0x447a0000, float:1000.0)
            goto L_0x0056
        L_0x0054:
            r4 = 1157234688(0x44fa0000, float:2000.0)
        L_0x0056:
            float r3 = r3 / r4
            float r12 = r12 + r3
            r0.posAnimationProgress = r12
            int r3 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1))
            if (r3 <= 0) goto L_0x0060
            r0.posAnimationProgress = r2
        L_0x0060:
            org.telegram.ui.Components.CubicBezierInterpolator r3 = r0.interpolator
            float r4 = r0.posAnimationProgress
            float r3 = r3.getInterpolation(r4)
            if (r1 != 0) goto L_0x006e
            int r4 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r4 > 0) goto L_0x007a
        L_0x006e:
            if (r1 != r9) goto L_0x0074
            int r4 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
            if (r4 > 0) goto L_0x007a
        L_0x0074:
            if (r1 != r6) goto L_0x0091
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x0091
        L_0x007a:
            boolean r1 = r0.rotationBack
            if (r1 == 0) goto L_0x0088
            int r1 = r0.phase
            int r1 = r1 + r9
            r0.phase = r1
            if (r1 <= r7) goto L_0x0091
            r0.phase = r8
            goto L_0x0091
        L_0x0088:
            int r1 = r0.phase
            int r1 = r1 - r9
            r0.phase = r1
            if (r1 >= 0) goto L_0x0091
            r0.phase = r7
        L_0x0091:
            int r1 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r1 > 0) goto L_0x0097
        L_0x0095:
            float r3 = r3 / r11
            goto L_0x00a5
        L_0x0097:
            int r1 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
            if (r1 > 0) goto L_0x009d
            float r3 = r3 - r11
            goto L_0x0095
        L_0x009d:
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 > 0) goto L_0x00a3
            float r3 = r3 - r10
            goto L_0x0095
        L_0x00a3:
            float r3 = r3 - r5
            goto L_0x0095
        L_0x00a5:
            boolean r1 = r0.rotationBack
            if (r1 == 0) goto L_0x00f1
            float r3 = r2 - r3
            float r1 = r0.posAnimationProgress
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 < 0) goto L_0x00f1
            int r1 = r0.phase
            int r1 = r1 + r9
            r0.phase = r1
            if (r1 <= r7) goto L_0x00ba
            r0.phase = r8
        L_0x00ba:
            r3 = 1065353216(0x3var_, float:1.0)
            goto L_0x00f1
        L_0x00bd:
            float r3 = (float) r3
            boolean r4 = r0.fastAnimation
            if (r4 == 0) goto L_0x00c5
            r4 = 1133903872(0x43960000, float:300.0)
            goto L_0x00c7
        L_0x00c5:
            r4 = 1140457472(0x43fa0000, float:500.0)
        L_0x00c7:
            float r3 = r3 / r4
            float r1 = r1 + r3
            r0.posAnimationProgress = r1
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 <= 0) goto L_0x00d1
            r0.posAnimationProgress = r2
        L_0x00d1:
            org.telegram.ui.Components.CubicBezierInterpolator r1 = r0.interpolator
            float r3 = r0.posAnimationProgress
            float r3 = r1.getInterpolation(r3)
            boolean r1 = r0.rotationBack
            if (r1 == 0) goto L_0x00f1
            float r3 = r2 - r3
            float r1 = r0.posAnimationProgress
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 < 0) goto L_0x00f1
            int r1 = r0.phase
            int r1 = r1 + r9
            r0.phase = r1
            if (r1 <= r7) goto L_0x00ee
            r0.phase = r8
        L_0x00ee:
            r12 = 1065353216(0x3var_, float:1.0)
            goto L_0x00f2
        L_0x00f1:
            r12 = r3
        L_0x00f2:
            boolean r1 = r0.postInvalidateParent
            if (r1 != 0) goto L_0x0143
            boolean r1 = r0.rotatingPreview
            if (r1 == 0) goto L_0x00fb
            goto L_0x0143
        L_0x00fb:
            r1 = 0
            int r2 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1))
            if (r2 == 0) goto L_0x0137
            r2 = 1051372203(0x3eaaaaab, float:0.33333334)
            float r3 = r12 / r2
            int r3 = (int) r3
            r4 = 0
            if (r3 != 0) goto L_0x0111
            android.graphics.Canvas r5 = r0.gradientCanvas
            android.graphics.Bitmap r6 = r0.gradientFromBitmap
            r5.drawBitmap(r6, r1, r1, r4)
            goto L_0x011c
        L_0x0111:
            android.graphics.Canvas r5 = r0.gradientCanvas
            android.graphics.Bitmap[] r6 = r0.gradientToBitmap
            int r7 = r3 + -1
            r6 = r6[r7]
            r5.drawBitmap(r6, r1, r1, r4)
        L_0x011c:
            float r4 = (float) r3
            float r4 = r4 * r2
            float r12 = r12 - r4
            float r12 = r12 / r2
            android.graphics.Paint r2 = r0.paint3
            r4 = 1132396544(0x437var_, float:255.0)
            float r12 = r12 * r4
            int r4 = (int) r12
            r2.setAlpha(r4)
            android.graphics.Canvas r2 = r0.gradientCanvas
            android.graphics.Bitmap[] r4 = r0.gradientToBitmap
            r3 = r4[r3]
            android.graphics.Paint r4 = r0.paint3
            r2.drawBitmap(r3, r1, r1, r4)
            goto L_0x015f
        L_0x0137:
            android.graphics.Canvas r2 = r0.gradientCanvas
            android.graphics.Bitmap[] r3 = r0.gradientToBitmap
            r3 = r3[r6]
            android.graphics.Paint r4 = r0.paint3
            r2.drawBitmap(r3, r1, r1, r4)
            goto L_0x015f
        L_0x0143:
            android.graphics.Bitmap r9 = r0.currentBitmap
            r10 = 1
            int r11 = r0.phase
            int r13 = r9.getWidth()
            android.graphics.Bitmap r1 = r0.currentBitmap
            int r14 = r1.getHeight()
            android.graphics.Bitmap r1 = r0.currentBitmap
            int r15 = r1.getRowBytes()
            int[] r1 = r0.colors
            r16 = r1
            org.telegram.messenger.Utilities.generateGradient(r9, r10, r11, r12, r13, r14, r15, r16)
        L_0x015f:
            r17.invalidateParent()
        L_0x0162:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.MotionBackgroundDrawable.updateAnimation():void");
    }

    public void setAlpha(int i) {
        this.alpha = i;
        this.paint.setAlpha(i);
        this.paint2.setAlpha(i);
    }
}
