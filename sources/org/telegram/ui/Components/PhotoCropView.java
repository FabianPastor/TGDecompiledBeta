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
    class C12323 implements Runnable {
        C12323() {
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
    class C20601 implements CropViewListener {
        C20601() {
        }

        public void onChange(boolean reset) {
            if (PhotoCropView.this.delegate != null) {
                PhotoCropView.this.delegate.onChange(reset);
            }
        }

        public void onAspectLock(boolean enabled) {
            PhotoCropView.this.wheelView.setAspectLock(enabled);
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoCropView$2 */
    class C20612 implements RotationWheelListener {
        C20612() {
        }

        public void onStart() {
            PhotoCropView.this.cropView.onRotationBegan();
        }

        public void onChange(float angle) {
            PhotoCropView.this.cropView.setRotation(angle);
            if (PhotoCropView.this.delegate != null) {
                PhotoCropView.this.delegate.onChange(false);
            }
        }

        public void onEnd(float angle) {
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

    public void setBitmap(Bitmap bitmap, int rotation, boolean freeform) {
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
        this.freeformCrop = freeform;
        this.orientation = rotation;
        requestLayout();
        if (this.cropView == null) {
            this.cropView = new CropView(getContext());
            this.cropView.setListener(new C20601());
            this.cropView.setBottomPadding((float) AndroidUtilities.dp(64.0f));
            addView(this.cropView);
            this.wheelView = new CropRotationWheel(getContext());
            this.wheelView.setListener(new C20612());
            addView(this.wheelView, LayoutHelper.createFrame(-1, -2.0f, 81, 0.0f, 0.0f, 0.0f, 0.0f));
        }
        this.cropView.setVisibility(0);
        this.cropView.setBitmap(bitmap, rotation, freeform);
        if (this.showOnSetBitmap) {
            this.showOnSetBitmap = false;
            this.cropView.show();
        }
        this.wheelView.setFreeform(freeform);
        this.wheelView.reset();
    }

    public void setOrientation(int rotation) {
        this.orientation = rotation;
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
        return ((float) (this.bitmapY - AndroidUtilities.dp(14.0f))) - ((float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0.0f));
    }

    public float getLimitX() {
        return this.rectX - Math.max(0.0f, (float) Math.ceil((double) ((((float) getWidth()) - (((float) this.bitmapWidth) * this.bitmapGlobalScale)) / 2.0f)));
    }

    public float getLimitY() {
        return this.rectY - Math.max(0.0f, (float) Math.ceil((double) (((((float) getHeight()) - (((float) this.bitmapHeight) * this.bitmapGlobalScale)) + ((float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0.0f))) / 2.0f)));
    }

    public float getLimitWidth() {
        return ((((float) (getWidth() - AndroidUtilities.dp(14.0f))) - this.rectX) - ((float) ((int) Math.max(0.0d, Math.ceil((double) ((((float) (getWidth() - AndroidUtilities.dp(28.0f))) - (((float) this.bitmapWidth) * this.bitmapGlobalScale)) / 2.0f)))))) - this.rectSizeX;
    }

    public float getLimitHeight() {
        float additionalY = (float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0.0f);
        return (((((float) (getHeight() - AndroidUtilities.dp(14.0f))) - additionalY) - this.rectY) - ((float) ((int) Math.max(0.0d, Math.ceil((double) (((((float) (getHeight() - AndroidUtilities.dp(28.0f))) - (((float) this.bitmapHeight) * this.bitmapGlobalScale)) - additionalY) / 2.0f)))))) - this.rectSizeY;
    }

    public Bitmap getBitmap() {
        if (this.cropView != null) {
            return this.cropView.getResult();
        }
        return null;
    }

    public void setBitmapParams(float scale, float x, float y) {
        this.bitmapGlobalScale = scale;
        this.bitmapGlobalX = x;
        this.bitmapGlobalY = y;
    }

    public void startAnimationRunnable() {
        if (this.animationRunnable == null) {
            this.animationRunnable = new C12323();
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

    public void setAnimationProgress(float animationProgress) {
        if (this.animationStartValues != null) {
            if (animationProgress == 1.0f) {
                this.rectX = this.animationEndValues.left;
                this.rectY = this.animationEndValues.top;
                this.rectSizeX = this.animationEndValues.right;
                this.rectSizeY = this.animationEndValues.bottom;
                this.animationStartValues = null;
                this.animationEndValues = null;
            } else {
                this.rectX = this.animationStartValues.left + ((this.animationEndValues.left - this.animationStartValues.left) * animationProgress);
                this.rectY = this.animationStartValues.top + ((this.animationEndValues.top - this.animationStartValues.top) * animationProgress);
                this.rectSizeX = this.animationStartValues.right + ((this.animationEndValues.right - this.animationStartValues.right) * animationProgress);
                this.rectSizeY = this.animationStartValues.bottom + ((this.animationEndValues.bottom - this.animationStartValues.bottom) * animationProgress);
            }
            invalidate();
        }
    }

    public void moveToFill(boolean animated) {
        float scaleToX = ((float) this.bitmapWidth) / this.rectSizeX;
        float scaleToY = ((float) this.bitmapHeight) / this.rectSizeY;
        float scaleTo = scaleToX > scaleToY ? scaleToY : scaleToX;
        if (scaleTo > 1.0f && r0.bitmapGlobalScale * scaleTo > 3.0f) {
            scaleTo = 3.0f / r0.bitmapGlobalScale;
        } else if (scaleTo < 1.0f && r0.bitmapGlobalScale * scaleTo < 1.0f) {
            scaleTo = 1.0f / r0.bitmapGlobalScale;
        }
        float newSizeX = r0.rectSizeX * scaleTo;
        float newSizeY = r0.rectSizeY * scaleTo;
        float additionalY = (float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0.0f);
        float newX = (((float) getWidth()) - newSizeX) / 2.0f;
        float newY = ((((float) getHeight()) - newSizeY) + additionalY) / 2.0f;
        r0.animationStartValues = new RectF(r0.rectX, r0.rectY, r0.rectSizeX, r0.rectSizeY);
        r0.animationEndValues = new RectF(newX, newY, newSizeX, newSizeY);
        r0.delegate.needMoveImageTo(((((float) (getWidth() / 2)) * (scaleTo - 1.0f)) + newX) + ((r0.bitmapGlobalX - r0.rectX) * scaleTo), ((((((float) getHeight()) + additionalY) / 2.0f) * (scaleTo - 1.0f)) + newY) + ((r0.bitmapGlobalY - r0.rectY) * scaleTo), r0.bitmapGlobalScale * scaleTo, animated);
    }

    public void setDelegate(PhotoCropViewDelegate delegate) {
        this.delegate = delegate;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Bitmap newBitmap = this.delegate.getBitmap();
        if (newBitmap != null) {
            this.bitmapToEdit = newBitmap;
        }
        if (this.cropView != null) {
            this.cropView.updateLayout();
        }
    }
}
