package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.Crop.CropRotationWheel;
import org.telegram.ui.Components.Crop.CropRotationWheel.RotationWheelListener;
import org.telegram.ui.Components.Crop.CropView;
import org.telegram.ui.Components.Crop.CropView.CropViewListener;

public class PhotoCropView extends FrameLayout {
    private RectF animationEndValues;
    private Runnable animationRunnable;
    private RectF animationStartValues;
    private float bitmapGlobalScale = 1.0f;
    private float bitmapGlobalX = 0.0f;
    private float bitmapGlobalY = 0.0f;
    private int bitmapHeight = 1;
    private Bitmap bitmapToEdit;
    private int bitmapWidth = 1;
    private int bitmapX;
    private int bitmapY;
    private CropView cropView;
    private PhotoCropViewDelegate delegate;
    private int draggingState = 0;
    private boolean freeformCrop = true;
    private float oldX = 0.0f;
    private float oldY = 0.0f;
    private int orientation;
    private float rectSizeX = 600.0f;
    private float rectSizeY = 600.0f;
    private float rectX = -1.0f;
    private float rectY = -1.0f;
    private boolean showOnSetBitmap;
    private CropRotationWheel wheelView;

    /* renamed from: org.telegram.ui.Components.PhotoCropView$3 */
    class C12383 implements Runnable {
        C12383() {
        }

        public void run() {
            if (PhotoCropView.this.animationRunnable == this) {
                PhotoCropView.this.animationRunnable = null;
                PhotoCropView.this.moveToFill(true);
            }
        }
    }

    public interface PhotoCropViewDelegate {
        Bitmap getBitmap();

        void needMoveImageTo(float f, float f2, float f3, boolean z);

        void onChange(boolean z);
    }

    /* renamed from: org.telegram.ui.Components.PhotoCropView$1 */
    class C20661 implements CropViewListener {
        C20661() {
        }

        public void onChange(boolean z) {
            if (PhotoCropView.this.delegate != null) {
                PhotoCropView.this.delegate.onChange(z);
            }
        }

        public void onAspectLock(boolean z) {
            PhotoCropView.this.wheelView.setAspectLock(z);
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoCropView$2 */
    class C20672 implements RotationWheelListener {
        C20672() {
        }

        public void onStart() {
            PhotoCropView.this.cropView.onRotationBegan();
        }

        public void onChange(float f) {
            PhotoCropView.this.cropView.setRotation(f);
            if (PhotoCropView.this.delegate != null) {
                PhotoCropView.this.delegate.onChange(false);
            }
        }

        public void onEnd(float f) {
            PhotoCropView.this.cropView.onRotationEnded();
        }

        public void aspectRatioPressed() {
            PhotoCropView.this.cropView.showAspectRatioDialog();
        }

        public void rotate90Pressed() {
            PhotoCropView.this.wheelView.reset();
            PhotoCropView.this.cropView.rotate90Degrees();
        }
    }

    public PhotoCropView(Context context) {
        super(context);
    }

    public void setBitmap(Bitmap bitmap, int i, boolean z) {
        this.bitmapToEdit = bitmap;
        this.rectSizeX = 600.0f;
        this.rectSizeY = 600.0f;
        this.draggingState = 0;
        this.oldX = 0.0f;
        this.oldY = 0.0f;
        this.bitmapWidth = 1;
        this.bitmapHeight = 1;
        this.rectX = -1.0f;
        this.rectY = -1.0f;
        this.freeformCrop = z;
        this.orientation = i;
        requestLayout();
        if (this.cropView == null) {
            this.cropView = new CropView(getContext());
            this.cropView.setListener(new C20661());
            this.cropView.setBottomPadding((float) AndroidUtilities.dp(64.0f));
            addView(this.cropView);
            this.wheelView = new CropRotationWheel(getContext());
            this.wheelView.setListener(new C20672());
            addView(this.wheelView, LayoutHelper.createFrame(-1, -2.0f, 81, 0.0f, 0.0f, 0.0f, 0.0f));
        }
        this.cropView.setVisibility(0);
        this.cropView.setBitmap(bitmap, i, z);
        if (this.showOnSetBitmap != null) {
            this.showOnSetBitmap = false;
            this.cropView.show();
        }
        this.wheelView.setFreeform(z);
        this.wheelView.reset();
    }

    public void setOrientation(int i) {
        this.orientation = i;
        this.rectX = -1.0f;
        this.rectY = -1.0f;
        this.rectSizeX = 600.0f;
        this.rectSizeY = 600.0f;
        this.delegate.needMoveImageTo(0.0f, 0.0f, 1.0f, false);
        requestLayout();
    }

    public boolean isReady() {
        return this.cropView.isReady();
    }

    public void reset() {
        this.wheelView.reset();
        this.cropView.reset();
    }

    public void onAppear() {
        if (this.cropView != null) {
            this.cropView.willShow();
        }
    }

    public void onAppeared() {
        if (this.cropView != null) {
            this.cropView.show();
        } else {
            this.showOnSetBitmap = true;
        }
    }

    public void onDisappear() {
        this.cropView.hide();
    }

    public float getRectX() {
        return this.cropView.getCropLeft() - ((float) AndroidUtilities.dp(14.0f));
    }

    public float getRectY() {
        return (this.cropView.getCropTop() - ((float) AndroidUtilities.dp(14.0f))) - ((float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
    }

    public float getRectSizeX() {
        return this.cropView.getCropWidth();
    }

    public float getRectSizeY() {
        return this.cropView.getCropHeight();
    }

    public float getBitmapX() {
        return (float) (this.bitmapX - AndroidUtilities.dp(14.0f));
    }

    public float getBitmapY() {
        return ((float) (this.bitmapY - AndroidUtilities.dp(14.0f))) - ((float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
    }

    public float getLimitX() {
        return this.rectX - Math.max(0.0f, (float) Math.ceil((double) ((((float) getWidth()) - (((float) this.bitmapWidth) * this.bitmapGlobalScale)) / 2.0f)));
    }

    public float getLimitY() {
        return this.rectY - Math.max(0.0f, (float) Math.ceil((double) (((((float) getHeight()) - (((float) this.bitmapHeight) * this.bitmapGlobalScale)) + ((float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))) / 2.0f)));
    }

    public float getLimitWidth() {
        return ((((float) (getWidth() - AndroidUtilities.dp(14.0f))) - this.rectX) - ((float) ((int) Math.max(0.0d, Math.ceil((double) ((((float) (getWidth() - AndroidUtilities.dp(28.0f))) - (((float) this.bitmapWidth) * this.bitmapGlobalScale)) / 2.0f)))))) - this.rectSizeX;
    }

    public float getLimitHeight() {
        float f = (float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        return (((((float) (getHeight() - AndroidUtilities.dp(14.0f))) - f) - this.rectY) - ((float) ((int) Math.max(0.0d, Math.ceil((double) (((((float) (getHeight() - AndroidUtilities.dp(28.0f))) - (((float) this.bitmapHeight) * this.bitmapGlobalScale)) - f) / 2.0f)))))) - this.rectSizeY;
    }

    public Bitmap getBitmap() {
        return this.cropView != null ? this.cropView.getResult() : null;
    }

    public void setBitmapParams(float f, float f2, float f3) {
        this.bitmapGlobalScale = f;
        this.bitmapGlobalX = f2;
        this.bitmapGlobalY = f3;
    }

    public void startAnimationRunnable() {
        if (this.animationRunnable == null) {
            this.animationRunnable = new C12383();
            AndroidUtilities.runOnUIThread(this.animationRunnable, 1500);
        }
    }

    public void cancelAnimationRunnable() {
        if (this.animationRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
            this.animationRunnable = null;
            this.animationStartValues = null;
            this.animationEndValues = null;
        }
    }

    public void setAnimationProgress(float f) {
        if (this.animationStartValues != null) {
            if (f == 1.0f) {
                this.rectX = this.animationEndValues.left;
                this.rectY = this.animationEndValues.top;
                this.rectSizeX = this.animationEndValues.right;
                this.rectSizeY = this.animationEndValues.bottom;
                this.animationStartValues = null;
                this.animationEndValues = null;
            } else {
                this.rectX = this.animationStartValues.left + ((this.animationEndValues.left - this.animationStartValues.left) * f);
                this.rectY = this.animationStartValues.top + ((this.animationEndValues.top - this.animationStartValues.top) * f);
                this.rectSizeX = this.animationStartValues.right + ((this.animationEndValues.right - this.animationStartValues.right) * f);
                this.rectSizeY = this.animationStartValues.bottom + ((this.animationEndValues.bottom - this.animationStartValues.bottom) * f);
            }
            invalidate();
        }
    }

    public void moveToFill(boolean z) {
        float f = ((float) this.bitmapWidth) / this.rectSizeX;
        float f2 = ((float) this.bitmapHeight) / this.rectSizeY;
        if (f > f2) {
            f = f2;
        }
        if (f > 1.0f && this.bitmapGlobalScale * f > 3.0f) {
            f = 3.0f / this.bitmapGlobalScale;
        } else if (f < 1.0f && this.bitmapGlobalScale * f < 1.0f) {
            f = 1.0f / this.bitmapGlobalScale;
        }
        float f3 = this.rectSizeX * f;
        float f4 = this.rectSizeY * f;
        float f5 = (float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        float width = (((float) getWidth()) - f3) / 2.0f;
        float height = ((((float) getHeight()) - f4) + f5) / 2.0f;
        this.animationStartValues = new RectF(this.rectX, this.rectY, this.rectSizeX, this.rectSizeY);
        this.animationEndValues = new RectF(width, height, f3, f4);
        f2 = f - 1.0f;
        this.delegate.needMoveImageTo((width + (((float) (getWidth() / 2)) * f2)) + ((this.bitmapGlobalX - this.rectX) * f), (height + (((((float) getHeight()) + f5) / 2.0f) * f2)) + ((this.bitmapGlobalY - this.rectY) * f), this.bitmapGlobalScale * f, z);
    }

    public void setDelegate(PhotoCropViewDelegate photoCropViewDelegate) {
        this.delegate = photoCropViewDelegate;
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        z = this.delegate.getBitmap();
        if (z) {
            this.bitmapToEdit = z;
        }
        if (this.cropView) {
            this.cropView.updateLayout();
        }
    }
}
