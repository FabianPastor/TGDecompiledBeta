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
import java.lang.ref.WeakReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;

public class MotionBackgroundDrawable extends Drawable {
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
    private Bitmap patternBitmap;
    private Rect patternBounds = new Rect();
    private int phase;
    private float posAnimationProgress = 1.0f;
    private RectF rect = new RectF();
    private boolean rotatingPreview;
    private boolean rotationBack;
    private int roundRadius;
    private int translationY;

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

    /* JADX WARNING: Removed duplicated region for block: B:100:0x02e4  */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x0304  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(android.graphics.Canvas r24) {
        /*
            r23 = this;
            r0 = r23
            r1 = r24
            android.graphics.Rect r2 = r23.getBounds()
            r24.save()
            android.graphics.Bitmap r3 = r0.patternBitmap
            if (r3 == 0) goto L_0x0012
            int r3 = r2.top
            goto L_0x0014
        L_0x0012:
            int r3 = r0.translationY
        L_0x0014:
            float r3 = (float) r3
            android.graphics.Bitmap r4 = r0.currentBitmap
            int r4 = r4.getWidth()
            android.graphics.Bitmap r5 = r0.currentBitmap
            int r5 = r5.getHeight()
            int r6 = r2.width()
            float r6 = (float) r6
            int r7 = r2.height()
            float r7 = (float) r7
            float r4 = (float) r4
            float r8 = r6 / r4
            float r5 = (float) r5
            float r9 = r7 / r5
            float r8 = java.lang.Math.max(r8, r9)
            float r4 = r4 * r8
            float r5 = r5 * r8
            float r8 = r6 - r4
            r9 = 1073741824(0x40000000, float:2.0)
            float r8 = r8 / r9
            float r10 = r7 - r5
            float r10 = r10 / r9
            boolean r11 = r0.isPreview
            if (r11 == 0) goto L_0x0054
            int r11 = r2.left
            float r12 = (float) r11
            float r8 = r8 + r12
            int r12 = r2.top
            float r13 = (float) r12
            float r10 = r10 + r13
            int r13 = r2.right
            int r14 = r2.bottom
            r1.clipRect(r11, r12, r13, r14)
        L_0x0054:
            android.graphics.Bitmap r11 = r0.patternBitmap
            r12 = 0
            r13 = 0
            r14 = 1065353216(0x3var_, float:1.0)
            if (r11 == 0) goto L_0x015b
            int r11 = r0.intensity
            if (r11 >= 0) goto L_0x015b
            r4 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r1.drawColor(r4)
            android.graphics.Bitmap r4 = r0.legacyBitmap
            if (r4 == 0) goto L_0x00d1
            android.graphics.RectF r3 = r0.rect
            int r4 = r4.getWidth()
            float r4 = (float) r4
            android.graphics.Bitmap r5 = r0.legacyBitmap
            int r5 = r5.getHeight()
            float r5 = (float) r5
            r3.set(r13, r13, r4, r5)
            android.graphics.Canvas r3 = r0.legacyCanvas
            android.graphics.Bitmap r4 = r0.currentBitmap
            android.graphics.RectF r5 = r0.rect
            android.graphics.Paint r8 = r0.paint
            r3.drawBitmap(r4, r12, r5, r8)
            android.graphics.Bitmap r3 = r0.patternBitmap
            int r3 = r3.getWidth()
            android.graphics.Bitmap r4 = r0.patternBitmap
            int r4 = r4.getHeight()
            float r3 = (float) r3
            float r5 = r6 / r3
            float r4 = (float) r4
            float r8 = r7 / r4
            float r5 = java.lang.Math.max(r5, r8)
            float r3 = r3 * r5
            float r4 = r4 * r5
            float r6 = r6 - r3
            float r6 = r6 / r9
            float r7 = r7 - r4
            float r7 = r7 / r9
            android.graphics.RectF r5 = r0.rect
            float r3 = r3 + r6
            float r4 = r4 + r7
            r5.set(r6, r7, r3, r4)
            android.graphics.Canvas r3 = r0.legacyCanvas
            android.graphics.Bitmap r4 = r0.patternBitmap
            android.graphics.RectF r5 = r0.rect
            android.graphics.Paint r6 = r0.paint2
            r3.drawBitmap(r4, r12, r5, r6)
            android.graphics.RectF r3 = r0.rect
            int r4 = r2.left
            float r4 = (float) r4
            int r5 = r2.top
            float r5 = (float) r5
            int r6 = r2.right
            float r6 = (float) r6
            int r2 = r2.bottom
            float r2 = (float) r2
            r3.set(r4, r5, r6, r2)
            android.graphics.Bitmap r2 = r0.legacyBitmap
            android.graphics.RectF r3 = r0.rect
            android.graphics.Paint r4 = r0.paint
            r1.drawBitmap(r2, r12, r3, r4)
            goto L_0x01f5
        L_0x00d1:
            android.graphics.Matrix r4 = r0.matrix
            r4.reset()
            android.graphics.Matrix r4 = r0.matrix
            float r10 = r10 + r3
            r4.setTranslate(r8, r10)
            android.graphics.Bitmap r4 = r0.currentBitmap
            int r4 = r4.getWidth()
            float r4 = (float) r4
            int r5 = r2.width()
            float r5 = (float) r5
            float r4 = r4 / r5
            android.graphics.Bitmap r5 = r0.currentBitmap
            int r5 = r5.getHeight()
            float r5 = (float) r5
            int r8 = r2.height()
            float r8 = (float) r8
            float r5 = r5 / r8
            float r4 = java.lang.Math.min(r4, r5)
            float r4 = r14 / r4
            android.graphics.Matrix r5 = r0.matrix
            r5.preScale(r4, r4)
            android.graphics.BitmapShader r4 = r0.bitmapShader
            android.graphics.Matrix r5 = r0.matrix
            r4.setLocalMatrix(r5)
            android.graphics.Matrix r4 = r0.matrix
            r4.reset()
            android.graphics.Bitmap r4 = r0.patternBitmap
            int r4 = r4.getWidth()
            android.graphics.Bitmap r5 = r0.patternBitmap
            int r5 = r5.getHeight()
            float r4 = (float) r4
            float r8 = r6 / r4
            float r5 = (float) r5
            float r10 = r7 / r5
            float r8 = java.lang.Math.max(r8, r10)
            float r4 = r4 * r8
            float r5 = r5 * r8
            float r6 = r6 - r4
            float r6 = r6 / r9
            float r7 = r7 - r5
            float r7 = r7 / r9
            android.graphics.Matrix r4 = r0.matrix
            float r7 = r7 + r3
            r4.setTranslate(r6, r7)
            android.graphics.Matrix r3 = r0.matrix
            r3.preScale(r8, r8)
            android.graphics.BitmapShader r3 = r0.gradientShader
            android.graphics.Matrix r4 = r0.matrix
            r3.setLocalMatrix(r4)
            android.graphics.RectF r3 = r0.rect
            int r4 = r2.left
            float r4 = (float) r4
            int r5 = r2.top
            float r5 = (float) r5
            int r6 = r2.right
            float r6 = (float) r6
            int r2 = r2.bottom
            float r2 = (float) r2
            r3.set(r4, r5, r6, r2)
            android.graphics.RectF r2 = r0.rect
            int r3 = r0.roundRadius
            float r4 = (float) r3
            float r3 = (float) r3
            android.graphics.Paint r5 = r0.paint2
            r1.drawRoundRect(r2, r4, r3, r5)
            goto L_0x01f5
        L_0x015b:
            int r11 = r0.roundRadius
            if (r11 == 0) goto L_0x01b2
            android.graphics.Matrix r3 = r0.matrix
            r3.reset()
            android.graphics.Matrix r3 = r0.matrix
            r3.setTranslate(r8, r10)
            android.graphics.Bitmap r3 = r0.currentBitmap
            int r3 = r3.getWidth()
            float r3 = (float) r3
            int r4 = r2.width()
            float r4 = (float) r4
            float r3 = r3 / r4
            android.graphics.Bitmap r4 = r0.currentBitmap
            int r4 = r4.getHeight()
            float r4 = (float) r4
            int r5 = r2.height()
            float r5 = (float) r5
            float r4 = r4 / r5
            float r3 = java.lang.Math.min(r3, r4)
            float r3 = r14 / r3
            android.graphics.Matrix r4 = r0.matrix
            r4.preScale(r3, r3)
            android.graphics.BitmapShader r3 = r0.bitmapShader
            android.graphics.Matrix r4 = r0.matrix
            r3.setLocalMatrix(r4)
            android.graphics.RectF r3 = r0.rect
            int r4 = r2.left
            float r4 = (float) r4
            int r5 = r2.top
            float r5 = (float) r5
            int r8 = r2.right
            float r8 = (float) r8
            int r2 = r2.bottom
            float r2 = (float) r2
            r3.set(r4, r5, r8, r2)
            android.graphics.RectF r2 = r0.rect
            int r3 = r0.roundRadius
            float r4 = (float) r3
            float r3 = (float) r3
            android.graphics.Paint r5 = r0.paint
            r1.drawRoundRect(r2, r4, r3, r5)
            goto L_0x01c5
        L_0x01b2:
            r1.translate(r13, r3)
            android.graphics.RectF r2 = r0.rect
            float r4 = r4 + r8
            float r5 = r5 + r10
            r2.set(r8, r10, r4, r5)
            android.graphics.Bitmap r2 = r0.currentBitmap
            android.graphics.RectF r3 = r0.rect
            android.graphics.Paint r4 = r0.paint
            r1.drawBitmap(r2, r12, r3, r4)
        L_0x01c5:
            android.graphics.Bitmap r2 = r0.patternBitmap
            if (r2 == 0) goto L_0x01f5
            int r2 = r2.getWidth()
            android.graphics.Bitmap r3 = r0.patternBitmap
            int r3 = r3.getHeight()
            float r2 = (float) r2
            float r4 = r6 / r2
            float r3 = (float) r3
            float r5 = r7 / r3
            float r4 = java.lang.Math.max(r4, r5)
            float r2 = r2 * r4
            float r3 = r3 * r4
            float r6 = r6 - r2
            float r6 = r6 / r9
            float r7 = r7 - r3
            float r7 = r7 / r9
            android.graphics.RectF r4 = r0.rect
            float r2 = r2 + r6
            float r3 = r3 + r7
            r4.set(r6, r7, r2, r3)
            android.graphics.Bitmap r2 = r0.patternBitmap
            android.graphics.RectF r3 = r0.rect
            android.graphics.Paint r4 = r0.paint2
            r1.drawBitmap(r2, r12, r3, r4)
        L_0x01f5:
            r24.restore()
            long r1 = android.os.SystemClock.elapsedRealtime()
            long r3 = r0.lastUpdateTime
            long r3 = r1 - r3
            r5 = 20
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 <= 0) goto L_0x0208
            r3 = 17
        L_0x0208:
            r0.lastUpdateTime = r1
            float r1 = r0.posAnimationProgress
            int r2 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r2 >= 0) goto L_0x034e
            boolean r2 = r0.rotatingPreview
            r5 = 2
            r6 = 7
            r7 = 0
            r8 = 1
            if (r2 == 0) goto L_0x02aa
            org.telegram.ui.Components.CubicBezierInterpolator r2 = r0.interpolator
            float r1 = r2.getInterpolation(r1)
            r2 = 1061158912(0x3var_, float:0.75)
            r9 = 1056964608(0x3var_, float:0.5)
            r10 = 1048576000(0x3e800000, float:0.25)
            int r11 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r11 > 0) goto L_0x022a
            r1 = 0
            goto L_0x0237
        L_0x022a:
            int r11 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
            if (r11 > 0) goto L_0x0230
            r1 = 1
            goto L_0x0237
        L_0x0230:
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 > 0) goto L_0x0236
            r1 = 2
            goto L_0x0237
        L_0x0236:
            r1 = 3
        L_0x0237:
            float r11 = r0.posAnimationProgress
            float r3 = (float) r3
            boolean r4 = r0.rotationBack
            if (r4 == 0) goto L_0x0241
            r4 = 1148846080(0x447a0000, float:1000.0)
            goto L_0x0243
        L_0x0241:
            r4 = 1157234688(0x44fa0000, float:2000.0)
        L_0x0243:
            float r3 = r3 / r4
            float r11 = r11 + r3
            r0.posAnimationProgress = r11
            int r3 = (r11 > r14 ? 1 : (r11 == r14 ? 0 : -1))
            if (r3 <= 0) goto L_0x024d
            r0.posAnimationProgress = r14
        L_0x024d:
            org.telegram.ui.Components.CubicBezierInterpolator r3 = r0.interpolator
            float r4 = r0.posAnimationProgress
            float r3 = r3.getInterpolation(r4)
            if (r1 != 0) goto L_0x025b
            int r4 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
            if (r4 > 0) goto L_0x0267
        L_0x025b:
            if (r1 != r8) goto L_0x0261
            int r4 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r4 > 0) goto L_0x0267
        L_0x0261:
            if (r1 != r5) goto L_0x027e
            int r1 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
            if (r1 <= 0) goto L_0x027e
        L_0x0267:
            boolean r1 = r0.rotationBack
            if (r1 == 0) goto L_0x0275
            int r1 = r0.phase
            int r1 = r1 + r8
            r0.phase = r1
            if (r1 <= r6) goto L_0x027e
            r0.phase = r7
            goto L_0x027e
        L_0x0275:
            int r1 = r0.phase
            int r1 = r1 - r8
            r0.phase = r1
            if (r1 >= 0) goto L_0x027e
            r0.phase = r6
        L_0x027e:
            int r1 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
            if (r1 > 0) goto L_0x0284
        L_0x0282:
            float r3 = r3 / r10
            goto L_0x0292
        L_0x0284:
            int r1 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r1 > 0) goto L_0x028a
            float r3 = r3 - r10
            goto L_0x0282
        L_0x028a:
            int r1 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
            if (r1 > 0) goto L_0x0290
            float r3 = r3 - r9
            goto L_0x0282
        L_0x0290:
            float r3 = r3 - r2
            goto L_0x0282
        L_0x0292:
            boolean r1 = r0.rotationBack
            if (r1 == 0) goto L_0x02de
            float r3 = r14 - r3
            float r1 = r0.posAnimationProgress
            int r1 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r1 < 0) goto L_0x02de
            int r1 = r0.phase
            int r1 = r1 + r8
            r0.phase = r1
            if (r1 <= r6) goto L_0x02a7
            r0.phase = r7
        L_0x02a7:
            r3 = 1065353216(0x3var_, float:1.0)
            goto L_0x02de
        L_0x02aa:
            float r2 = (float) r3
            boolean r3 = r0.fastAnimation
            if (r3 == 0) goto L_0x02b2
            r3 = 1133903872(0x43960000, float:300.0)
            goto L_0x02b4
        L_0x02b2:
            r3 = 1140457472(0x43fa0000, float:500.0)
        L_0x02b4:
            float r2 = r2 / r3
            float r1 = r1 + r2
            r0.posAnimationProgress = r1
            int r1 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r1 <= 0) goto L_0x02be
            r0.posAnimationProgress = r14
        L_0x02be:
            org.telegram.ui.Components.CubicBezierInterpolator r1 = r0.interpolator
            float r2 = r0.posAnimationProgress
            float r3 = r1.getInterpolation(r2)
            boolean r1 = r0.rotationBack
            if (r1 == 0) goto L_0x02de
            float r3 = r14 - r3
            float r1 = r0.posAnimationProgress
            int r1 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r1 < 0) goto L_0x02de
            int r1 = r0.phase
            int r1 = r1 + r8
            r0.phase = r1
            if (r1 <= r6) goto L_0x02db
            r0.phase = r7
        L_0x02db:
            r18 = 1065353216(0x3var_, float:1.0)
            goto L_0x02e0
        L_0x02de:
            r18 = r3
        L_0x02e0:
            boolean r1 = r0.rotatingPreview
            if (r1 == 0) goto L_0x0304
            android.graphics.Bitmap r15 = r0.currentBitmap
            r16 = 1
            int r1 = r0.phase
            int r19 = r15.getWidth()
            android.graphics.Bitmap r2 = r0.currentBitmap
            int r20 = r2.getHeight()
            android.graphics.Bitmap r2 = r0.currentBitmap
            int r21 = r2.getRowBytes()
            int[] r2 = r0.colors
            r17 = r1
            r22 = r2
            org.telegram.messenger.Utilities.generateGradient(r15, r16, r17, r18, r19, r20, r21, r22)
            goto L_0x034b
        L_0x0304:
            int r1 = (r18 > r14 ? 1 : (r18 == r14 ? 0 : -1))
            if (r1 == 0) goto L_0x0340
            r1 = 1051372203(0x3eaaaaab, float:0.33333334)
            float r2 = r18 / r1
            int r2 = (int) r2
            if (r2 != 0) goto L_0x0318
            android.graphics.Canvas r3 = r0.gradientCanvas
            android.graphics.Bitmap r4 = r0.gradientFromBitmap
            r3.drawBitmap(r4, r13, r13, r12)
            goto L_0x0323
        L_0x0318:
            android.graphics.Canvas r3 = r0.gradientCanvas
            android.graphics.Bitmap[] r4 = r0.gradientToBitmap
            int r5 = r2 + -1
            r4 = r4[r5]
            r3.drawBitmap(r4, r13, r13, r12)
        L_0x0323:
            float r3 = (float) r2
            float r3 = r3 * r1
            float r18 = r18 - r3
            float r18 = r18 / r1
            android.graphics.Paint r1 = r0.paint3
            r3 = 1132396544(0x437var_, float:255.0)
            float r3 = r3 * r18
            int r3 = (int) r3
            r1.setAlpha(r3)
            android.graphics.Canvas r1 = r0.gradientCanvas
            android.graphics.Bitmap[] r3 = r0.gradientToBitmap
            r2 = r3[r2]
            android.graphics.Paint r3 = r0.paint3
            r1.drawBitmap(r2, r13, r13, r3)
            goto L_0x034b
        L_0x0340:
            android.graphics.Canvas r1 = r0.gradientCanvas
            android.graphics.Bitmap[] r2 = r0.gradientToBitmap
            r2 = r2[r5]
            android.graphics.Paint r3 = r0.paint3
            r1.drawBitmap(r2, r13, r13, r3)
        L_0x034b:
            r23.invalidateParent()
        L_0x034e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.MotionBackgroundDrawable.draw(android.graphics.Canvas):void");
    }

    public void setAlpha(int i) {
        this.paint.setAlpha(i);
        this.paint2.setAlpha(i);
    }
}
