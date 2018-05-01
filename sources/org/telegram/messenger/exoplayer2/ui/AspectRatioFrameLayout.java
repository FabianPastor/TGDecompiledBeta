package org.telegram.messenger.exoplayer2.ui;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
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
    private boolean drawingReady;
    private Matrix matrix;
    private int resizeMode;
    private int rotation;
    private float videoAspectRatio;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ResizeMode {
    }

    public AspectRatioFrameLayout(Context context) {
        this(context, null);
    }

    public AspectRatioFrameLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.matrix = new Matrix();
        this.resizeMode = null;
    }

    public boolean isDrawingReady() {
        return this.drawingReady;
    }

    public void setAspectRatio(float f, int i) {
        if (this.videoAspectRatio != f || this.rotation != i) {
            this.videoAspectRatio = f;
            this.rotation = i;
            requestLayout();
        }
    }

    public int getResizeMode() {
        return this.resizeMode;
    }

    public void setResizeMode(int i) {
        if (this.resizeMode != i) {
            this.resizeMode = i;
            requestLayout();
        }
    }

    public void setDrawingReady(boolean z) {
        if (this.drawingReady != z) {
            this.drawingReady = z;
        }
    }

    public float getAspectRatio() {
        return this.videoAspectRatio;
    }

    public int getVideoRotation() {
        return this.rotation;
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.resizeMode != 3) {
            if (this.videoAspectRatio > 0) {
                i = getMeasuredWidth();
                int measuredHeight = getMeasuredHeight();
                float f = (float) i;
                float f2 = (float) measuredHeight;
                float f3 = (this.videoAspectRatio / (f / f2)) - 1.0f;
                if (Math.abs(f3) > MAX_ASPECT_RATIO_DEFORMATION_FRACTION) {
                    int i3 = this.resizeMode;
                    if (i3 != 4) {
                        switch (i3) {
                            case 1:
                                measuredHeight = (int) (f / this.videoAspectRatio);
                                break;
                            case 2:
                                i = (int) (f2 * this.videoAspectRatio);
                                break;
                            default:
                                if (f3 <= 0.0f) {
                                    i = (int) (f2 * this.videoAspectRatio);
                                    break;
                                } else {
                                    measuredHeight = (int) (f / this.videoAspectRatio);
                                    break;
                                }
                        }
                    } else if (f3 > 0.0f) {
                        i = (int) (f2 * this.videoAspectRatio);
                    } else {
                        measuredHeight = (int) (f / this.videoAspectRatio);
                    }
                    super.onMeasure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(measuredHeight, NUM));
                    i = getChildCount();
                    for (i2 = 0; i2 < i; i2++) {
                        View childAt = getChildAt(i2);
                        if (childAt instanceof TextureView) {
                            this.matrix.reset();
                            i = (float) (getWidth() / 2);
                            i2 = (float) (getHeight() / 2);
                            this.matrix.postRotate((float) this.rotation, i, i2);
                            if (this.rotation == 90 || this.rotation == 270) {
                                f = ((float) getHeight()) / ((float) getWidth());
                                this.matrix.postScale(1.0f / f, f, i, i2);
                            }
                            ((TextureView) childAt).setTransform(this.matrix);
                        }
                    }
                }
            }
        }
    }
}
