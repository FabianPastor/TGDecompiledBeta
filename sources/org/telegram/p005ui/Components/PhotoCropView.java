package org.telegram.p005ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.p005ui.Components.Crop.CropRotationWheel;
import org.telegram.p005ui.Components.Crop.CropRotationWheel.RotationWheelListener;
import org.telegram.p005ui.Components.Crop.CropView;
import org.telegram.p005ui.Components.Crop.CropView.CropViewListener;

/* renamed from: org.telegram.ui.Components.PhotoCropView */
public class PhotoCropView extends FrameLayout {
    private CropView cropView = new CropView(getContext());
    private PhotoCropViewDelegate delegate;
    private boolean showOnSetBitmap;
    private CropRotationWheel wheelView;

    /* renamed from: org.telegram.ui.Components.PhotoCropView$1 */
    class CLASSNAME implements CropViewListener {
        CLASSNAME() {
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
    class CLASSNAME implements RotationWheelListener {
        CLASSNAME() {
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
            PhotoCropView.this.rotate();
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoCropView$PhotoCropViewDelegate */
    public interface PhotoCropViewDelegate {
        void onChange(boolean z);
    }

    public PhotoCropView(Context context) {
        super(context);
        this.cropView.setListener(new CLASSNAME());
        this.cropView.setBottomPadding((float) AndroidUtilities.m9dp(64.0f));
        addView(this.cropView);
        this.wheelView = new CropRotationWheel(getContext());
        this.wheelView.setListener(new CLASSNAME());
        addView(this.wheelView, LayoutHelper.createFrame(-1, -2.0f, 81, 0.0f, 0.0f, 0.0f, 0.0f));
    }

    public void rotate() {
        if (this.wheelView != null) {
            this.wheelView.reset();
        }
        this.cropView.rotate90Degrees();
    }

    public void setBitmap(Bitmap bitmap, int rotation, boolean freeform, boolean update) {
        int i = 0;
        requestLayout();
        this.cropView.setBitmap(bitmap, rotation, freeform, update);
        if (this.showOnSetBitmap) {
            this.showOnSetBitmap = false;
            this.cropView.show();
        }
        this.wheelView.setFreeform(freeform);
        this.wheelView.reset();
        CropRotationWheel cropRotationWheel = this.wheelView;
        if (!freeform) {
            i = 4;
        }
        cropRotationWheel.setVisibility(i);
    }

    public boolean isReady() {
        return this.cropView.isReady();
    }

    public void reset() {
        this.wheelView.reset();
        this.cropView.reset();
    }

    public void onAppear() {
        this.cropView.willShow();
    }

    public void setAspectRatio(float ratio) {
        this.cropView.setAspectRatio(ratio);
    }

    public void hideBackView() {
        this.cropView.hideBackView();
    }

    public void showBackView() {
        this.cropView.showBackView();
    }

    public void setFreeform(boolean freeform) {
        this.cropView.setFreeform(freeform);
    }

    public void onAppeared() {
        if (this.cropView != null) {
            this.cropView.show();
        } else {
            this.showOnSetBitmap = true;
        }
    }

    public void onDisappear() {
        if (this.cropView != null) {
            this.cropView.hide();
        }
    }

    public float getRectX() {
        return this.cropView.getCropLeft() - ((float) AndroidUtilities.m9dp(14.0f));
    }

    public float getRectY() {
        return (this.cropView.getCropTop() - ((float) AndroidUtilities.m9dp(14.0f))) - ((float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
    }

    public float getRectSizeX() {
        return this.cropView.getCropWidth();
    }

    public float getRectSizeY() {
        return this.cropView.getCropHeight();
    }

    public Bitmap getBitmap() {
        if (this.cropView != null) {
            return this.cropView.getResult();
        }
        return null;
    }

    public void setDelegate(PhotoCropViewDelegate delegate) {
        this.delegate = delegate;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.cropView != null) {
            this.cropView.updateLayout();
        }
    }
}
