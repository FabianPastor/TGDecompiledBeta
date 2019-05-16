package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;

public class VideoForwardDrawable extends Drawable {
    private static final int[] playPath = new int[]{10, 7, 26, 16, 10, 25};
    private boolean animating;
    private float animationProgress;
    private VideoForwardDrawableDelegate delegate;
    private long lastAnimationTime;
    private boolean leftSide;
    private Paint paint = new Paint(1);
    private Path path1 = new Path();

    public interface VideoForwardDrawableDelegate {
        void invalidate();

        void onAnimationEnd();
    }

    public int getOpacity() {
        return -2;
    }

    public VideoForwardDrawable() {
        this.paint.setColor(-1);
        this.path1.reset();
        int i = 0;
        while (true) {
            int[] iArr = playPath;
            if (i < iArr.length / 2) {
                int i2;
                if (i == 0) {
                    i2 = i * 2;
                    this.path1.moveTo((float) AndroidUtilities.dp((float) iArr[i2]), (float) AndroidUtilities.dp((float) playPath[i2 + 1]));
                } else {
                    i2 = i * 2;
                    this.path1.lineTo((float) AndroidUtilities.dp((float) iArr[i2]), (float) AndroidUtilities.dp((float) playPath[i2 + 1]));
                }
                i++;
            } else {
                this.path1.close();
                return;
            }
        }
    }

    public boolean isAnimating() {
        return this.animating;
    }

    public void startAnimation() {
        this.animating = true;
        this.animationProgress = 0.0f;
        invalidateSelf();
    }

    public void setLeftSide(boolean z) {
        if (this.leftSide != z || this.animationProgress < 1.0f) {
            this.leftSide = z;
            startAnimation();
        }
    }

    public void setDelegate(VideoForwardDrawableDelegate videoForwardDrawableDelegate) {
        this.delegate = videoForwardDrawableDelegate;
    }

    public void setAlpha(int i) {
        this.paint.setAlpha(i);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
    }

    public void setColor(int i) {
        this.paint.setColor(i);
    }

    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        int width = bounds.left + ((bounds.width() - getIntrinsicWidth()) / 2);
        int height = bounds.top + ((bounds.height() - getIntrinsicHeight()) / 2);
        if (this.leftSide) {
            width -= (bounds.width() / 4) - AndroidUtilities.dp(16.0f);
        } else {
            width += (bounds.width() / 4) + AndroidUtilities.dp(16.0f);
        }
        canvas.save();
        canvas.clipRect(bounds.left, bounds.top, bounds.right, bounds.bottom);
        float f = this.animationProgress;
        if (f <= 0.7f) {
            this.paint.setAlpha((int) (Math.min(1.0f, f / 0.3f) * 80.0f));
        } else {
            this.paint.setAlpha((int) ((1.0f - ((f - 0.7f) / 0.3f)) * 80.0f));
        }
        canvas.drawCircle((float) (((Math.max(bounds.width(), bounds.height()) / 4) * (this.leftSide ? -1 : 1)) + width), (float) (AndroidUtilities.dp(16.0f) + height), (float) (Math.max(bounds.width(), bounds.height()) / 2), this.paint);
        canvas.restore();
        canvas.save();
        if (this.leftSide) {
            canvas.rotate(180.0f, (float) width, (float) ((getIntrinsicHeight() / 2) + height));
        }
        canvas.translate((float) width, (float) height);
        float f2 = this.animationProgress;
        if (f2 <= 0.6f) {
            if (f2 < 0.4f) {
                this.paint.setAlpha(Math.min(255, (int) ((f2 * 255.0f) / 0.2f)));
            } else {
                this.paint.setAlpha((int) ((1.0f - ((f2 - 0.4f) / 0.2f)) * 255.0f));
            }
            canvas.drawPath(this.path1, this.paint);
        }
        canvas.translate((float) AndroidUtilities.dp(18.0f), 0.0f);
        float f3 = this.animationProgress;
        if (f3 >= 0.2f && f3 <= 0.8f) {
            f3 -= 0.2f;
            if (f3 < 0.4f) {
                this.paint.setAlpha(Math.min(255, (int) ((f3 * 255.0f) / 0.2f)));
            } else {
                this.paint.setAlpha((int) ((1.0f - ((f3 - 0.4f) / 0.2f)) * 255.0f));
            }
            canvas.drawPath(this.path1, this.paint);
        }
        canvas.translate((float) AndroidUtilities.dp(18.0f), 0.0f);
        f2 = this.animationProgress;
        if (f2 >= 0.4f && f2 <= 1.0f) {
            f2 -= 0.4f;
            if (f2 < 0.4f) {
                this.paint.setAlpha(Math.min(255, (int) ((f2 * 255.0f) / 0.2f)));
            } else {
                this.paint.setAlpha((int) ((1.0f - ((f2 - 0.4f) / 0.2f)) * 255.0f));
            }
            canvas.drawPath(this.path1, this.paint);
        }
        canvas.restore();
        if (this.animating) {
            long currentTimeMillis = System.currentTimeMillis();
            long j = currentTimeMillis - this.lastAnimationTime;
            if (j > 17) {
                j = 17;
            }
            this.lastAnimationTime = currentTimeMillis;
            float f4 = this.animationProgress;
            if (f4 < 1.0f) {
                VideoForwardDrawableDelegate videoForwardDrawableDelegate;
                this.animationProgress = f4 + (((float) j) / 800.0f);
                if (this.animationProgress >= 1.0f) {
                    this.animationProgress = 0.0f;
                    this.animating = false;
                    videoForwardDrawableDelegate = this.delegate;
                    if (videoForwardDrawableDelegate != null) {
                        videoForwardDrawableDelegate.onAnimationEnd();
                    }
                }
                videoForwardDrawableDelegate = this.delegate;
                if (videoForwardDrawableDelegate != null) {
                    videoForwardDrawableDelegate.invalidate();
                } else {
                    invalidateSelf();
                }
            }
        }
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(32.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(32.0f);
    }

    public int getMinimumWidth() {
        return AndroidUtilities.dp(32.0f);
    }

    public int getMinimumHeight() {
        return AndroidUtilities.dp(32.0f);
    }
}
