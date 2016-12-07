package org.telegram.messenger.exoplayer2.ui;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import org.telegram.messenger.volley.DefaultRetryPolicy;

public final class AspectRatioFrameLayout extends FrameLayout {
    private static final float MAX_ASPECT_RATIO_DEFORMATION_FRACTION = 0.01f;
    public static final int RESIZE_MODE_FIT = 0;
    public static final int RESIZE_MODE_FIXED_HEIGHT = 2;
    public static final int RESIZE_MODE_FIXED_WIDTH = 1;
    private Matrix matrix;
    private int resizeMode;
    private int rotation;
    private float videoAspectRatio;

    public AspectRatioFrameLayout(Context context) {
        this(context, null);
    }

    public AspectRatioFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.matrix = new Matrix();
        this.resizeMode = 0;
    }

    public void setAspectRatio(float widthHeightRatio, int rotation) {
        if (this.videoAspectRatio != widthHeightRatio || this.rotation != rotation) {
            this.videoAspectRatio = widthHeightRatio;
            this.rotation = rotation;
            requestLayout();
        }
    }

    public void setResizeMode(int resizeMode) {
        if (this.resizeMode != resizeMode) {
            this.resizeMode = resizeMode;
            requestLayout();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.videoAspectRatio != 0.0f) {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            float aspectDeformation = (this.videoAspectRatio / (((float) width) / ((float) height))) - DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            if (Math.abs(aspectDeformation) > MAX_ASPECT_RATIO_DEFORMATION_FRACTION) {
                switch (this.resizeMode) {
                    case 1:
                        height = (int) (((float) width) / this.videoAspectRatio);
                        break;
                    case 2:
                        width = (int) (((float) height) * this.videoAspectRatio);
                        break;
                    default:
                        if (aspectDeformation <= 0.0f) {
                            width = (int) (((float) height) * this.videoAspectRatio);
                            break;
                        } else {
                            height = (int) (((float) width) / this.videoAspectRatio);
                            break;
                        }
                }
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
                            this.matrix.postScale(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT / ratio, ratio, (float) px, (float) py);
                        }
                        ((TextureView) child).setTransform(this.matrix);
                        return;
                    }
                }
            }
        }
    }
}
