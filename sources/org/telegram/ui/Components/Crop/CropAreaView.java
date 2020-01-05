package org.telegram.ui.Components.Crop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import androidx.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;

public class CropAreaView extends View {
    private Control activeControl;
    private RectF actualRect = new RectF();
    private Animator animator;
    private RectF bottomEdge = new RectF();
    private RectF bottomLeftCorner = new RectF();
    private float bottomPadding;
    private RectF bottomRightCorner = new RectF();
    private Bitmap circleBitmap;
    Paint dimPaint = new Paint();
    private boolean dimVisibile = true;
    private Paint eraserPaint;
    Paint framePaint;
    private boolean frameVisible = true;
    private boolean freeform = true;
    private Animator gridAnimator;
    private float gridProgress;
    private GridType gridType = GridType.NONE;
    Paint handlePaint;
    AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
    private boolean isDragging;
    private RectF leftEdge = new RectF();
    Paint linePaint;
    private AreaViewListener listener;
    private float lockAspectRatio;
    private float minWidth = ((float) AndroidUtilities.dp(32.0f));
    private GridType previousGridType;
    private int previousX;
    private int previousY;
    private RectF rightEdge = new RectF();
    Paint shadowPaint;
    private float sidePadding = ((float) AndroidUtilities.dp(16.0f));
    private RectF tempRect = new RectF();
    private RectF topEdge = new RectF();
    private RectF topLeftCorner = new RectF();
    private RectF topRightCorner = new RectF();

    /* renamed from: org.telegram.ui.Components.Crop.CropAreaView$3 */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control = new int[Control.values().length];

        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x0040 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x004b */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0056 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0035 */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Can't wrap try/catch for region: R(18:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|18) */
        /* JADX WARNING: Can't wrap try/catch for region: R(18:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|18) */
        /* JADX WARNING: Can't wrap try/catch for region: R(18:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|18) */
        /* JADX WARNING: Can't wrap try/catch for region: R(16:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|(3:15|16|18)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(16:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|(3:15|16|18)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(16:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|(3:15|16|18)) */
        static {
            /*
            r0 = org.telegram.ui.Components.Crop.CropAreaView.Control.values();
            r0 = r0.length;
            r0 = new int[r0];
            $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control = r0;
            r0 = $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control;	 Catch:{ NoSuchFieldError -> 0x0014 }
            r1 = org.telegram.ui.Components.Crop.CropAreaView.Control.TOP_LEFT;	 Catch:{ NoSuchFieldError -> 0x0014 }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x0014 }
            r2 = 1;
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x0014 }
        L_0x0014:
            r0 = $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control;	 Catch:{ NoSuchFieldError -> 0x001f }
            r1 = org.telegram.ui.Components.Crop.CropAreaView.Control.TOP_RIGHT;	 Catch:{ NoSuchFieldError -> 0x001f }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x001f }
            r2 = 2;
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x001f }
        L_0x001f:
            r0 = $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control;	 Catch:{ NoSuchFieldError -> 0x002a }
            r1 = org.telegram.ui.Components.Crop.CropAreaView.Control.BOTTOM_LEFT;	 Catch:{ NoSuchFieldError -> 0x002a }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x002a }
            r2 = 3;
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x002a }
        L_0x002a:
            r0 = $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control;	 Catch:{ NoSuchFieldError -> 0x0035 }
            r1 = org.telegram.ui.Components.Crop.CropAreaView.Control.BOTTOM_RIGHT;	 Catch:{ NoSuchFieldError -> 0x0035 }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x0035 }
            r2 = 4;
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x0035 }
        L_0x0035:
            r0 = $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control;	 Catch:{ NoSuchFieldError -> 0x0040 }
            r1 = org.telegram.ui.Components.Crop.CropAreaView.Control.TOP;	 Catch:{ NoSuchFieldError -> 0x0040 }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x0040 }
            r2 = 5;
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x0040 }
        L_0x0040:
            r0 = $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control;	 Catch:{ NoSuchFieldError -> 0x004b }
            r1 = org.telegram.ui.Components.Crop.CropAreaView.Control.LEFT;	 Catch:{ NoSuchFieldError -> 0x004b }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x004b }
            r2 = 6;
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x004b }
        L_0x004b:
            r0 = $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control;	 Catch:{ NoSuchFieldError -> 0x0056 }
            r1 = org.telegram.ui.Components.Crop.CropAreaView.Control.RIGHT;	 Catch:{ NoSuchFieldError -> 0x0056 }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x0056 }
            r2 = 7;
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x0056 }
        L_0x0056:
            r0 = $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control;	 Catch:{ NoSuchFieldError -> 0x0062 }
            r1 = org.telegram.ui.Components.Crop.CropAreaView.Control.BOTTOM;	 Catch:{ NoSuchFieldError -> 0x0062 }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x0062 }
            r2 = 8;
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x0062 }
        L_0x0062:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Crop.CropAreaView$AnonymousClass3.<clinit>():void");
        }
    }

    interface AreaViewListener {
        void onAreaChange();

        void onAreaChangeBegan();

        void onAreaChangeEnded();
    }

    private enum Control {
        NONE,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        TOP,
        LEFT,
        BOTTOM,
        RIGHT
    }

    enum GridType {
        NONE,
        MINOR,
        MAJOR
    }

    public CropAreaView(Context context) {
        super(context);
        this.dimPaint.setColor(-NUM);
        this.shadowPaint = new Paint();
        this.shadowPaint.setStyle(Style.FILL);
        this.shadowPaint.setColor(NUM);
        this.shadowPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.linePaint = new Paint();
        this.linePaint.setStyle(Style.FILL);
        this.linePaint.setColor(-1);
        this.linePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        this.handlePaint = new Paint();
        this.handlePaint.setStyle(Style.FILL);
        this.handlePaint.setColor(-1);
        this.framePaint = new Paint();
        this.framePaint.setStyle(Style.FILL);
        this.framePaint.setColor(-NUM);
        this.eraserPaint = new Paint(1);
        this.eraserPaint.setColor(0);
        this.eraserPaint.setStyle(Style.FILL);
        this.eraserPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
    }

    public boolean isDragging() {
        return this.isDragging;
    }

    public void setDimVisibility(boolean z) {
        this.dimVisibile = z;
    }

    public void setFrameVisibility(boolean z) {
        this.frameVisible = z;
    }

    public void setBottomPadding(float f) {
        this.bottomPadding = f;
    }

    public Interpolator getInterpolator() {
        return this.interpolator;
    }

    public void setListener(AreaViewListener areaViewListener) {
        this.listener = areaViewListener;
    }

    public void setBitmap(Bitmap bitmap, boolean z, boolean z2) {
        if (bitmap != null && !bitmap.isRecycled()) {
            float height;
            int width;
            this.freeform = z2;
            if (z) {
                height = (float) bitmap.getHeight();
                width = bitmap.getWidth();
            } else {
                height = (float) bitmap.getWidth();
                width = bitmap.getHeight();
            }
            height /= (float) width;
            if (!this.freeform) {
                this.lockAspectRatio = 1.0f;
                height = 1.0f;
            }
            setActualRect(height);
        }
    }

    public void setFreeform(boolean z) {
        this.freeform = z;
    }

    public void setActualRect(float f) {
        calculateRect(this.actualRect, f);
        updateTouchAreas();
        invalidate();
    }

    public void setActualRect(RectF rectF) {
        this.actualRect.set(rectF);
        updateTouchAreas();
        invalidate();
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.freeform) {
            Canvas canvas2;
            float f;
            int dp = AndroidUtilities.dp(2.0f);
            int dp2 = AndroidUtilities.dp(16.0f);
            int dp3 = AndroidUtilities.dp(3.0f);
            RectF rectF = this.actualRect;
            float f2 = rectF.left;
            int i = ((int) f2) - dp;
            float f3 = rectF.top;
            int i2 = ((int) f3) - dp;
            int i3 = dp * 2;
            int i4 = ((int) (rectF.right - f2)) + i3;
            int i5 = ((int) (rectF.bottom - f3)) + i3;
            if (this.dimVisibile) {
                f3 = (float) (i2 + dp);
                canvas2 = canvas;
                canvas2.drawRect(0.0f, 0.0f, (float) getWidth(), f3, this.dimPaint);
                Canvas canvas3 = canvas;
                f = f3;
                float f4 = (float) ((i2 + i5) - dp);
                canvas3.drawRect(0.0f, f, (float) (i + dp), f4, this.dimPaint);
                canvas3.drawRect((float) ((i + i4) - dp), f, (float) getWidth(), f4, this.dimPaint);
                canvas.drawRect(0.0f, f4, (float) getWidth(), (float) getHeight(), this.dimPaint);
            }
            if (this.frameVisible) {
                int i6;
                int i7;
                int i8;
                int i9;
                int i10;
                int i11;
                float f5;
                float f6;
                float f7;
                float f8;
                float f9;
                float var_;
                int i12 = dp3 - dp;
                i3 = dp3 * 2;
                int i13 = i4 - i3;
                i3 = i5 - i3;
                GridType gridType = this.gridType;
                if (gridType == GridType.NONE && this.gridProgress > 0.0f) {
                    gridType = this.previousGridType;
                }
                this.shadowPaint.setAlpha((int) (this.gridProgress * 26.0f));
                this.linePaint.setAlpha((int) (this.gridProgress * 178.0f));
                int i14 = 0;
                while (true) {
                    int i15 = 3;
                    if (i14 >= 3) {
                        break;
                    }
                    float var_;
                    if (gridType == GridType.MINOR) {
                        i6 = 1;
                        while (i6 < 4) {
                            if (i14 == 2 && i6 == i15) {
                                i7 = dp;
                                i8 = dp2;
                                i9 = i5;
                                i10 = i4;
                            } else {
                                i11 = i + dp3;
                                i8 = i13 / 3;
                                f5 = (float) ((i11 + ((i8 / 3) * i6)) + (i8 * i14));
                                i8 = dp2;
                                dp2 = i2 + dp3;
                                i9 = i5;
                                i7 = dp;
                                i10 = i4;
                                Canvas canvas4 = canvas;
                                f6 = f5;
                                f7 = (float) dp2;
                                f8 = f5;
                                float var_ = (float) (dp2 + i3);
                                canvas4.drawLine(f6, f7, f8, var_, this.shadowPaint);
                                canvas4.drawLine(f6, f7, f8, var_, this.linePaint);
                                i5 = i3 / 3;
                                var_ = (float) ((dp2 + ((i5 / 3) * i6)) + (i5 * i14));
                                f6 = (float) i11;
                                f7 = var_;
                                f8 = (float) (i11 + i13);
                                var_ = var_;
                                canvas4.drawLine(f6, f7, f8, var_, this.shadowPaint);
                                canvas4.drawLine(f6, f7, f8, var_, this.linePaint);
                            }
                            i6++;
                            dp2 = i8;
                            i5 = i9;
                            dp = i7;
                            i4 = i10;
                            i15 = 3;
                        }
                        i7 = dp;
                        i8 = dp2;
                        i9 = i5;
                        i10 = i4;
                    } else {
                        i7 = dp;
                        i8 = dp2;
                        i9 = i5;
                        i10 = i4;
                        if (gridType == GridType.MAJOR && i14 > 0) {
                            dp = i + dp3;
                            var_ = (float) (((i13 / 3) * i14) + dp);
                            i5 = i2 + dp3;
                            Canvas canvas5 = canvas;
                            f9 = var_;
                            f6 = (float) i5;
                            f7 = var_;
                            f8 = (float) (i5 + i3);
                            canvas5.drawLine(f9, f6, f7, f8, this.shadowPaint);
                            canvas5.drawLine(f9, f6, f7, f8, this.linePaint);
                            var_ = (float) dp;
                            var_ = (float) (i5 + ((i3 / 3) * i14));
                            f9 = var_;
                            f6 = var_;
                            f7 = (float) (dp + i13);
                            f8 = var_;
                            canvas5.drawLine(f9, f6, f7, f8, this.shadowPaint);
                            canvas5.drawLine(f9, f6, f7, f8, this.linePaint);
                        }
                    }
                    i14++;
                    dp2 = i8;
                    i5 = i9;
                    dp = i7;
                    i4 = i10;
                }
                i7 = dp;
                i8 = dp2;
                i9 = i5;
                i10 = i4;
                dp = i + i12;
                i5 = i2 + i12;
                f2 = (float) i5;
                i11 = i + i10;
                i6 = i11 - i12;
                f5 = (float) i6;
                var_ = (float) (i5 + i7);
                canvas2 = canvas;
                float var_ = (float) dp;
                float var_ = f2;
                float var_ = f5;
                f9 = f5;
                f5 = var_;
                i5 = i6;
                canvas2.drawRect(var_, var_, var_, f5, this.framePaint);
                var_ = (float) (dp + i7);
                dp = i2 + i9;
                i12 = dp - i12;
                float var_ = (float) i12;
                float var_ = var_;
                canvas2.drawRect(var_, var_, var_, var_, this.framePaint);
                var_ = f9;
                f5 = var_;
                canvas2.drawRect(var_, (float) (i12 - i7), var_, f5, this.framePaint);
                canvas2.drawRect((float) (i5 - i7), f2, var_, f5, this.framePaint);
                var_ = (float) i2;
                f2 = (float) (i + i8);
                f5 = (float) (i2 + dp3);
                Canvas canvas6 = canvas;
                f7 = (float) i;
                f8 = var_;
                canvas6.drawRect(f7, f8, f2, f5, this.handlePaint);
                float var_ = (float) (i + dp3);
                f3 = (float) (i2 + i8);
                canvas6.drawRect(f7, f8, var_, f3, this.handlePaint);
                float var_ = (float) (i11 - i8);
                var_ = (float) i11;
                var_ = var_;
                float var_ = var_;
                canvas2.drawRect(var_, var_, var_, f5, this.handlePaint);
                f = (float) (i11 - dp3);
                canvas2.drawRect(f, var_, var_, f3, this.handlePaint);
                float var_ = (float) (dp - dp3);
                var_ = (float) dp;
                float var_ = var_;
                canvas6.drawRect(f7, var_, f2, var_, this.handlePaint);
                float var_ = (float) (dp - i8);
                canvas6.drawRect(f7, var_, var_, var_, this.handlePaint);
                Canvas canvas7 = canvas;
                f6 = var_;
                f7 = var_;
                canvas7.drawRect(var_, var_, f6, f7, this.handlePaint);
                canvas7.drawRect(f, var_, f6, f7, this.handlePaint);
            } else {
                return;
            }
        }
        Bitmap bitmap = this.circleBitmap;
        if (bitmap == null || ((float) bitmap.getWidth()) != this.actualRect.width()) {
            bitmap = this.circleBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.circleBitmap = null;
            }
            try {
                this.circleBitmap = Bitmap.createBitmap((int) this.actualRect.width(), (int) this.actualRect.height(), Config.ARGB_8888);
                Canvas canvas8 = new Canvas(this.circleBitmap);
                canvas8.drawRect(0.0f, 0.0f, this.actualRect.width(), this.actualRect.height(), this.dimPaint);
                canvas8.drawCircle(this.actualRect.width() / 2.0f, this.actualRect.height() / 2.0f, this.actualRect.width() / 2.0f, this.eraserPaint);
                canvas8.setBitmap(null);
            } catch (Throwable unused) {
            }
        }
        canvas.drawRect(0.0f, 0.0f, (float) getWidth(), (float) ((int) this.actualRect.top), this.dimPaint);
        RectF rectF2 = this.actualRect;
        canvas.drawRect(0.0f, (float) ((int) rectF2.top), (float) ((int) rectF2.left), (float) ((int) rectF2.bottom), this.dimPaint);
        rectF2 = this.actualRect;
        canvas.drawRect((float) ((int) rectF2.right), (float) ((int) rectF2.top), (float) getWidth(), (float) ((int) this.actualRect.bottom), this.dimPaint);
        canvas.drawRect(0.0f, (float) ((int) this.actualRect.bottom), (float) getWidth(), (float) getHeight(), this.dimPaint);
        bitmap = this.circleBitmap;
        if (bitmap != null) {
            RectF rectF3 = this.actualRect;
            canvas.drawBitmap(bitmap, (float) ((int) rectF3.left), (float) ((int) rectF3.top), null);
        }
    }

    private void updateTouchAreas() {
        int dp = AndroidUtilities.dp(16.0f);
        RectF rectF = this.topLeftCorner;
        RectF rectF2 = this.actualRect;
        float f = rectF2.left;
        float f2 = (float) dp;
        float f3 = f - f2;
        float f4 = rectF2.top;
        rectF.set(f3, f4 - f2, f + f2, f4 + f2);
        rectF = this.topRightCorner;
        rectF2 = this.actualRect;
        f = rectF2.right;
        f3 = f - f2;
        f4 = rectF2.top;
        rectF.set(f3, f4 - f2, f + f2, f4 + f2);
        rectF = this.bottomLeftCorner;
        rectF2 = this.actualRect;
        f = rectF2.left;
        f3 = f - f2;
        f4 = rectF2.bottom;
        rectF.set(f3, f4 - f2, f + f2, f4 + f2);
        rectF = this.bottomRightCorner;
        rectF2 = this.actualRect;
        f = rectF2.right;
        f3 = f - f2;
        f4 = rectF2.bottom;
        rectF.set(f3, f4 - f2, f + f2, f4 + f2);
        rectF = this.topEdge;
        rectF2 = this.actualRect;
        f = rectF2.left + f2;
        f3 = rectF2.top;
        rectF.set(f, f3 - f2, rectF2.right - f2, f3 + f2);
        rectF = this.leftEdge;
        rectF2 = this.actualRect;
        f = rectF2.left;
        rectF.set(f - f2, rectF2.top + f2, f + f2, rectF2.bottom - f2);
        rectF = this.rightEdge;
        rectF2 = this.actualRect;
        f = rectF2.right;
        rectF.set(f - f2, rectF2.top + f2, f + f2, rectF2.bottom - f2);
        rectF = this.bottomEdge;
        rectF2 = this.actualRect;
        f = rectF2.left + f2;
        f3 = rectF2.bottom;
        rectF.set(f, f3 - f2, rectF2.right - f2, f3 + f2);
    }

    public float getLockAspectRatio() {
        return this.lockAspectRatio;
    }

    public void setLockedAspectRatio(float f) {
        this.lockAspectRatio = f;
    }

    public void setGridType(GridType gridType, boolean z) {
        if (!(this.gridAnimator == null || (z && this.gridType == gridType))) {
            this.gridAnimator.cancel();
            this.gridAnimator = null;
        }
        GridType gridType2 = this.gridType;
        if (gridType2 != gridType) {
            this.previousGridType = gridType2;
            this.gridType = gridType;
            float f = gridType == GridType.NONE ? 0.0f : 1.0f;
            if (z) {
                this.gridAnimator = ObjectAnimator.ofFloat(this, "gridProgress", new float[]{this.gridProgress, f});
                this.gridAnimator.setDuration(200);
                this.gridAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        CropAreaView.this.gridAnimator = null;
                    }
                });
                if (gridType == GridType.NONE) {
                    this.gridAnimator.setStartDelay(200);
                }
                this.gridAnimator.start();
            } else {
                this.gridProgress = f;
                invalidate();
            }
        }
    }

    @Keep
    private void setGridProgress(float f) {
        this.gridProgress = f;
        invalidate();
    }

    private float getGridProgress() {
        return this.gridProgress;
    }

    public float getAspectRatio() {
        RectF rectF = this.actualRect;
        return (rectF.right - rectF.left) / (rectF.bottom - rectF.top);
    }

    public void fill(final RectF rectF, Animator animator, boolean z) {
        if (z) {
            Animator animator2 = this.animator;
            if (animator2 != null) {
                animator2.cancel();
                this.animator = null;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            this.animator = animatorSet;
            animatorSet.setDuration(300);
            Animator[] animatorArr = new Animator[5];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "cropLeft", new float[]{rectF.left});
            animatorArr[0].setInterpolator(this.interpolator);
            animatorArr[1] = ObjectAnimator.ofFloat(this, "cropTop", new float[]{rectF.top});
            animatorArr[1].setInterpolator(this.interpolator);
            animatorArr[2] = ObjectAnimator.ofFloat(this, "cropRight", new float[]{rectF.right});
            animatorArr[2].setInterpolator(this.interpolator);
            animatorArr[3] = ObjectAnimator.ofFloat(this, "cropBottom", new float[]{rectF.bottom});
            animatorArr[3].setInterpolator(this.interpolator);
            animatorArr[4] = animator;
            animatorArr[4].setInterpolator(this.interpolator);
            animatorSet.playTogether(animatorArr);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    CropAreaView.this.setActualRect(rectF);
                    CropAreaView.this.animator = null;
                }
            });
            animatorSet.start();
            return;
        }
        setActualRect(rectF);
    }

    public void resetAnimator() {
        Animator animator = this.animator;
        if (animator != null) {
            animator.cancel();
            this.animator = null;
        }
    }

    @Keep
    private void setCropLeft(float f) {
        this.actualRect.left = f;
        invalidate();
    }

    public float getCropLeft() {
        return this.actualRect.left;
    }

    @Keep
    private void setCropTop(float f) {
        this.actualRect.top = f;
        invalidate();
    }

    public float getCropTop() {
        return this.actualRect.top;
    }

    @Keep
    private void setCropRight(float f) {
        this.actualRect.right = f;
        invalidate();
    }

    public float getCropRight() {
        return this.actualRect.right;
    }

    @Keep
    private void setCropBottom(float f) {
        this.actualRect.bottom = f;
        invalidate();
    }

    public float getCropBottom() {
        return this.actualRect.bottom;
    }

    public float getCropCenterX() {
        RectF rectF = this.actualRect;
        float f = rectF.left;
        return f + ((rectF.right - f) / 2.0f);
    }

    public float getCropCenterY() {
        RectF rectF = this.actualRect;
        float f = rectF.top;
        return f + ((rectF.bottom - f) / 2.0f);
    }

    public float getCropWidth() {
        RectF rectF = this.actualRect;
        return rectF.right - rectF.left;
    }

    public float getCropHeight() {
        RectF rectF = this.actualRect;
        return rectF.bottom - rectF.top;
    }

    public RectF getTargetRectToFill() {
        RectF rectF = new RectF();
        calculateRect(rectF, getAspectRatio());
        return rectF;
    }

    public void calculateRect(RectF rectF, float f) {
        float f2 = (float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        float measuredHeight = (((float) getMeasuredHeight()) - this.bottomPadding) - f2;
        float measuredWidth = ((float) getMeasuredWidth()) / measuredHeight;
        float min = Math.min((float) getMeasuredWidth(), measuredHeight) - (this.sidePadding * 2.0f);
        float measuredWidth2 = (float) getMeasuredWidth();
        float f3 = this.sidePadding;
        measuredWidth2 -= f3 * 2.0f;
        f3 = measuredHeight - (f3 * 2.0f);
        float measuredWidth3 = ((float) getMeasuredWidth()) / 2.0f;
        f2 += measuredHeight / 2.0f;
        if (((double) Math.abs(1.0f - f)) < 1.0E-4d) {
            min /= 2.0f;
            f = measuredWidth3 - min;
            measuredHeight = f2 - min;
            measuredWidth3 += min;
            f2 += min;
        } else if (f > measuredWidth) {
            measuredHeight = measuredWidth2 / 2.0f;
            measuredWidth = measuredWidth3 - measuredHeight;
            measuredWidth2 = (measuredWidth2 / f) / 2.0f;
            f = f2 - measuredWidth2;
            measuredWidth3 += measuredHeight;
            f2 += measuredWidth2;
            measuredHeight = f;
            f = measuredWidth;
        } else {
            f = (f * f3) / 2.0f;
            measuredHeight = measuredWidth3 - f;
            f3 /= 2.0f;
            measuredWidth = f2 - f3;
            measuredWidth3 += f;
            f2 += f3;
            f = measuredHeight;
            measuredHeight = measuredWidth;
        }
        rectF.set(f, measuredHeight, measuredWidth3, f2);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int x = (int) (motionEvent.getX() - ((ViewGroup) getParent()).getX());
        int y = (int) (motionEvent.getY() - ((ViewGroup) getParent()).getY());
        float f = (float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        int actionMasked = motionEvent.getActionMasked();
        float f2;
        AreaViewListener areaViewListener;
        if (actionMasked == 0) {
            if (this.freeform) {
                f = (float) x;
                f2 = (float) y;
                if (this.topLeftCorner.contains(f, f2)) {
                    this.activeControl = Control.TOP_LEFT;
                } else if (this.topRightCorner.contains(f, f2)) {
                    this.activeControl = Control.TOP_RIGHT;
                } else if (this.bottomLeftCorner.contains(f, f2)) {
                    this.activeControl = Control.BOTTOM_LEFT;
                } else if (this.bottomRightCorner.contains(f, f2)) {
                    this.activeControl = Control.BOTTOM_RIGHT;
                } else if (this.leftEdge.contains(f, f2)) {
                    this.activeControl = Control.LEFT;
                } else if (this.topEdge.contains(f, f2)) {
                    this.activeControl = Control.TOP;
                } else if (this.rightEdge.contains(f, f2)) {
                    this.activeControl = Control.RIGHT;
                } else if (this.bottomEdge.contains(f, f2)) {
                    this.activeControl = Control.BOTTOM;
                } else {
                    this.activeControl = Control.NONE;
                    return false;
                }
                this.previousX = x;
                this.previousY = y;
                setGridType(GridType.MAJOR, false);
                this.isDragging = true;
                areaViewListener = this.listener;
                if (areaViewListener != null) {
                    areaViewListener.onAreaChangeBegan();
                }
                return true;
            }
            this.activeControl = Control.NONE;
            return false;
        } else if (actionMasked == 1 || actionMasked == 3) {
            this.isDragging = false;
            Control control = this.activeControl;
            Control control2 = Control.NONE;
            if (control == control2) {
                return false;
            }
            this.activeControl = control2;
            areaViewListener = this.listener;
            if (areaViewListener != null) {
                areaViewListener.onAreaChangeEnded();
            }
            return true;
        } else if (actionMasked != 2 || this.activeControl == Control.NONE) {
            return false;
        } else {
            float width;
            RectF rectF;
            this.tempRect.set(this.actualRect);
            float f3 = (float) (x - this.previousX);
            float f4 = (float) (y - this.previousY);
            this.previousX = x;
            this.previousY = y;
            RectF rectF2;
            switch (AnonymousClass3.$SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[this.activeControl.ordinal()]) {
                case 1:
                    rectF2 = this.tempRect;
                    rectF2.left += f3;
                    rectF2.top += f4;
                    if (this.lockAspectRatio > 0.0f) {
                        width = rectF2.width();
                        f2 = this.tempRect.height();
                        if (Math.abs(f3) > Math.abs(f4)) {
                            constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                        } else {
                            constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                        }
                        rectF = this.tempRect;
                        rectF.left -= rectF.width() - width;
                        rectF = this.tempRect;
                        rectF.top -= rectF.width() - f2;
                        break;
                    }
                    break;
                case 2:
                    rectF2 = this.tempRect;
                    rectF2.right += f3;
                    rectF2.top += f4;
                    if (this.lockAspectRatio > 0.0f) {
                        width = rectF2.height();
                        if (Math.abs(f3) > Math.abs(f4)) {
                            constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                        } else {
                            constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                        }
                        rectF = this.tempRect;
                        rectF.top -= rectF.width() - width;
                        break;
                    }
                    break;
                case 3:
                    rectF2 = this.tempRect;
                    rectF2.left += f3;
                    rectF2.bottom += f4;
                    if (this.lockAspectRatio > 0.0f) {
                        width = rectF2.width();
                        if (Math.abs(f3) > Math.abs(f4)) {
                            constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                        } else {
                            constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                        }
                        rectF = this.tempRect;
                        rectF.left -= rectF.width() - width;
                        break;
                    }
                    break;
                case 4:
                    rectF2 = this.tempRect;
                    rectF2.right += f3;
                    rectF2.bottom += f4;
                    if (this.lockAspectRatio > 0.0f) {
                        if (Math.abs(f3) <= Math.abs(f4)) {
                            constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                            break;
                        }
                        constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                        break;
                    }
                    break;
                case 5:
                    rectF = this.tempRect;
                    rectF.top += f4;
                    width = this.lockAspectRatio;
                    if (width > 0.0f) {
                        constrainRectByHeight(rectF, width);
                        break;
                    }
                    break;
                case 6:
                    rectF2 = this.tempRect;
                    rectF2.left += f3;
                    f3 = this.lockAspectRatio;
                    if (f3 > 0.0f) {
                        constrainRectByWidth(rectF2, f3);
                        break;
                    }
                    break;
                case 7:
                    rectF2 = this.tempRect;
                    rectF2.right += f3;
                    f3 = this.lockAspectRatio;
                    if (f3 > 0.0f) {
                        constrainRectByWidth(rectF2, f3);
                        break;
                    }
                    break;
                case 8:
                    rectF = this.tempRect;
                    rectF.bottom += f4;
                    width = this.lockAspectRatio;
                    if (width > 0.0f) {
                        constrainRectByHeight(rectF, width);
                        break;
                    }
                    break;
            }
            rectF = this.tempRect;
            width = rectF.left;
            f4 = this.sidePadding;
            if (width < f4) {
                width = this.lockAspectRatio;
                if (width > 0.0f) {
                    rectF.bottom = rectF.top + ((rectF.right - f4) / width);
                }
                this.tempRect.left = this.sidePadding;
            } else if (rectF.right > ((float) getWidth()) - this.sidePadding) {
                this.tempRect.right = ((float) getWidth()) - this.sidePadding;
                if (this.lockAspectRatio > 0.0f) {
                    rectF = this.tempRect;
                    rectF.bottom = rectF.top + (rectF.width() / this.lockAspectRatio);
                }
            }
            f3 = this.sidePadding;
            f += f3;
            width = this.bottomPadding + f3;
            rectF = this.tempRect;
            if (rectF.top < f) {
                width = this.lockAspectRatio;
                if (width > 0.0f) {
                    rectF.right = rectF.left + ((rectF.bottom - f) * width);
                }
                this.tempRect.top = f;
            } else if (rectF.bottom > ((float) getHeight()) - width) {
                this.tempRect.bottom = ((float) getHeight()) - width;
                if (this.lockAspectRatio > 0.0f) {
                    rectF = this.tempRect;
                    rectF.right = rectF.left + (rectF.height() * this.lockAspectRatio);
                }
            }
            f3 = this.tempRect.width();
            width = this.minWidth;
            if (f3 < width) {
                rectF = this.tempRect;
                rectF.right = rectF.left + width;
            }
            f3 = this.tempRect.height();
            width = this.minWidth;
            if (f3 < width) {
                rectF = this.tempRect;
                rectF.bottom = rectF.top + width;
            }
            f3 = this.lockAspectRatio;
            if (f3 > 0.0f) {
                if (f3 < 1.0f) {
                    f3 = this.tempRect.width();
                    width = this.minWidth;
                    if (f3 <= width) {
                        rectF = this.tempRect;
                        rectF.right = rectF.left + width;
                        rectF.bottom = rectF.top + (rectF.width() / this.lockAspectRatio);
                    }
                } else {
                    f3 = this.tempRect.height();
                    width = this.minWidth;
                    if (f3 <= width) {
                        rectF = this.tempRect;
                        rectF.bottom = rectF.top + width;
                        rectF.right = rectF.left + (rectF.height() * this.lockAspectRatio);
                    }
                }
            }
            setActualRect(this.tempRect);
            areaViewListener = this.listener;
            if (areaViewListener != null) {
                areaViewListener.onAreaChange();
            }
            return true;
        }
    }

    private void constrainRectByWidth(RectF rectF, float f) {
        float width = rectF.width();
        f = width / f;
        rectF.right = rectF.left + width;
        rectF.bottom = rectF.top + f;
    }

    private void constrainRectByHeight(RectF rectF, float f) {
        float height = rectF.height();
        rectF.right = rectF.left + (f * height);
        rectF.bottom = rectF.top + height;
    }

    public void getCropRect(RectF rectF) {
        rectF.set(this.actualRect);
    }
}
