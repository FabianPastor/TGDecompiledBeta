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

    public VideoForwardDrawable() {
        this.paint.setColor(-1);
        this.path1.reset();
        for (int a = 0; a < playPath.length / 2; a++) {
            if (a == 0) {
                this.path1.moveTo((float) AndroidUtilities.dp((float) playPath[a * 2]), (float) AndroidUtilities.dp((float) playPath[(a * 2) + 1]));
            } else {
                this.path1.lineTo((float) AndroidUtilities.dp((float) playPath[a * 2]), (float) AndroidUtilities.dp((float) playPath[(a * 2) + 1]));
            }
        }
        this.path1.close();
    }

    public boolean isAnimating() {
        return this.animating;
    }

    public void startAnimation() {
        this.animating = true;
        this.animationProgress = 0.0f;
        invalidateSelf();
    }

    public void setLeftSide(boolean value) {
        if (this.leftSide != value || this.animationProgress < 1.0f) {
            this.leftSide = value;
            startAnimation();
        }
    }

    public void setDelegate(VideoForwardDrawableDelegate videoForwardDrawableDelegate) {
        this.delegate = videoForwardDrawableDelegate;
    }

    public void setAlpha(int alpha) {
        this.paint.setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
    }

    public void setColor(int value) {
        this.paint.setColor(value);
    }

    public int getOpacity() {
        return -2;
    }

    public void draw(Canvas canvas) {
        float progress;
        Rect rect = getBounds();
        int x = rect.left + ((rect.width() - getIntrinsicWidth()) / 2);
        int y = rect.top + ((rect.height() - getIntrinsicHeight()) / 2);
        if (this.leftSide) {
            x -= (rect.width() / 4) - AndroidUtilities.dp(16.0f);
        } else {
            x += (rect.width() / 4) + AndroidUtilities.dp(16.0f);
        }
        canvas.save();
        canvas.clipRect(rect.left, rect.top, rect.right, rect.bottom);
        if (this.animationProgress <= 0.7f) {
            this.paint.setAlpha((int) (80.0f * Math.min(1.0f, this.animationProgress / 0.3f)));
        } else {
            this.paint.setAlpha((int) (80.0f * (1.0f - ((this.animationProgress - 0.7f) / 0.3f))));
        }
        canvas.drawCircle((float) (((this.leftSide ? -1 : 1) * (Math.max(rect.width(), rect.height()) / 4)) + x), (float) (AndroidUtilities.dp(16.0f) + y), (float) (Math.max(rect.width(), rect.height()) / 2), this.paint);
        canvas.restore();
        canvas.save();
        if (this.leftSide) {
            canvas.rotate(180.0f, (float) x, (float) ((getIntrinsicHeight() / 2) + y));
        }
        canvas.translate((float) x, (float) y);
        if (this.animationProgress <= 0.6f) {
            if (this.animationProgress < 0.4f) {
                this.paint.setAlpha(Math.min(255, (int) ((255.0f * this.animationProgress) / 0.2f)));
            } else {
                this.paint.setAlpha((int) (255.0f * (1.0f - ((this.animationProgress - 0.4f) / 0.2f))));
            }
            canvas.drawPath(this.path1, this.paint);
        }
        canvas.translate((float) AndroidUtilities.dp(18.0f), 0.0f);
        if (this.animationProgress >= 0.2f && this.animationProgress <= 0.8f) {
            progress = this.animationProgress - 0.2f;
            if (progress < 0.4f) {
                this.paint.setAlpha(Math.min(255, (int) ((255.0f * progress) / 0.2f)));
            } else {
                this.paint.setAlpha((int) (255.0f * (1.0f - ((progress - 0.4f) / 0.2f))));
            }
            canvas.drawPath(this.path1, this.paint);
        }
        canvas.translate((float) AndroidUtilities.dp(18.0f), 0.0f);
        if (this.animationProgress >= 0.4f && this.animationProgress <= 1.0f) {
            progress = this.animationProgress - 0.4f;
            if (progress < 0.4f) {
                this.paint.setAlpha(Math.min(255, (int) ((255.0f * progress) / 0.2f)));
            } else {
                this.paint.setAlpha((int) (255.0f * (1.0f - ((progress - 0.4f) / 0.2f))));
            }
            canvas.drawPath(this.path1, this.paint);
        }
        canvas.restore();
        if (this.animating) {
            long newTime = System.currentTimeMillis();
            long dt = newTime - this.lastAnimationTime;
            if (dt > 17) {
                dt = 17;
            }
            this.lastAnimationTime = newTime;
            if (this.animationProgress < 1.0f) {
                this.animationProgress += ((float) dt) / 800.0f;
                if (this.animationProgress >= 1.0f) {
                    this.animationProgress = 0.0f;
                    this.animating = false;
                    if (this.delegate != null) {
                        this.delegate.onAnimationEnd();
                    }
                }
                if (this.delegate != null) {
                    this.delegate.invalidate();
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
