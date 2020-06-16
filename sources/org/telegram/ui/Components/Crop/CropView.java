package org.telegram.ui.Components.Crop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Components.Crop.CropAreaView;
import org.telegram.ui.Components.Crop.CropGestureDetector;
import org.telegram.ui.Components.PaintingOverlay;
import org.telegram.ui.Components.VideoEditTextureView;

public class CropView extends FrameLayout implements CropAreaView.AreaViewListener, CropGestureDetector.CropGestureListener {
    /* access modifiers changed from: private */
    public boolean animating = false;
    /* access modifiers changed from: private */
    public CropAreaView areaView;
    private View backView;
    private Bitmap bitmap;
    private float bottomPadding;
    RectF cropRect = new RectF();
    private CropGestureDetector detector;
    /* access modifiers changed from: private */
    public boolean freeform;
    private boolean hasAspectRatioDialog;
    /* access modifiers changed from: private */
    public ImageView imageView;
    private RectF initialAreaRect = new RectF();
    private boolean isVisible;
    /* access modifiers changed from: private */
    public CropViewListener listener;
    private Matrix overlayMatrix = new Matrix();
    private PaintingOverlay paintingOverlay;
    private Matrix presentationMatrix = new Matrix();
    private RectF previousAreaRect = new RectF();
    private float rotationStartScale;
    RectF sizeRect = new RectF(0.0f, 0.0f, 1280.0f, 1280.0f);
    /* access modifiers changed from: private */
    public CropState state;
    private Matrix tempMatrix = new Matrix();
    private CropRectangle tempRect = new CropRectangle();
    float[] values = new float[9];
    private VideoEditTextureView videoEditTextureView;

    public interface CropViewListener {
        void onAspectLock(boolean z);

        void onChange(boolean z);

        void onUpdate();
    }

    public void onFling(float f, float f2, float f3, float f4) {
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return true;
    }

    private class CropState {
        private float baseRotation;
        /* access modifiers changed from: private */
        public float height;
        /* access modifiers changed from: private */
        public Matrix matrix;
        /* access modifiers changed from: private */
        public float minimumScale;
        private float orientation;
        /* access modifiers changed from: private */
        public float rotation;
        /* access modifiers changed from: private */
        public float scale;
        /* access modifiers changed from: private */
        public float width;
        private float x;
        private float y;

        private CropState(int i, int i2, int i3) {
            this.width = (float) i;
            this.height = (float) i2;
            this.x = 0.0f;
            this.y = 0.0f;
            this.scale = 1.0f;
            this.baseRotation = (float) i3;
            this.rotation = 0.0f;
            this.matrix = new Matrix();
        }

        /* access modifiers changed from: private */
        public void update(int i, int i2, int i3) {
            float f = (float) i;
            this.scale *= this.width / f;
            this.width = f;
            this.height = (float) i2;
            updateMinimumScale();
            this.matrix.getValues(CropView.this.values);
            this.matrix.reset();
            Matrix matrix2 = this.matrix;
            float f2 = this.scale;
            matrix2.postScale(f2, f2);
            Matrix matrix3 = this.matrix;
            float[] fArr = CropView.this.values;
            matrix3.postTranslate(fArr[2], fArr[5]);
            CropView.this.updateMatrix();
        }

        /* access modifiers changed from: private */
        public boolean hasChanges() {
            return Math.abs(this.x) > 1.0E-5f || Math.abs(this.y) > 1.0E-5f || Math.abs(this.scale - this.minimumScale) > 1.0E-5f || Math.abs(this.rotation) > 1.0E-5f || Math.abs(this.orientation) > 1.0E-5f;
        }

        /* access modifiers changed from: private */
        public float getWidth() {
            return this.width;
        }

        /* access modifiers changed from: private */
        public float getHeight() {
            return this.height;
        }

        /* access modifiers changed from: private */
        public float getOrientedWidth() {
            return (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.height : this.width;
        }

        /* access modifiers changed from: private */
        public float getOrientedHeight() {
            return (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.width : this.height;
        }

        /* access modifiers changed from: private */
        public void translate(float f, float f2) {
            this.x += f;
            this.y += f2;
            this.matrix.postTranslate(f, f2);
        }

        /* access modifiers changed from: private */
        public float getX() {
            return this.x;
        }

        /* access modifiers changed from: private */
        public float getY() {
            return this.y;
        }

        /* access modifiers changed from: private */
        public void scale(float f, float f2, float f3) {
            this.scale *= f;
            this.matrix.postScale(f, f, f2, f3);
        }

        /* access modifiers changed from: private */
        public float getScale() {
            return this.scale;
        }

        /* access modifiers changed from: private */
        public void rotate(float f, float f2, float f3) {
            this.rotation += f;
            this.matrix.postRotate(f, f2, f3);
        }

        /* access modifiers changed from: private */
        public float getRotation() {
            return this.rotation;
        }

        /* access modifiers changed from: private */
        public float getOrientation() {
            return this.orientation + this.baseRotation;
        }

        /* access modifiers changed from: private */
        public int getOrientationOnly() {
            return (int) this.orientation;
        }

        /* access modifiers changed from: private */
        public float getBaseRotation() {
            return this.baseRotation;
        }

        /* access modifiers changed from: private */
        public void reset(CropAreaView cropAreaView, float f, boolean z) {
            this.matrix.reset();
            this.x = 0.0f;
            this.y = 0.0f;
            this.rotation = 0.0f;
            this.orientation = f;
            updateMinimumScale();
            float f2 = this.minimumScale;
            this.scale = f2;
            this.matrix.postScale(f2, f2);
        }

        private void updateMinimumScale() {
            float f = (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.height : this.width;
            float f2 = (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.width : this.height;
            if (CropView.this.freeform) {
                this.minimumScale = CropView.this.areaView.getCropWidth() / f;
            } else {
                this.minimumScale = Math.max(CropView.this.areaView.getCropWidth() / f, CropView.this.areaView.getCropHeight() / f2);
            }
        }

        /* access modifiers changed from: private */
        public void getConcatMatrix(Matrix matrix2) {
            matrix2.postConcat(this.matrix);
        }

        /* access modifiers changed from: private */
        public Matrix getMatrix() {
            Matrix matrix2 = new Matrix();
            matrix2.set(this.matrix);
            return matrix2;
        }
    }

    public CropView(Context context) {
        super(context);
        View view = new View(context);
        this.backView = view;
        view.setBackgroundColor(-16777216);
        this.backView.setVisibility(4);
        addView(this.backView);
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setDrawingCacheEnabled(true);
        this.imageView.setScaleType(ImageView.ScaleType.MATRIX);
        addView(this.imageView);
        CropGestureDetector cropGestureDetector = new CropGestureDetector(context);
        this.detector = cropGestureDetector;
        cropGestureDetector.setOnGestureListener(this);
        CropAreaView cropAreaView = new CropAreaView(context);
        this.areaView = cropAreaView;
        cropAreaView.setListener(this);
        addView(this.areaView);
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        boolean drawChild = super.drawChild(canvas, view, j);
        if (view == this.imageView && this.paintingOverlay != null && this.videoEditTextureView == null) {
            canvas.save();
            canvas.setMatrix(this.overlayMatrix);
            this.paintingOverlay.draw(canvas);
            canvas.restore();
        }
        return drawChild;
    }

    public boolean isReady() {
        return !this.detector.isScaling() && !this.detector.isDragging() && !this.areaView.isDragging();
    }

    public void setListener(CropViewListener cropViewListener) {
        this.listener = cropViewListener;
    }

    public void setBottomPadding(float f) {
        this.bottomPadding = f;
        this.areaView.setBottomPadding(f);
    }

    public void setAspectRatio(float f) {
        this.areaView.setActualRect(f);
    }

    public void setBitmap(Bitmap bitmap2, int i, boolean z, boolean z2, PaintingOverlay paintingOverlay2, VideoEditTextureView videoEditTextureView2, final MediaController.CropState cropState) {
        this.freeform = z;
        this.paintingOverlay = paintingOverlay2;
        this.videoEditTextureView = videoEditTextureView2;
        this.bitmap = bitmap2;
        Bitmap bitmap3 = null;
        if (bitmap2 == null && videoEditTextureView2 == null) {
            this.state = null;
            this.imageView.setImageDrawable((Drawable) null);
            return;
        }
        final int currentWidth = getCurrentWidth();
        final int currentHeight = getCurrentHeight();
        CropState cropState2 = this.state;
        if (cropState2 == null || !z2) {
            this.state = new CropState(currentWidth, currentHeight, i);
            this.imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    int i;
                    int i2;
                    float f;
                    float f2;
                    CropView.this.reset();
                    MediaController.CropState cropState = cropState;
                    if (cropState != null) {
                        boolean z = true;
                        if (cropState.lockedAspectRatio > 1.0E-4f) {
                            CropView.this.areaView.setLockedAspectRatio(cropState.lockedAspectRatio);
                            if (CropView.this.listener != null) {
                                CropView.this.listener.onAspectLock(true);
                            }
                        }
                        CropView.this.setFreeform(cropState.freeform);
                        float aspectRatio = CropView.this.areaView.getAspectRatio();
                        int i3 = cropState.transformRotation;
                        if (i3 == 90 || i3 == 270) {
                            aspectRatio = 1.0f / aspectRatio;
                            f2 = CropView.this.state.height;
                            f = CropView.this.state.width;
                            i2 = currentHeight;
                            i = currentWidth;
                        } else {
                            f2 = CropView.this.state.width;
                            f = CropView.this.state.height;
                            i2 = currentWidth;
                            i = currentHeight;
                        }
                        int i4 = cropState.transformRotation;
                        boolean access$000 = CropView.this.freeform;
                        if (!CropView.this.freeform || CropView.this.areaView.getLockAspectRatio() <= 0.0f) {
                            CropAreaView access$100 = CropView.this.areaView;
                            int access$700 = CropView.this.getCurrentWidth();
                            int access$800 = CropView.this.getCurrentHeight();
                            if ((((float) i4) + CropView.this.state.getBaseRotation()) % 180.0f == 0.0f) {
                                z = false;
                            }
                            access$100.setBitmap(access$700, access$800, z, CropView.this.freeform);
                        } else {
                            CropView.this.areaView.setLockedAspectRatio(1.0f / CropView.this.areaView.getLockAspectRatio());
                            CropView.this.areaView.setActualRect(CropView.this.areaView.getLockAspectRatio());
                            access$000 = false;
                        }
                        CropView.this.state.reset(CropView.this.areaView, (float) i4, access$000);
                        CropAreaView access$1002 = CropView.this.areaView;
                        MediaController.CropState cropState2 = cropState;
                        access$1002.setActualRect((aspectRatio * cropState2.cropPw) / cropState2.cropPh);
                        CropView.this.state.rotate(cropState.cropRotate, 0.0f, 0.0f);
                        CropView.this.state.translate(cropState.cropPx * ((float) i2) * CropView.this.state.minimumScale, cropState.cropPy * ((float) i) * CropView.this.state.minimumScale);
                        CropView.this.state.scale(cropState.cropScale * (Math.max(CropView.this.areaView.getCropWidth() / f2, CropView.this.areaView.getCropHeight() / f) / CropView.this.state.minimumScale), 0.0f, 0.0f);
                        CropView.this.updateMatrix();
                        if (CropView.this.listener != null) {
                            CropView.this.listener.onChange(false);
                        }
                    }
                    CropView.this.imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
        } else {
            cropState2.update(currentWidth, currentHeight, i);
        }
        ImageView imageView2 = this.imageView;
        if (videoEditTextureView2 == null) {
            bitmap3 = this.bitmap;
        }
        imageView2.setImageBitmap(bitmap3);
    }

    public void willShow() {
        this.areaView.setFrameVisibility(true);
        this.areaView.setDimVisibility(true);
        this.areaView.invalidate();
    }

    public void hideBackView() {
        this.backView.setVisibility(4);
    }

    public void showBackView() {
        if (this.videoEditTextureView != null) {
            this.backView.setVisibility(0);
        }
    }

    public void setFreeform(boolean z) {
        this.areaView.setFreeform(z);
        this.freeform = z;
    }

    public void onShow() {
        this.isVisible = true;
    }

    public void onHide() {
        this.videoEditTextureView = null;
        this.paintingOverlay = null;
        this.isVisible = false;
    }

    public void show() {
        if (this.videoEditTextureView == null) {
            this.backView.setVisibility(0);
        } else {
            updateVideoTextureView();
        }
        this.imageView.setVisibility(0);
        this.areaView.setDimVisibility(true);
        this.areaView.setFrameVisibility(true);
        this.areaView.invalidate();
    }

    public void hide() {
        this.backView.setVisibility(4);
        this.imageView.setVisibility(4);
        this.areaView.setDimVisibility(false);
        this.areaView.setFrameVisibility(false);
        this.areaView.invalidate();
    }

    public void reset() {
        this.areaView.resetAnimator();
        this.areaView.setBitmap(getCurrentWidth(), getCurrentHeight(), this.state.getBaseRotation() % 180.0f != 0.0f, this.freeform);
        this.areaView.setLockedAspectRatio(this.freeform ? 0.0f : 1.0f);
        this.state.reset(this.areaView, 0.0f, this.freeform);
        this.areaView.getCropRect(this.initialAreaRect);
        updateMatrix();
        resetRotationStartScale();
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(true);
            this.listener.onAspectLock(false);
        }
    }

    public void updateMatrix() {
        this.presentationMatrix.reset();
        this.presentationMatrix.postTranslate((-this.state.getWidth()) / 2.0f, (-this.state.getHeight()) / 2.0f);
        this.presentationMatrix.postRotate(this.state.getOrientation());
        this.state.getConcatMatrix(this.presentationMatrix);
        this.presentationMatrix.postTranslate(this.areaView.getCropCenterX(), this.areaView.getCropCenterY());
        this.imageView.setImageMatrix(this.presentationMatrix);
        this.overlayMatrix.reset();
        if (this.state.getBaseRotation() == 90.0f || this.state.getBaseRotation() == 270.0f) {
            this.overlayMatrix.postTranslate((-this.state.getHeight()) / 2.0f, (-this.state.getWidth()) / 2.0f);
        } else {
            this.overlayMatrix.postTranslate((-this.state.getWidth()) / 2.0f, (-this.state.getHeight()) / 2.0f);
        }
        this.overlayMatrix.postRotate((float) this.state.getOrientationOnly());
        this.state.getConcatMatrix(this.overlayMatrix);
        this.overlayMatrix.postTranslate(this.areaView.getCropCenterX(), this.areaView.getCropCenterY());
        if (this.isVisible && this.videoEditTextureView != null) {
            updateVideoTextureView();
            this.listener.onUpdate();
        }
        invalidate();
    }

    private void fillAreaView(RectF rectF, boolean z) {
        final boolean z2;
        float f;
        int i = 0;
        float[] fArr = {1.0f};
        float max = Math.max(rectF.width() / this.areaView.getCropWidth(), rectF.height() / this.areaView.getCropHeight());
        if (this.state.getScale() * max > 30.0f) {
            f = 30.0f / this.state.getScale();
            z2 = true;
        } else {
            f = max;
            z2 = false;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            i = AndroidUtilities.statusBarHeight;
        }
        float f2 = (float) i;
        float access$2300 = this.state.getOrientedWidth() * ((rectF.centerX() - ((float) (this.imageView.getWidth() / 2))) / this.areaView.getCropWidth());
        float centerY = ((rectF.centerY() - (((((float) this.imageView.getHeight()) - this.bottomPadding) + f2) / 2.0f)) / this.areaView.getCropHeight()) * this.state.getOrientedHeight();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(f, fArr, access$2300, centerY) {
            public final /* synthetic */ float f$1;
            public final /* synthetic */ float[] f$2;
            public final /* synthetic */ float f$3;
            public final /* synthetic */ float f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                CropView.this.lambda$fillAreaView$0$CropView(this.f$1, this.f$2, this.f$3, this.f$4, valueAnimator);
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (z2) {
                    CropView.this.fitContentInBounds(false, false, true);
                }
            }
        });
        this.areaView.fill(rectF, ofFloat, true);
        this.initialAreaRect.set(rectF);
    }

    public /* synthetic */ void lambda$fillAreaView$0$CropView(float f, float[] fArr, float f2, float f3, ValueAnimator valueAnimator) {
        float floatValue = (((f - 1.0f) * ((Float) valueAnimator.getAnimatedValue()).floatValue()) + 1.0f) / fArr[0];
        fArr[0] = fArr[0] * floatValue;
        this.state.scale(floatValue, f2, f3);
        updateMatrix();
    }

    private float fitScale(RectF rectF, float f, float f2) {
        float width = rectF.width() * f2;
        float height = rectF.height() * f2;
        float width2 = (rectF.width() - width) / 2.0f;
        float height2 = (rectF.height() - height) / 2.0f;
        float f3 = rectF.left;
        float f4 = rectF.top;
        rectF.set(f3 + width2, f4 + height2, f3 + width2 + width, f4 + height2 + height);
        return f * f2;
    }

    private void fitTranslation(RectF rectF, RectF rectF2, PointF pointF, float f) {
        float f2 = rectF2.left;
        float f3 = rectF2.top;
        float f4 = rectF2.right;
        float f5 = rectF2.bottom;
        float f6 = rectF.left;
        if (f6 > f2) {
            f4 += f6 - f2;
            f2 = f6;
        }
        float f7 = rectF.top;
        if (f7 > f3) {
            f5 += f7 - f3;
            f3 = f7;
        }
        float f8 = rectF.right;
        if (f8 < f4) {
            f2 += f8 - f4;
        }
        float f9 = rectF.bottom;
        if (f9 < f5) {
            f3 += f9 - f5;
        }
        float centerX = rectF2.centerX() - (f2 + (rectF2.width() / 2.0f));
        float centerY = rectF2.centerY() - (f3 + (rectF2.height() / 2.0f));
        double d = (double) f;
        Double.isNaN(d);
        double d2 = 1.5707963267948966d - d;
        double sin = Math.sin(d2);
        double d3 = (double) centerX;
        Double.isNaN(d3);
        double cos = Math.cos(d2);
        Double.isNaN(d3);
        float var_ = (float) (cos * d3);
        Double.isNaN(d);
        double d4 = d + 1.5707963267948966d;
        double cos2 = Math.cos(d4);
        double d5 = (double) centerY;
        Double.isNaN(d5);
        double sin2 = Math.sin(d4);
        Double.isNaN(d5);
        pointF.set(pointF.x + ((float) (sin * d3)) + ((float) (cos2 * d5)), pointF.y + var_ + ((float) (sin2 * d5)));
    }

    public RectF calculateBoundingBox(float f, float f2, float f3) {
        RectF rectF = new RectF(0.0f, 0.0f, f, f2);
        Matrix matrix = new Matrix();
        matrix.postRotate(f3, f / 2.0f, f2 / 2.0f);
        matrix.mapRect(rectF);
        return rectF;
    }

    public float scaleWidthToMaxSize(RectF rectF, RectF rectF2) {
        float width = rectF2.width();
        return ((float) Math.floor((double) ((rectF.height() * width) / rectF.width()))) > rectF2.height() ? (float) Math.floor((double) ((rectF2.height() * rectF.width()) / rectF.height())) : width;
    }

    private static class CropRectangle {
        float[] coords = new float[8];

        CropRectangle() {
        }

        /* access modifiers changed from: package-private */
        public void setRect(RectF rectF) {
            float[] fArr = this.coords;
            float f = rectF.left;
            fArr[0] = f;
            float f2 = rectF.top;
            fArr[1] = f2;
            float f3 = rectF.right;
            fArr[2] = f3;
            fArr[3] = f2;
            fArr[4] = f3;
            float f4 = rectF.bottom;
            fArr[5] = f4;
            fArr[6] = f;
            fArr[7] = f4;
        }

        /* access modifiers changed from: package-private */
        public void applyMatrix(Matrix matrix) {
            matrix.mapPoints(this.coords);
        }

        /* access modifiers changed from: package-private */
        public void getRect(RectF rectF) {
            float[] fArr = this.coords;
            rectF.set(fArr[0], fArr[1], fArr[2], fArr[7]);
        }
    }

    /* access modifiers changed from: private */
    public void fitContentInBounds(boolean z, boolean z2, boolean z3) {
        fitContentInBounds(z, z2, z3, false);
    }

    /* access modifiers changed from: private */
    public void fitContentInBounds(boolean z, boolean z2, boolean z3, boolean z4) {
        float f;
        if (this.state != null) {
            float cropWidth = this.areaView.getCropWidth();
            float cropHeight = this.areaView.getCropHeight();
            float access$2300 = this.state.getOrientedWidth();
            float access$2400 = this.state.getOrientedHeight();
            float access$2600 = this.state.getRotation();
            float radians = (float) Math.toRadians((double) access$2600);
            RectF calculateBoundingBox = calculateBoundingBox(cropWidth, cropHeight, access$2600);
            RectF rectF = new RectF(0.0f, 0.0f, access$2300, access$2400);
            float access$2200 = this.state.getScale();
            this.tempRect.setRect(rectF);
            Matrix access$2700 = this.state.getMatrix();
            access$2700.preTranslate(((cropWidth - access$2300) / 2.0f) / access$2200, ((cropHeight - access$2400) / 2.0f) / access$2200);
            this.tempMatrix.reset();
            this.tempMatrix.setTranslate(rectF.centerX(), rectF.centerY());
            Matrix matrix = this.tempMatrix;
            matrix.setConcat(matrix, access$2700);
            this.tempMatrix.preTranslate(-rectF.centerX(), -rectF.centerY());
            this.tempRect.applyMatrix(this.tempMatrix);
            this.tempMatrix.reset();
            this.tempMatrix.preRotate(-access$2600, access$2300 / 2.0f, access$2400 / 2.0f);
            this.tempRect.applyMatrix(this.tempMatrix);
            this.tempRect.getRect(rectF);
            PointF pointF = new PointF(this.state.getX(), this.state.getY());
            if (!rectF.contains(calculateBoundingBox)) {
                f = (!z || (calculateBoundingBox.width() <= rectF.width() && calculateBoundingBox.height() <= rectF.height())) ? access$2200 : fitScale(rectF, access$2200, calculateBoundingBox.width() / scaleWidthToMaxSize(calculateBoundingBox, rectF));
                fitTranslation(rectF, calculateBoundingBox, pointF, radians);
            } else if (!z2 || this.rotationStartScale <= 0.0f) {
                f = access$2200;
            } else {
                float width = calculateBoundingBox.width() / scaleWidthToMaxSize(calculateBoundingBox, rectF);
                if (this.state.getScale() * width < this.rotationStartScale) {
                    width = 1.0f;
                }
                f = fitScale(rectF, access$2200, width);
                fitTranslation(rectF, calculateBoundingBox, pointF, radians);
            }
            float access$2800 = pointF.x - this.state.getX();
            float access$2900 = pointF.y - this.state.getY();
            if (z3) {
                float f2 = f / access$2200;
                if (Math.abs(f2 - 1.0f) >= 1.0E-5f || Math.abs(access$2800) >= 1.0E-5f || Math.abs(access$2900) >= 1.0E-5f) {
                    this.animating = true;
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(access$2800, new float[]{1.0f, 0.0f, 0.0f}, access$2900, f2) {
                        public final /* synthetic */ float f$1;
                        public final /* synthetic */ float[] f$2;
                        public final /* synthetic */ float f$3;
                        public final /* synthetic */ float f$4;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                        }

                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            CropView.this.lambda$fitContentInBounds$1$CropView(this.f$1, this.f$2, this.f$3, this.f$4, valueAnimator);
                        }
                    });
                    final boolean z5 = z4;
                    final boolean z6 = z;
                    final boolean z7 = z2;
                    final boolean z8 = z3;
                    ofFloat.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            boolean unused = CropView.this.animating = false;
                            if (!z5) {
                                CropView.this.fitContentInBounds(z6, z7, z8, true);
                            }
                        }
                    });
                    ofFloat.setInterpolator(this.areaView.getInterpolator());
                    ofFloat.setDuration(z4 ? 100 : 200);
                    ofFloat.start();
                    return;
                }
                return;
            }
            this.state.translate(access$2800, access$2900);
            this.state.scale(f / access$2200, 0.0f, 0.0f);
            updateMatrix();
        }
    }

    public /* synthetic */ void lambda$fitContentInBounds$1$CropView(float f, float[] fArr, float f2, float f3, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float f4 = (f * floatValue) - fArr[1];
        fArr[1] = fArr[1] + f4;
        float f5 = (f2 * floatValue) - fArr[2];
        fArr[2] = fArr[2] + f5;
        this.state.translate(f4 * fArr[0], f5 * fArr[0]);
        float f6 = (((f3 - 1.0f) * floatValue) + 1.0f) / fArr[0];
        fArr[0] = fArr[0] * f6;
        this.state.scale(f6, 0.0f, 0.0f);
        updateMatrix();
    }

    /* access modifiers changed from: private */
    public int getCurrentWidth() {
        VideoEditTextureView videoEditTextureView2 = this.videoEditTextureView;
        if (videoEditTextureView2 != null) {
            return videoEditTextureView2.getVideoWidth();
        }
        return this.bitmap.getWidth();
    }

    /* access modifiers changed from: private */
    public int getCurrentHeight() {
        VideoEditTextureView videoEditTextureView2 = this.videoEditTextureView;
        if (videoEditTextureView2 != null) {
            return videoEditTextureView2.getVideoHeight();
        }
        return this.bitmap.getHeight();
    }

    public void rotate90Degrees() {
        if (this.state != null) {
            this.areaView.resetAnimator();
            resetRotationStartScale();
            float access$1900 = ((this.state.getOrientation() - this.state.getBaseRotation()) - 90.0f) % 360.0f;
            boolean z = this.freeform;
            boolean z2 = true;
            if (!z || this.areaView.getLockAspectRatio() <= 0.0f) {
                this.areaView.setBitmap(getCurrentWidth(), getCurrentHeight(), (this.state.getBaseRotation() + access$1900) % 180.0f != 0.0f, this.freeform);
            } else {
                CropAreaView cropAreaView = this.areaView;
                cropAreaView.setLockedAspectRatio(1.0f / cropAreaView.getLockAspectRatio());
                CropAreaView cropAreaView2 = this.areaView;
                cropAreaView2.setActualRect(cropAreaView2.getLockAspectRatio());
                z = false;
            }
            this.state.reset(this.areaView, access$1900, z);
            updateMatrix();
            CropViewListener cropViewListener = this.listener;
            if (cropViewListener != null) {
                if (!(access$1900 == 0.0f && this.areaView.getLockAspectRatio() == 0.0f)) {
                    z2 = false;
                }
                cropViewListener.onChange(z2);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.animating || this.areaView.onTouchEvent(motionEvent)) {
            return true;
        }
        int action = motionEvent.getAction();
        if (action == 0) {
            onScrollChangeBegan();
        } else if (action == 1 || action == 3) {
            onScrollChangeEnded();
        }
        try {
            return this.detector.onTouchEvent(motionEvent);
        } catch (Exception unused) {
            return false;
        }
    }

    public void onAreaChangeBegan() {
        this.areaView.getCropRect(this.previousAreaRect);
        resetRotationStartScale();
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(false);
        }
    }

    public void onAreaChange() {
        this.areaView.setGridType(CropAreaView.GridType.MAJOR, false);
        this.state.translate(this.previousAreaRect.centerX() - this.areaView.getCropCenterX(), this.previousAreaRect.centerY() - this.areaView.getCropCenterY());
        updateMatrix();
        this.areaView.getCropRect(this.previousAreaRect);
        fitContentInBounds(true, false, false);
    }

    public void onAreaChangeEnded() {
        this.areaView.setGridType(CropAreaView.GridType.NONE, true);
        fillAreaView(this.areaView.getTargetRectToFill(), false);
    }

    public void onDrag(float f, float f2) {
        if (!this.animating) {
            this.state.translate(f, f2);
            updateMatrix();
        }
    }

    public void onScrollChangeBegan() {
        if (!this.animating) {
            this.areaView.setGridType(CropAreaView.GridType.MAJOR, true);
            resetRotationStartScale();
            CropViewListener cropViewListener = this.listener;
            if (cropViewListener != null) {
                cropViewListener.onChange(false);
            }
        }
    }

    public void onScrollChangeEnded() {
        this.areaView.setGridType(CropAreaView.GridType.NONE, true);
        fitContentInBounds(true, false, true);
    }

    public void onScale(float f, float f2, float f3) {
        if (!this.animating) {
            if (this.state.getScale() * f > 30.0f) {
                f = 30.0f / this.state.getScale();
            }
            this.state.scale(f, ((f2 - ((float) (this.imageView.getWidth() / 2))) / this.areaView.getCropWidth()) * this.state.getOrientedWidth(), ((f3 - (((((float) this.imageView.getHeight()) - this.bottomPadding) - ((float) (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))) / 2.0f)) / this.areaView.getCropHeight()) * this.state.getOrientedHeight());
            updateMatrix();
        }
    }

    public void onRotationBegan() {
        this.areaView.setGridType(CropAreaView.GridType.MINOR, false);
        if (this.rotationStartScale < 1.0E-5f) {
            this.rotationStartScale = this.state.getScale();
        }
    }

    public void onRotationEnded() {
        this.areaView.setGridType(CropAreaView.GridType.NONE, true);
    }

    private void resetRotationStartScale() {
        this.rotationStartScale = 0.0f;
    }

    public void setRotation(float f) {
        this.state.rotate(f - this.state.getRotation(), 0.0f, 0.0f);
        fitContentInBounds(true, true, false);
    }

    public static void editBitmap(String str, Bitmap bitmap2, Canvas canvas, Bitmap bitmap3, Bitmap.CompressFormat compressFormat, Matrix matrix, int i, int i2, float f, float f2, float f3, float f4, ArrayList<VideoEditedInfo.MediaEntity> arrayList, boolean z) {
        Bitmap bitmap4 = bitmap3;
        float f5 = f3;
        float f6 = f4;
        ArrayList<VideoEditedInfo.MediaEntity> arrayList2 = arrayList;
        char c = 0;
        if (z) {
            try {
                bitmap4.eraseColor(0);
            } catch (Throwable th) {
                FileLog.e(th);
                return;
            }
        }
        Bitmap decodeFile = bitmap2 == null ? BitmapFactory.decodeFile(str) : bitmap2;
        float max = ((float) Math.max(decodeFile.getWidth(), decodeFile.getHeight())) / ((float) Math.max(i, i2));
        Matrix matrix2 = new Matrix();
        matrix2.postTranslate((float) ((-decodeFile.getWidth()) / 2), (float) ((-decodeFile.getHeight()) / 2));
        float f7 = 1.0f / max;
        matrix2.postScale(f7, f7);
        matrix2.postRotate(f5);
        matrix2.postConcat(matrix);
        matrix2.postScale(f6, f6);
        matrix2.postTranslate((float) (bitmap3.getWidth() / 2), (float) (bitmap3.getHeight() / 2));
        canvas.drawBitmap(decodeFile, matrix2, new Paint(2));
        FileOutputStream fileOutputStream = new FileOutputStream(new File(str));
        bitmap4.compress(compressFormat, 87, fileOutputStream);
        fileOutputStream.close();
        if (arrayList2 != null && !arrayList.isEmpty()) {
            float[] fArr = new float[4];
            float f8 = f7 * f6 * f;
            int size = arrayList.size();
            int i3 = 0;
            while (i3 < size) {
                VideoEditedInfo.MediaEntity mediaEntity = arrayList2.get(i3);
                fArr[c] = (mediaEntity.x * ((float) decodeFile.getWidth())) + ((((float) mediaEntity.viewWidth) * mediaEntity.scale) / 2.0f);
                fArr[1] = (mediaEntity.y * ((float) decodeFile.getHeight())) + ((((float) mediaEntity.viewHeight) * mediaEntity.scale) / 2.0f);
                fArr[2] = mediaEntity.textViewX * ((float) decodeFile.getWidth());
                fArr[3] = mediaEntity.textViewY * ((float) decodeFile.getHeight());
                matrix2.mapPoints(fArr);
                f8 *= ((float) decodeFile.getWidth()) / ((float) bitmap3.getWidth());
                if (mediaEntity.type == 0) {
                    int width = bitmap3.getWidth() / 2;
                    mediaEntity.viewHeight = width;
                    mediaEntity.viewWidth = width;
                } else if (mediaEntity.type == 1) {
                    mediaEntity.fontSize = bitmap3.getWidth() / 9;
                }
                float f9 = mediaEntity.scale * f8;
                mediaEntity.scale = f9;
                mediaEntity.x = (fArr[c] - ((((float) mediaEntity.viewWidth) * f9) / 2.0f)) / ((float) bitmap3.getWidth());
                mediaEntity.y = (fArr[1] - ((((float) mediaEntity.viewHeight) * mediaEntity.scale) / 2.0f)) / ((float) bitmap3.getHeight());
                mediaEntity.textViewX = fArr[2] / ((float) bitmap3.getWidth());
                mediaEntity.textViewY = fArr[3] / ((float) bitmap3.getHeight());
                mediaEntity.width = (((float) mediaEntity.viewWidth) * mediaEntity.scale) / ((float) bitmap3.getWidth());
                mediaEntity.height = (((float) mediaEntity.viewHeight) * mediaEntity.scale) / ((float) bitmap3.getHeight());
                mediaEntity.textViewWidth = ((float) mediaEntity.viewWidth) / ((float) bitmap3.getWidth());
                mediaEntity.textViewHeight = ((float) mediaEntity.viewHeight) / ((float) bitmap3.getHeight());
                double d = (double) mediaEntity.rotation;
                double d2 = (double) (f2 + f5);
                Double.isNaN(d2);
                Double.isNaN(d);
                mediaEntity.rotation = (float) (d - (d2 * 0.017453292519943295d));
                i3++;
                c = 0;
            }
        }
        decodeFile.recycle();
    }

    private void updateVideoTextureView() {
        float f;
        int i;
        float f2;
        this.areaView.getCropRect(this.cropRect);
        int ceil = (int) Math.ceil((double) scaleWidthToMaxSize(this.cropRect, this.sizeRect));
        float f3 = (float) ceil;
        int ceil2 = (int) Math.ceil((double) (f3 / this.areaView.getAspectRatio()));
        float cropWidth = f3 / this.areaView.getCropWidth();
        this.state.matrix.getValues(this.values);
        float access$1200 = this.state.minimumScale * cropWidth;
        int access$2100 = this.state.getOrientationOnly();
        while (access$2100 < 0) {
            access$2100 += 360;
        }
        if (access$2100 == 90 || access$2100 == 270) {
            i = (int) this.state.height;
            f = this.state.width;
        } else {
            i = (int) this.state.width;
            f = this.state.height;
        }
        double d = (double) ceil;
        float f4 = (float) i;
        double ceil3 = Math.ceil((double) (f4 * access$1200));
        Double.isNaN(d);
        float f5 = (float) (d / ceil3);
        double d2 = (double) ceil2;
        float f6 = (float) ((int) f);
        double ceil4 = Math.ceil((double) (access$1200 * f6));
        Double.isNaN(d2);
        float f7 = (float) (d2 / ceil4);
        if (f5 > 1.0f || f7 > 1.0f) {
            float max = Math.max(f5, f7);
            f5 /= max;
            f7 /= max;
        }
        float f8 = f5;
        float f9 = f7;
        RectF targetRectToFill = this.areaView.getTargetRectToFill(f4 / f6);
        if (this.freeform) {
            f2 = targetRectToFill.width() / f4;
        } else {
            f2 = Math.max(targetRectToFill.width() / f4, targetRectToFill.height() / f6);
        }
        float access$3300 = (this.state.scale / this.state.minimumScale) / (f2 / this.state.minimumScale);
        float access$33002 = (this.values[2] / f4) / this.state.scale;
        float access$33003 = (this.values[5] / f6) / this.state.scale;
        float access$3400 = this.state.rotation;
        RectF targetRectToFill2 = this.areaView.getTargetRectToFill();
        this.videoEditTextureView.setViewTransform(this.state.hasChanges() || this.state.getBaseRotation() >= 1.0E-5f, access$33002, access$33003, access$3400, this.state.getOrientationOnly(), access$3300, f8, f9, this.areaView.getCropCenterX() - targetRectToFill2.centerX(), this.areaView.getCropCenterY() - targetRectToFill2.centerY());
    }

    public static String getPathOrCopy(boolean z, String str) {
        if (!z) {
            return str;
        }
        File directory = FileLoader.getDirectory(4);
        File file = new File(directory, SharedConfig.getLastLocalId() + "_temp.jpg");
        try {
            AndroidUtilities.copyFile(new File(str), file);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return file.getAbsolutePath();
    }

    public Bitmap getResult(MediaController.MediaEditState mediaEditState) {
        Matrix matrix;
        Canvas canvas;
        boolean z;
        int i;
        float f;
        int i2;
        MediaController.MediaEditState mediaEditState2 = mediaEditState;
        CropState cropState = this.state;
        if (cropState != null && (this.videoEditTextureView != null || cropState.hasChanges() || this.state.getBaseRotation() >= 1.0E-5f || !this.freeform)) {
            this.areaView.getCropRect(this.cropRect);
            int ceil = (int) Math.ceil((double) scaleWidthToMaxSize(this.cropRect, this.sizeRect));
            float f2 = (float) ceil;
            int ceil2 = (int) Math.ceil((double) (f2 / this.areaView.getAspectRatio()));
            float cropWidth = f2 / this.areaView.getCropWidth();
            Bitmap createBitmap = Bitmap.createBitmap(ceil, ceil2, Bitmap.Config.ARGB_8888);
            Matrix matrix2 = new Matrix();
            matrix2.postTranslate((-this.state.getWidth()) / 2.0f, (-this.state.getHeight()) / 2.0f);
            matrix2.postRotate(this.state.getOrientation());
            this.state.getConcatMatrix(matrix2);
            matrix2.postScale(cropWidth, cropWidth);
            matrix2.postTranslate((float) (ceil / 2), (float) (ceil2 / 2));
            Canvas canvas2 = new Canvas(createBitmap);
            if (mediaEditState2.paintPath != null) {
                String pathOrCopy = getPathOrCopy(this.videoEditTextureView != null, mediaEditState2.paintPath);
                if (this.videoEditTextureView != null) {
                    if (mediaEditState2.croppedPaintPath != null) {
                        new File(mediaEditState2.croppedPaintPath).delete();
                        mediaEditState2.croppedPaintPath = null;
                    }
                    mediaEditState2.croppedPaintPath = pathOrCopy;
                    ArrayList<VideoEditedInfo.MediaEntity> arrayList = mediaEditState2.mediaEntities;
                    if (arrayList == null || arrayList.isEmpty()) {
                        mediaEditState2.croppedMediaEntities = null;
                    } else {
                        mediaEditState2.croppedMediaEntities = new ArrayList<>(mediaEditState2.mediaEntities.size());
                        int size = mediaEditState2.mediaEntities.size();
                        for (int i3 = 0; i3 < size; i3++) {
                            mediaEditState2.croppedMediaEntities.add(mediaEditState2.mediaEntities.get(i3).copy());
                        }
                    }
                } else {
                    mediaEditState2.croppedMediaEntities = null;
                }
                z = true;
                canvas = canvas2;
                matrix = matrix2;
                editBitmap(pathOrCopy, (Bitmap) null, canvas2, createBitmap, Bitmap.CompressFormat.PNG, this.state.matrix, getCurrentWidth(), getCurrentHeight(), this.state.scale, this.state.rotation, (float) this.state.getOrientationOnly(), cropWidth, mediaEditState2.croppedMediaEntities, false);
                if (this.videoEditTextureView == null && !mediaEditState2.paintPath.equals(mediaEditState2.fullPaintPath)) {
                    editBitmap(mediaEditState2.fullPaintPath, (Bitmap) null, canvas, createBitmap, Bitmap.CompressFormat.PNG, this.state.matrix, getCurrentWidth(), getCurrentHeight(), this.state.scale, this.state.rotation, (float) this.state.getOrientationOnly(), cropWidth, mediaEditState2.mediaEntities, true);
                }
            } else {
                canvas = canvas2;
                matrix = matrix2;
                z = true;
            }
            if (mediaEditState2.filterPath != null && this.videoEditTextureView == null) {
                if (mediaEditState2.croppedPath == null) {
                    mediaEditState2.croppedPath = new File(FileLoader.getDirectory(4), SharedConfig.getLastLocalId() + "_temp.jpg").getAbsolutePath();
                }
                editBitmap(mediaEditState2.croppedPath, ImageLoader.loadBitmap(mediaEditState.getPath(), (Uri) null, (float) getCurrentWidth(), (float) getCurrentHeight(), z), canvas, createBitmap, Bitmap.CompressFormat.JPEG, this.state.matrix, getCurrentWidth(), getCurrentHeight(), this.state.scale, this.state.rotation, (float) this.state.getOrientationOnly(), cropWidth, (ArrayList<VideoEditedInfo.MediaEntity>) null, false);
            }
            if (this.videoEditTextureView == null) {
                canvas.drawBitmap(this.bitmap, matrix, new Paint(2));
                return createBitmap;
            }
            if (mediaEditState2.cropState == null) {
                mediaEditState2.cropState = new MediaController.CropState();
            }
            this.state.matrix.getValues(this.values);
            float access$1200 = this.state.minimumScale * cropWidth;
            mediaEditState2.cropState.transformRotation = this.state.getOrientationOnly();
            while (true) {
                MediaController.CropState cropState2 = mediaEditState2.cropState;
                i = cropState2.transformRotation;
                if (i >= 0) {
                    break;
                }
                cropState2.transformRotation = i + 360;
            }
            if (i == 90 || i == 270) {
                i2 = (int) this.state.height;
                f = this.state.width;
            } else {
                i2 = (int) this.state.width;
                f = this.state.height;
            }
            MediaController.CropState cropState3 = mediaEditState2.cropState;
            double d = (double) ceil;
            float f3 = (float) i2;
            double ceil3 = Math.ceil((double) (f3 * access$1200));
            Double.isNaN(d);
            cropState3.cropPw = (float) (d / ceil3);
            MediaController.CropState cropState4 = mediaEditState2.cropState;
            double d2 = (double) ceil2;
            float f4 = (float) ((int) f);
            double ceil4 = Math.ceil((double) (access$1200 * f4));
            Double.isNaN(d2);
            cropState4.cropPh = (float) (d2 / ceil4);
            MediaController.CropState cropState5 = mediaEditState2.cropState;
            if (cropState5.cropPw > 1.0f || cropState5.cropPh > 1.0f) {
                MediaController.CropState cropState6 = mediaEditState2.cropState;
                float max = Math.max(cropState6.cropPw, cropState6.cropPh);
                MediaController.CropState cropState7 = mediaEditState2.cropState;
                cropState7.cropPw /= max;
                cropState7.cropPh /= max;
            }
            mediaEditState2.cropState.cropScale = this.state.scale * Math.min(f3 / this.areaView.getCropWidth(), f4 / this.areaView.getCropHeight());
            mediaEditState2.cropState.cropPx = (this.values[2] / f3) / this.state.scale;
            mediaEditState2.cropState.cropPy = (this.values[5] / f4) / this.state.scale;
            mediaEditState2.cropState.cropRotate = this.state.rotation;
            mediaEditState2.cropState.stateScale = this.state.scale;
            MediaController.CropState cropState8 = mediaEditState2.cropState;
            cropState8.scale = cropWidth;
            cropState8.matrix = this.state.matrix;
            MediaController.CropState cropState9 = mediaEditState2.cropState;
            cropState9.width = ceil;
            cropState9.height = ceil2;
            cropState9.freeform = this.freeform;
            cropState9.lockedAspectRatio = this.areaView.getLockAspectRatio();
            return null;
        } else if (this.videoEditTextureView != null) {
            return null;
        } else {
            return this.bitmap;
        }
    }

    private void setLockedAspectRatio(float f) {
        this.areaView.setLockedAspectRatio(f);
        RectF rectF = new RectF();
        this.areaView.calculateRect(rectF, f);
        fillAreaView(rectF, true);
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(false);
            this.listener.onAspectLock(true);
        }
    }

    public void showAspectRatioDialog() {
        if (this.state != null) {
            if (this.areaView.getLockAspectRatio() > 0.0f) {
                this.areaView.setLockedAspectRatio(0.0f);
                CropViewListener cropViewListener = this.listener;
                if (cropViewListener != null) {
                    cropViewListener.onAspectLock(false);
                }
            } else if (!this.hasAspectRatioDialog) {
                this.hasAspectRatioDialog = true;
                String[] strArr = new String[8];
                Integer[][] numArr = {new Integer[]{3, 2}, new Integer[]{5, 3}, new Integer[]{4, 3}, new Integer[]{5, 4}, new Integer[]{7, 5}, new Integer[]{16, 9}};
                strArr[0] = LocaleController.getString("CropOriginal", NUM);
                strArr[1] = LocaleController.getString("CropSquare", NUM);
                int i = 2;
                for (int i2 = 0; i2 < 6; i2++) {
                    Integer[] numArr2 = numArr[i2];
                    if (this.areaView.getAspectRatio() > 1.0f) {
                        strArr[i] = String.format("%d:%d", new Object[]{numArr2[0], numArr2[1]});
                    } else {
                        strArr[i] = String.format("%d:%d", new Object[]{numArr2[1], numArr2[0]});
                    }
                    i++;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setItems(strArr, new DialogInterface.OnClickListener(numArr) {
                    public final /* synthetic */ Integer[][] f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        CropView.this.lambda$showAspectRatioDialog$2$CropView(this.f$1, dialogInterface, i);
                    }
                });
                AlertDialog create = builder.create();
                create.setCanceledOnTouchOutside(true);
                create.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public final void onCancel(DialogInterface dialogInterface) {
                        CropView.this.lambda$showAspectRatioDialog$3$CropView(dialogInterface);
                    }
                });
                create.show();
            }
        }
    }

    public /* synthetic */ void lambda$showAspectRatioDialog$2$CropView(Integer[][] numArr, DialogInterface dialogInterface, int i) {
        this.hasAspectRatioDialog = false;
        if (i == 0) {
            setLockedAspectRatio((this.state.getBaseRotation() % 180.0f != 0.0f ? this.state.getHeight() : this.state.getWidth()) / (this.state.getBaseRotation() % 180.0f != 0.0f ? this.state.getWidth() : this.state.getHeight()));
        } else if (i != 1) {
            Integer[] numArr2 = numArr[i - 2];
            if (this.areaView.getAspectRatio() > 1.0f) {
                setLockedAspectRatio(((float) numArr2[0].intValue()) / ((float) numArr2[1].intValue()));
            } else {
                setLockedAspectRatio(((float) numArr2[1].intValue()) / ((float) numArr2[0].intValue()));
            }
        } else {
            setLockedAspectRatio(1.0f);
        }
    }

    public /* synthetic */ void lambda$showAspectRatioDialog$3$CropView(DialogInterface dialogInterface) {
        this.hasAspectRatioDialog = false;
    }

    public void updateLayout() {
        CropState cropState;
        float cropWidth = this.areaView.getCropWidth();
        if (cropWidth != 0.0f && (cropState = this.state) != null) {
            this.areaView.calculateRect(this.initialAreaRect, cropState.getWidth() / this.state.getHeight());
            CropAreaView cropAreaView = this.areaView;
            cropAreaView.setActualRect(cropAreaView.getAspectRatio());
            this.areaView.getCropRect(this.previousAreaRect);
            this.state.scale(this.areaView.getCropWidth() / cropWidth, 0.0f, 0.0f);
            updateMatrix();
        }
    }

    public float getCropLeft() {
        return this.areaView.getCropLeft();
    }

    public float getCropTop() {
        return this.areaView.getCropTop();
    }

    public float getCropWidth() {
        return this.areaView.getCropWidth();
    }

    public float getCropHeight() {
        return this.areaView.getCropHeight();
    }
}
