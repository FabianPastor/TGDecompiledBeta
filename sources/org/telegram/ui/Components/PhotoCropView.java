package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.Crop.CropRotationWheel;
import org.telegram.ui.Components.Crop.CropRotationWheel.RotationWheelListener;
import org.telegram.ui.Components.Crop.CropView;
import org.telegram.ui.Components.Crop.CropView.CropViewListener;

public class PhotoCropView extends FrameLayout {
    private CropView cropView = new CropView(getContext());
    private PhotoCropViewDelegate delegate;
    private boolean showOnSetBitmap;
    private CropRotationWheel wheelView;

    public interface PhotoCropViewDelegate {
        void onChange(boolean z);
    }

    public PhotoCropView(Context context) {
        super(context);
        this.cropView.setListener(new CropViewListener() {
            public void onChange(boolean z) {
                if (PhotoCropView.this.delegate != null) {
                    PhotoCropView.this.delegate.onChange(z);
                }
            }

            public void onAspectLock(boolean z) {
                PhotoCropView.this.wheelView.setAspectLock(z);
            }
        });
        this.cropView.setBottomPadding((float) AndroidUtilities.dp(64.0f));
        addView(this.cropView);
        this.wheelView = new CropRotationWheel(getContext());
        this.wheelView.setListener(new RotationWheelListener() {
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
                PhotoCropView.this.rotate();
            }
        });
        addView(this.wheelView, LayoutHelper.createFrame(-1, -2.0f, 81, 0.0f, 0.0f, 0.0f, 0.0f));
    }

    public void rotate() {
        CropRotationWheel cropRotationWheel = this.wheelView;
        if (cropRotationWheel != null) {
            cropRotationWheel.reset();
        }
        this.cropView.rotate90Degrees();
    }

    public void setBitmap(Bitmap bitmap, int i, boolean z, boolean z2) {
        requestLayout();
        this.cropView.setBitmap(bitmap, i, z, z2);
        i = 0;
        if (this.showOnSetBitmap) {
            this.showOnSetBitmap = false;
            this.cropView.show();
        }
        this.wheelView.setFreeform(z);
        this.wheelView.reset();
        CropRotationWheel cropRotationWheel = this.wheelView;
        if (!z) {
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

    public void setAspectRatio(float f) {
        this.cropView.setAspectRatio(f);
    }

    public void hideBackView() {
        this.cropView.hideBackView();
    }

    public void showBackView() {
        this.cropView.showBackView();
    }

    public void setFreeform(boolean z) {
        this.cropView.setFreeform(z);
    }

    public void onAppeared() {
        CropView cropView = this.cropView;
        if (cropView != null) {
            cropView.show();
        } else {
            this.showOnSetBitmap = true;
        }
    }

    public void onDisappear() {
        CropView cropView = this.cropView;
        if (cropView != null) {
            cropView.hide();
        }
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

    public Bitmap getBitmap() {
        CropView cropView = this.cropView;
        return cropView != null ? cropView.getResult() : null;
    }

    public void setDelegate(PhotoCropViewDelegate photoCropViewDelegate) {
        this.delegate = photoCropViewDelegate;
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        CropView cropView = this.cropView;
        if (cropView != null) {
            cropView.updateLayout();
        }
    }
}
