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

    public AspectRatioFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.matrix = new Matrix();
        this.resizeMode = 0;
    }

    public boolean isDrawingReady() {
        return this.drawingReady;
    }

    public void setAspectRatio(float widthHeightRatio, int rotation) {
        if (this.videoAspectRatio != widthHeightRatio || this.rotation != rotation) {
            this.videoAspectRatio = widthHeightRatio;
            this.rotation = rotation;
            requestLayout();
        }
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
        if (this.resizeMode != 3) {
            if (r0.videoAspectRatio > 0.0f) {
                int width = getMeasuredWidth();
                int height = getMeasuredHeight();
                float aspectDeformation = (r0.videoAspectRatio / (((float) width) / ((float) height))) - 1.0f;
                if (Math.abs(aspectDeformation) > MAX_ASPECT_RATIO_DEFORMATION_FRACTION) {
                    int i = r0.resizeMode;
                    if (i != 4) {
                        switch (i) {
                            case 1:
                                height = (int) (((float) width) / r0.videoAspectRatio);
                                break;
                            case 2:
                                width = (int) (((float) height) * r0.videoAspectRatio);
                                break;
                            default:
                                if (aspectDeformation <= 0.0f) {
                                    width = (int) (((float) height) * r0.videoAspectRatio);
                                    break;
                                } else {
                                    height = (int) (((float) width) / r0.videoAspectRatio);
                                    break;
                                }
                        }
                    } else if (aspectDeformation > 0.0f) {
                        width = (int) (((float) height) * r0.videoAspectRatio);
                    } else {
                        height = (int) (((float) width) / r0.videoAspectRatio);
                    }
                    super.onMeasure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height, NUM));
                    int count = getChildCount();
                    for (i = 0; i < count; i++) {
                        View child = getChildAt(i);
                        if (child instanceof TextureView) {
                            r0.matrix.reset();
                            int px = getWidth() / 2;
                            int py = getHeight() / 2;
                            r0.matrix.postRotate((float) r0.rotation, (float) px, (float) py);
                            if (r0.rotation == 90 || r0.rotation == 270) {
                                float ratio = ((float) getHeight()) / ((float) getWidth());
                                r0.matrix.postScale(1.0f / ratio, ratio, (float) px, (float) py);
                            }
                            ((TextureView) child).setTransform(r0.matrix);
                        }
                    }
                }
            }
        }
    }
}
