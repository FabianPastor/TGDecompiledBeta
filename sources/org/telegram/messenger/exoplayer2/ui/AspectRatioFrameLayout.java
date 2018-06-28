package org.telegram.messenger.exoplayer2.ui;

import android.content.Context;
import android.graphics.Matrix;
import android.view.TextureView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AspectRatioFrameLayout extends FrameLayout {
    private static final float MAX_ASPECT_RATIO_DEFORMATION_FRACTION = 0.01f;
    public static final int RESIZE_MODE_FILL = 3;
    public static final int RESIZE_MODE_FIT = 0;
    public static final int RESIZE_MODE_FIXED_HEIGHT = 2;
    public static final int RESIZE_MODE_FIXED_WIDTH = 1;
    public static final int RESIZE_MODE_ZOOM = 4;
    private AspectRatioListener aspectRatioListener;
    private final AspectRatioUpdateDispatcher aspectRatioUpdateDispatcher = new AspectRatioUpdateDispatcher();
    private boolean drawingReady;
    private Matrix matrix = new Matrix();
    private int resizeMode = 0;
    private int rotation;
    private float videoAspectRatio;

    public interface AspectRatioListener {
        void onAspectRatioUpdated(float f, float f2, boolean z);
    }

    private final class AspectRatioUpdateDispatcher implements Runnable {
        private boolean aspectRatioMismatch;
        private boolean isScheduled;
        private float naturalAspectRatio;
        private float targetAspectRatio;

        private AspectRatioUpdateDispatcher() {
        }

        public void scheduleUpdate(float targetAspectRatio, float naturalAspectRatio, boolean aspectRatioMismatch) {
            this.targetAspectRatio = targetAspectRatio;
            this.naturalAspectRatio = naturalAspectRatio;
            this.aspectRatioMismatch = aspectRatioMismatch;
            if (!this.isScheduled) {
                this.isScheduled = true;
                AspectRatioFrameLayout.this.post(this);
            }
        }

        public void run() {
            this.isScheduled = false;
            if (AspectRatioFrameLayout.this.aspectRatioListener != null) {
                AspectRatioFrameLayout.this.aspectRatioListener.onAspectRatioUpdated(this.targetAspectRatio, this.naturalAspectRatio, this.aspectRatioMismatch);
            }
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ResizeMode {
    }

    public AspectRatioFrameLayout(Context context) {
        super(context);
    }

    public boolean isDrawingReady() {
        return this.drawingReady;
    }

    public void setAspectRatio(float widthHeightRatio, int rotation) {
        if (this.videoAspectRatio != widthHeightRatio) {
            this.videoAspectRatio = widthHeightRatio;
            this.rotation = rotation;
            requestLayout();
        }
    }

    public void setAspectRatioListener(AspectRatioListener listener) {
        this.aspectRatioListener = listener;
    }

    public int getResizeMode() {
        return this.resizeMode;
    }

    public void setResizeMode(int resizeMode) {
        if (this.resizeMode != resizeMode) {
            this.resizeMode = resizeMode;
            requestLayout();
        }
    }

    public void setDrawingReady(boolean value) {
        if (this.drawingReady != value) {
            this.drawingReady = value;
        }
    }

    public float getAspectRatio() {
        return this.videoAspectRatio;
    }

    public int getVideoRotation() {
        return this.rotation;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.videoAspectRatio > 0.0f) {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            float viewAspectRatio = ((float) width) / ((float) height);
            float aspectDeformation = (this.videoAspectRatio / viewAspectRatio) - 1.0f;
            if (Math.abs(aspectDeformation) <= MAX_ASPECT_RATIO_DEFORMATION_FRACTION) {
                this.aspectRatioUpdateDispatcher.scheduleUpdate(this.videoAspectRatio, viewAspectRatio, false);
                return;
            }
            switch (this.resizeMode) {
                case 0:
                    if (aspectDeformation <= 0.0f) {
                        width = (int) (((float) height) * this.videoAspectRatio);
                        break;
                    } else {
                        height = (int) (((float) width) / this.videoAspectRatio);
                        break;
                    }
                case 1:
                    height = (int) (((float) width) / this.videoAspectRatio);
                    break;
                case 2:
                    width = (int) (((float) height) * this.videoAspectRatio);
                    break;
                case 4:
                    if (aspectDeformation <= 0.0f) {
                        height = (int) (((float) width) / this.videoAspectRatio);
                        break;
                    } else {
                        width = (int) (((float) height) * this.videoAspectRatio);
                        break;
                    }
            }
            this.aspectRatioUpdateDispatcher.scheduleUpdate(this.videoAspectRatio, viewAspectRatio, true);
            super.onMeasure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height, NUM));
            int count = getChildCount();
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                if (child instanceof TextureView) {
                    this.matrix.reset();
                    int px = getWidth() / 2;
                    int py = getHeight() / 2;
                    this.matrix.postRotate((float) this.rotation, (float) px, (float) py);
                    if (this.rotation == 90 || this.rotation == 270) {
                        float ratio = ((float) getHeight()) / ((float) getWidth());
                        this.matrix.postScale(1.0f / ratio, ratio, (float) px, (float) py);
                    }
                    ((TextureView) child).setTransform(this.matrix);
                    return;
                }
            }
        }
    }
}
