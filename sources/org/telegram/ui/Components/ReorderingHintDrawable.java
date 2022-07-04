package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.animation.Interpolator;
import org.telegram.messenger.AndroidUtilities;

public class ReorderingHintDrawable extends Drawable {
    public static final int DURATION = 1500;
    private static final int DURATION_DELAY1 = 300;
    private static final int DURATION_DELAY2 = 300;
    private static final int DURATION_DELAY3 = 300;
    private static final int DURATION_DELAY4 = 100;
    private static final int DURATION_STAGE1 = 150;
    private static final int DURATION_STAGE2 = 200;
    private static final int DURATION_STAGE3 = 150;
    private final Interpolator interpolator = Easings.easeInOutSine;
    private final int intrinsicHeight = AndroidUtilities.dp(24.0f);
    private final int intrinsicWidth = AndroidUtilities.dp(24.0f);
    private final RectDrawable primaryRectDrawable;
    private float scaleX;
    private float scaleY;
    private final RectDrawable secondaryRectDrawable;
    private long startedTime = -1;
    private final Rect tempRect = new Rect();

    public ReorderingHintDrawable() {
        RectDrawable rectDrawable = new RectDrawable();
        this.primaryRectDrawable = rectDrawable;
        rectDrawable.setColor(-NUM);
        RectDrawable rectDrawable2 = new RectDrawable();
        this.secondaryRectDrawable = rectDrawable2;
        rectDrawable2.setColor(-NUM);
    }

    public void startAnimation() {
        this.startedTime = System.currentTimeMillis();
        invalidateSelf();
    }

    public void resetAnimation() {
        this.startedTime = -1;
        invalidateSelf();
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect bounds) {
        this.scaleX = ((float) bounds.width()) / ((float) this.intrinsicWidth);
        this.scaleY = ((float) bounds.height()) / ((float) this.intrinsicHeight);
    }

    public void draw(Canvas canvas) {
        if (this.startedTime > 0) {
            int passedTime = ((int) (System.currentTimeMillis() - this.startedTime)) - 300;
            if (passedTime < 0) {
                drawStage1(canvas, 0.0f);
            } else if (passedTime < 150) {
                drawStage1(canvas, ((float) passedTime) / 150.0f);
            } else {
                int passedTime2 = passedTime - 450;
                if (passedTime2 < 0) {
                    drawStage1(canvas, 1.0f);
                } else if (passedTime2 < 200) {
                    drawStage2(canvas, ((float) passedTime2) / 200.0f);
                } else {
                    int passedTime3 = passedTime2 - 500;
                    if (passedTime3 < 0) {
                        drawStage2(canvas, 1.0f);
                    } else if (passedTime3 < 150) {
                        drawStage3(canvas, ((float) passedTime3) / 150.0f);
                    } else {
                        drawStage3(canvas, 1.0f);
                        if (passedTime3 - 150 >= 100) {
                            this.startedTime = System.currentTimeMillis();
                        }
                    }
                }
            }
            invalidateSelf();
            return;
        }
        drawStage1(canvas, 0.0f);
    }

    private void drawStage1(Canvas canvas, float progress) {
        Rect bounds = getBounds();
        float progress2 = this.interpolator.getInterpolation(progress);
        this.tempRect.left = (int) (((float) AndroidUtilities.dp(2.0f)) * this.scaleX);
        this.tempRect.bottom = bounds.bottom - ((int) (((float) AndroidUtilities.dp(6.0f)) * this.scaleY));
        this.tempRect.right = bounds.right - this.tempRect.left;
        Rect rect = this.tempRect;
        rect.top = rect.bottom - ((int) (((float) AndroidUtilities.dp(4.0f)) * this.scaleY));
        this.secondaryRectDrawable.setBounds(this.tempRect);
        this.secondaryRectDrawable.draw(canvas);
        Rect rect2 = this.tempRect;
        int dp = AndroidUtilities.dp(12.0f);
        rect2.right = dp;
        rect2.left = dp;
        Rect rect3 = this.tempRect;
        int dp2 = AndroidUtilities.dp(8.0f);
        rect3.bottom = dp2;
        rect3.top = dp2;
        this.tempRect.inset(-AndroidUtilities.dp((float) AndroidUtilities.lerp(10, 11, progress2)), -AndroidUtilities.dp((float) AndroidUtilities.lerp(2, 3, progress2)));
        this.primaryRectDrawable.setBounds(this.tempRect);
        this.primaryRectDrawable.setAlpha(AndroidUtilities.lerp(128, 255, progress2));
        this.primaryRectDrawable.draw(canvas);
    }

    private void drawStage2(Canvas canvas, float progress) {
        Rect bounds = getBounds();
        float progress2 = this.interpolator.getInterpolation(progress);
        this.tempRect.left = (int) (((float) AndroidUtilities.dp(2.0f)) * this.scaleX);
        this.tempRect.bottom = bounds.bottom - ((int) (((float) AndroidUtilities.dp(6.0f)) * this.scaleY));
        this.tempRect.right = bounds.right - this.tempRect.left;
        Rect rect = this.tempRect;
        rect.top = rect.bottom - ((int) (((float) AndroidUtilities.dp(4.0f)) * this.scaleY));
        this.tempRect.offset(0, AndroidUtilities.dp((float) AndroidUtilities.lerp(0, -8, progress2)));
        this.secondaryRectDrawable.setBounds(this.tempRect);
        this.secondaryRectDrawable.draw(canvas);
        this.tempRect.left = (int) (AndroidUtilities.dpf2((float) AndroidUtilities.lerp(1, 2, progress2)) * this.scaleX);
        this.tempRect.top = (int) (AndroidUtilities.dpf2((float) AndroidUtilities.lerp(5, 6, progress2)) * this.scaleY);
        this.tempRect.right = bounds.right - this.tempRect.left;
        Rect rect2 = this.tempRect;
        rect2.bottom = rect2.top + ((int) (AndroidUtilities.dpf2((float) AndroidUtilities.lerp(6, 4, progress2)) * this.scaleY));
        this.tempRect.offset(0, AndroidUtilities.dp((float) AndroidUtilities.lerp(0, 8, progress2)));
        this.primaryRectDrawable.setBounds(this.tempRect);
        this.primaryRectDrawable.setAlpha(255);
        this.primaryRectDrawable.draw(canvas);
    }

    private void drawStage3(Canvas canvas, float progress) {
        Rect bounds = getBounds();
        float progress2 = this.interpolator.getInterpolation(progress);
        this.tempRect.left = (int) (((float) AndroidUtilities.dp(2.0f)) * this.scaleX);
        this.tempRect.bottom = bounds.bottom - ((int) (((float) AndroidUtilities.dp(6.0f)) * this.scaleY));
        this.tempRect.right = bounds.right - this.tempRect.left;
        Rect rect = this.tempRect;
        rect.top = rect.bottom - ((int) (((float) AndroidUtilities.dp(4.0f)) * this.scaleY));
        this.tempRect.offset(0, AndroidUtilities.dp(-8.0f));
        this.secondaryRectDrawable.setBounds(this.tempRect);
        this.secondaryRectDrawable.draw(canvas);
        this.tempRect.left = (int) (AndroidUtilities.dpf2(2.0f) * this.scaleX);
        this.tempRect.top = (int) (AndroidUtilities.dpf2(6.0f) * this.scaleY);
        this.tempRect.right = bounds.right - this.tempRect.left;
        Rect rect2 = this.tempRect;
        rect2.bottom = rect2.top + ((int) (AndroidUtilities.dpf2(4.0f) * this.scaleY));
        this.tempRect.offset(0, AndroidUtilities.dp(8.0f));
        this.primaryRectDrawable.setBounds(this.tempRect);
        this.primaryRectDrawable.setAlpha(AndroidUtilities.lerp(255, 128, progress2));
        this.primaryRectDrawable.draw(canvas);
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.primaryRectDrawable.setColorFilter(colorFilter);
        this.secondaryRectDrawable.setColorFilter(colorFilter);
        invalidateSelf();
    }

    public int getOpacity() {
        return -3;
    }

    public int getIntrinsicWidth() {
        return this.intrinsicWidth;
    }

    public int getIntrinsicHeight() {
        return this.intrinsicHeight;
    }

    protected static class RectDrawable extends Drawable {
        private final Paint paint = new Paint(1);
        private final RectF tempRect = new RectF();

        protected RectDrawable() {
        }

        public void draw(Canvas canvas) {
            this.tempRect.set(getBounds());
            float radius = this.tempRect.height() * 0.2f;
            canvas.drawRoundRect(this.tempRect, radius, radius, this.paint);
        }

        public void setColor(int color) {
            this.paint.setColor(color);
        }

        public void setAlpha(int alpha) {
            this.paint.setAlpha(alpha);
        }

        public void setColorFilter(ColorFilter colorFilter) {
            this.paint.setColorFilter(colorFilter);
        }

        public int getOpacity() {
            return -3;
        }
    }
}
