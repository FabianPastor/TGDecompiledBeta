package org.telegram.messenger.exoplayer;

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
    private Matrix matrix = new Matrix();
    private int rotation;
    private float videoAspectRatio;

    public AspectRatioFrameLayout(Context context) {
        super(context);
    }

    public AspectRatioFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAspectRatio(float widthHeightRatio, int rotation) {
        if (this.videoAspectRatio != widthHeightRatio || this.rotation != rotation) {
            this.videoAspectRatio = widthHeightRatio;
            this.rotation = rotation;
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
                if (aspectDeformation > 0.0f) {
                    height = (int) (((float) width) / this.videoAspectRatio);
                } else {
                    width = (int) (((float) height) * this.videoAspectRatio);
                }
                super.onMeasure(MeasureSpec.makeMeasureSpec(width, C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(height, C.ENCODING_PCM_32BIT));
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
