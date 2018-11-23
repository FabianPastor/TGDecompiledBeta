package org.telegram.p005ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;

/* renamed from: org.telegram.ui.Components.MediaActionDrawable */
public class MediaActionDrawable extends Drawable {
    public static final int ICON_ARROW = 2;
    public static final int ICON_CANCEL = 3;
    public static final int ICON_EMPTY = 5;
    public static final int ICON_NONE = 4;
    public static final int ICON_PAUSE = 1;
    public static final int ICON_PLAY = 0;
    private static final int[] arrowMiniPath1 = new int[]{11, 16, 15, 12, 15, 14, 20, 14, 20, 16};
    private static final int[] arrowMiniPath2 = new int[]{11, 16, 15, 20, 15, 18, 20, 18, 20, 16};
    private static final int[] arrowPath1 = new int[]{7, 16, 13, 11, 13, 14, 25, 14, 25, 16};
    private static final int[] arrowPath2 = new int[]{7, 16, 13, 21, 13, 18, 25, 18, 25, 16};
    private static final int arrowRotation = -90;
    private static final int[] cancelMiniPath1 = new int[]{12, 13, 12, 13, 13, 12, 20, 19, 19, 20};
    private static final int[] cancelMiniPath2 = new int[]{12, 19, 12, 19, 13, 20, 20, 13, 19, 12};
    private static final int[] cancelPath1 = new int[]{8, 10, 8, 10, 10, 8, 24, 22, 22, 24};
    private static final int[] cancelPath2 = new int[]{8, 22, 8, 22, 10, 24, 24, 10, 22, 8};
    private static final int cancelRotation = 0;
    private static final int[] pausePath1 = new int[]{8, 9, 8, 9, 24, 9, 24, 14, 8, 14};
    private static final int[] pausePath2 = new int[]{8, 23, 8, 23, 24, 23, 24, 18, 8, 18};
    private static final int pauseRotation = -90;
    private static final int[] playPath1 = new int[]{10, 7, 10, 7, 26, 16, 26, 16, 10, 16};
    private static final int[] playPath2 = new int[]{10, 25, 10, 25, 26, 16, 26, 16, 10, 16};
    private static final int playRotation = 0;
    private boolean animating;
    private float animationProgress;
    private int currentIcon;
    private MediaActionDrawableDelegate delegate;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private boolean isMini;
    private long lastAnimationTime;
    private int nextIcon;
    private Paint paint = new Paint(1);
    private Path path1 = new Path();
    private Path path2 = new Path();
    private float scale = 1.0f;

    /* renamed from: org.telegram.ui.Components.MediaActionDrawable$MediaActionDrawableDelegate */
    public interface MediaActionDrawableDelegate {
        void invalidate();
    }

    public MediaActionDrawable() {
        this.paint.setColor(-1);
    }

    public void setScale(float value) {
        this.scale = value;
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

    public int getColor() {
        return this.paint.getColor();
    }

    public void setMini(boolean value) {
        this.isMini = value;
    }

    public int getOpacity() {
        return -2;
    }

    public void setDelegate(MediaActionDrawableDelegate mediaActionDrawableDelegate) {
        this.delegate = mediaActionDrawableDelegate;
    }

    public boolean setIcon(int icon, boolean animated) {
        if (animated) {
            if (this.currentIcon == icon || this.nextIcon == icon) {
                return false;
            }
            this.animating = true;
            this.nextIcon = icon;
            this.animationProgress = 0.0f;
        } else if (this.currentIcon == icon) {
            return false;
        } else {
            this.animating = false;
            this.nextIcon = icon;
            this.currentIcon = icon;
            this.animationProgress = 1.0f;
        }
        if (this.delegate != null) {
            this.delegate.invalidate();
        } else {
            invalidateSelf();
        }
        return true;
    }

    public int getCurrentIcon() {
        return this.nextIcon;
    }

    public void draw(Canvas canvas) {
        if (this.currentIcon != 4 && this.currentIcon != 5) {
            int[] p1;
            int[] p2;
            int rotation1;
            int[] p3;
            int[] p4;
            int rotation2;
            this.path1.reset();
            this.path2.reset();
            switch (this.currentIcon) {
                case 0:
                    p1 = playPath1;
                    p2 = playPath2;
                    rotation1 = 0;
                    break;
                case 1:
                    p1 = pausePath1;
                    p2 = pausePath2;
                    rotation1 = -90;
                    break;
                case 2:
                    if (this.isMini) {
                        p1 = arrowMiniPath1;
                        p2 = arrowMiniPath2;
                    } else {
                        p1 = arrowPath1;
                        p2 = arrowPath2;
                    }
                    rotation1 = -90;
                    break;
                default:
                    if (this.isMini) {
                        p1 = cancelMiniPath1;
                        p2 = cancelMiniPath2;
                    } else {
                        p1 = cancelPath1;
                        p2 = cancelPath2;
                    }
                    rotation1 = 0;
                    break;
            }
            switch (this.nextIcon) {
                case 0:
                    p3 = playPath1;
                    p4 = playPath2;
                    rotation2 = 0;
                    break;
                case 1:
                    p3 = pausePath1;
                    p4 = pausePath2;
                    rotation2 = -90;
                    break;
                case 2:
                    if (this.isMini) {
                        p3 = arrowMiniPath1;
                        p4 = arrowMiniPath2;
                    } else {
                        p3 = arrowPath1;
                        p4 = arrowPath2;
                    }
                    rotation2 = -90;
                    break;
                default:
                    if (this.isMini) {
                        p3 = cancelMiniPath1;
                        p4 = cancelMiniPath2;
                    } else {
                        p3 = cancelPath1;
                        p4 = cancelPath2;
                    }
                    rotation2 = 0;
                    break;
            }
            float p = this.animating ? this.interpolator.getInterpolation(this.animationProgress) : 0.0f;
            for (int a = 0; a < 5; a++) {
                if (a == 0) {
                    this.path1.moveTo(((float) AndroidUtilities.m9dp(((float) p1[a * 2]) + (((float) (p3[a * 2] - p1[a * 2])) * p))) * this.scale, ((float) AndroidUtilities.m9dp(((float) p1[(a * 2) + 1]) + (((float) (p3[(a * 2) + 1] - p1[(a * 2) + 1])) * p))) * this.scale);
                    this.path2.moveTo(((float) AndroidUtilities.m9dp(((float) p2[a * 2]) + (((float) (p4[a * 2] - p2[a * 2])) * p))) * this.scale, ((float) AndroidUtilities.m9dp(((float) p2[(a * 2) + 1]) + (((float) (p4[(a * 2) + 1] - p2[(a * 2) + 1])) * p))) * this.scale);
                } else {
                    this.path1.lineTo(((float) AndroidUtilities.m9dp(((float) p1[a * 2]) + (((float) (p3[a * 2] - p1[a * 2])) * p))) * this.scale, ((float) AndroidUtilities.m9dp(((float) p1[(a * 2) + 1]) + (((float) (p3[(a * 2) + 1] - p1[(a * 2) + 1])) * p))) * this.scale);
                    this.path2.lineTo(((float) AndroidUtilities.m9dp(((float) p2[a * 2]) + (((float) (p4[a * 2] - p2[a * 2])) * p))) * this.scale, ((float) AndroidUtilities.m9dp(((float) p2[(a * 2) + 1]) + (((float) (p4[(a * 2) + 1] - p2[(a * 2) + 1])) * p))) * this.scale);
                }
            }
            this.path1.close();
            this.path2.close();
            Rect rect = getBounds();
            int x = rect.left + ((rect.width() - getIntrinsicWidth()) / 2);
            int y = rect.top + ((rect.height() - getIntrinsicHeight()) / 2);
            canvas.save();
            canvas.translate((float) x, (float) y);
            canvas.rotate(((float) rotation1) + (((float) (rotation2 - rotation1)) * p), (float) (getBounds().centerX() - x), (float) (getBounds().centerY() - y));
            canvas.drawPath(this.path1, this.paint);
            canvas.drawPath(this.path2, this.paint);
            canvas.restore();
            if (this.animating) {
                long newTime = System.currentTimeMillis();
                long dt = newTime - this.lastAnimationTime;
                if (dt > 17) {
                    dt = 17;
                }
                this.lastAnimationTime = newTime;
                if (this.animationProgress < 1.0f) {
                    this.animationProgress += ((float) dt) / 180.0f;
                    if (this.animationProgress >= 1.0f) {
                        this.currentIcon = this.nextIcon;
                        this.animationProgress = 0.0f;
                        this.animating = false;
                    }
                    if (this.delegate != null) {
                        this.delegate.invalidate();
                    } else {
                        invalidateSelf();
                    }
                }
            }
        }
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.m9dp(32.0f * this.scale);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.m9dp(32.0f * this.scale);
    }

    public int getMinimumWidth() {
        return AndroidUtilities.m9dp(32.0f * this.scale);
    }

    public int getMinimumHeight() {
        return AndroidUtilities.m9dp(32.0f * this.scale);
    }
}
