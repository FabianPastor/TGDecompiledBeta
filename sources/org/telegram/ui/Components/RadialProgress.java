package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class RadialProgress {
    private static DecelerateInterpolator decelerateInterpolator;
    private boolean alphaForMiniPrevious = true;
    private boolean alphaForPrevious = true;
    private float animatedAlphaValue = 1.0f;
    private float animatedProgressValue = 0.0f;
    private float animationProgressStart = 0.0f;
    private Drawable checkBackgroundDrawable;
    private CheckDrawable checkDrawable;
    private RectF cicleRect = new RectF();
    private Drawable currentDrawable;
    private Drawable currentMiniDrawable;
    private boolean currentMiniWithRound;
    private float currentProgress = 0.0f;
    private long currentProgressTime = 0;
    private boolean currentWithRound;
    private int diff = AndroidUtilities.dp(4.0f);
    private boolean drawMiniProgress;
    private boolean hideCurrentDrawable;
    private long lastUpdateTime = 0;
    private Bitmap miniDrawBitmap;
    private Canvas miniDrawCanvas;
    private Paint miniProgressBackgroundPaint;
    private Paint miniProgressPaint;
    private float overrideAlpha = 1.0f;
    private View parent;
    private boolean previousCheckDrawable;
    private Drawable previousDrawable;
    private Drawable previousMiniDrawable;
    private boolean previousMiniWithRound;
    private boolean previousWithRound;
    private int progressColor = -1;
    private Paint progressPaint;
    private RectF progressRect = new RectF();
    private float radOffset = 0.0f;

    private class CheckDrawable extends Drawable {
        private Paint paint = new Paint(1);
        private float progress;

        public CheckDrawable() {
            this.paint.setStyle(Style.STROKE);
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
            this.paint.setStrokeCap(Cap.ROUND);
            this.paint.setColor(-1);
        }

        public void resetProgress(boolean animated) {
            this.progress = animated ? 0.0f : 1.0f;
        }

        public boolean updateAnimation(long dt) {
            if (this.progress >= 1.0f) {
                return false;
            }
            this.progress += ((float) dt) / 700.0f;
            if (this.progress > 1.0f) {
                this.progress = 1.0f;
            }
            return true;
        }

        public void draw(Canvas canvas) {
            int x = getBounds().centerX() - AndroidUtilities.dp(12.0f);
            int y = getBounds().centerY() - AndroidUtilities.dp(6.0f);
            float f = 1.0f;
            if (this.progress != 1.0f) {
                f = RadialProgress.decelerateInterpolator.getInterpolation(this.progress);
            }
            float p = f;
            Canvas canvas2 = canvas;
            canvas2.drawLine((float) (AndroidUtilities.dp(7.0f) + x), (float) (((int) AndroidUtilities.dpf2(13.0f)) + y), (float) (x + ((int) (((float) AndroidUtilities.dp(7.0f)) - (((float) AndroidUtilities.dp(6.0f)) * p)))), (float) (y + ((int) (AndroidUtilities.dpf2(13.0f) - (((float) AndroidUtilities.dp(6.0f)) * p)))), this.paint);
            Canvas canvas3 = canvas;
            canvas3.drawLine((float) (((int) AndroidUtilities.dpf2(7.0f)) + x), (float) (((int) AndroidUtilities.dpf2(13.0f)) + y), (float) (x + ((int) (AndroidUtilities.dpf2(7.0f) + (((float) AndroidUtilities.dp(13.0f)) * p)))), (float) (y + ((int) (AndroidUtilities.dpf2(13.0f) - (((float) AndroidUtilities.dp(13.0f)) * p)))), this.paint);
        }

        public void setAlpha(int alpha) {
            this.paint.setAlpha(alpha);
        }

        public void setColorFilter(ColorFilter cf) {
            this.paint.setColorFilter(cf);
        }

        public int getOpacity() {
            return -2;
        }

        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(48.0f);
        }

        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(48.0f);
        }
    }

    public RadialProgress(View parentView) {
        if (decelerateInterpolator == null) {
            decelerateInterpolator = new DecelerateInterpolator();
        }
        this.progressPaint = new Paint(1);
        this.progressPaint.setStyle(Style.STROKE);
        this.progressPaint.setStrokeCap(Cap.ROUND);
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.miniProgressPaint = new Paint(1);
        this.miniProgressPaint.setStyle(Style.STROKE);
        this.miniProgressPaint.setStrokeCap(Cap.ROUND);
        this.miniProgressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.miniProgressBackgroundPaint = new Paint(1);
        this.parent = parentView;
    }

    public void setStrikeWidth(int width) {
        this.progressPaint.setStrokeWidth((float) width);
    }

    public void setProgressRect(int left, int top, int right, int bottom) {
        this.progressRect.set((float) left, (float) top, (float) right, (float) bottom);
    }

    public RectF getProgressRect() {
        return this.progressRect;
    }

    public void setAlphaForPrevious(boolean value) {
        this.alphaForPrevious = value;
    }

    public void setAlphaForMiniPrevious(boolean value) {
        this.alphaForMiniPrevious = value;
    }

    private void updateAnimation(boolean progress) {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (this.checkBackgroundDrawable != null && ((r0.currentDrawable == r0.checkBackgroundDrawable || r0.previousDrawable == r0.checkBackgroundDrawable) && r0.checkDrawable.updateAnimation(dt))) {
            invalidateParent();
        }
        if (progress) {
            if (r0.animatedProgressValue != 1.0f) {
                r0.radOffset += ((float) (360 * dt)) / 3000.0f;
                float progressDiff = r0.currentProgress - r0.animationProgressStart;
                if (progressDiff > 0.0f) {
                    r0.currentProgressTime += dt;
                    if (r0.currentProgressTime >= 300) {
                        r0.animatedProgressValue = r0.currentProgress;
                        r0.animationProgressStart = r0.currentProgress;
                        r0.currentProgressTime = 0;
                    } else {
                        r0.animatedProgressValue = r0.animationProgressStart + (decelerateInterpolator.getInterpolation(((float) r0.currentProgressTime) / 300.0f) * progressDiff);
                    }
                }
                invalidateParent();
            }
            if (r0.drawMiniProgress) {
                if (r0.animatedProgressValue >= 1.0f && r0.previousMiniDrawable != null) {
                    r0.animatedAlphaValue -= ((float) dt) / 200.0f;
                    if (r0.animatedAlphaValue <= 0.0f) {
                        r0.animatedAlphaValue = 0.0f;
                        r0.previousMiniDrawable = null;
                        r0.drawMiniProgress = r0.currentMiniDrawable != null;
                    }
                    invalidateParent();
                }
            } else if (r0.animatedProgressValue >= 1.0f && r0.previousDrawable != null) {
                r0.animatedAlphaValue -= ((float) dt) / 200.0f;
                if (r0.animatedAlphaValue <= 0.0f) {
                    r0.animatedAlphaValue = 0.0f;
                    r0.previousDrawable = null;
                }
                invalidateParent();
            }
        } else if (r0.drawMiniProgress) {
            if (r0.previousMiniDrawable != null) {
                r0.animatedAlphaValue -= ((float) dt) / 200.0f;
                if (r0.animatedAlphaValue <= 0.0f) {
                    r0.animatedAlphaValue = 0.0f;
                    r0.previousMiniDrawable = null;
                    r0.drawMiniProgress = r0.currentMiniDrawable != null;
                }
                invalidateParent();
            }
        } else if (r0.previousDrawable != null) {
            r0.animatedAlphaValue -= ((float) dt) / 200.0f;
            if (r0.animatedAlphaValue <= 0.0f) {
                r0.animatedAlphaValue = 0.0f;
                r0.previousDrawable = null;
            }
            invalidateParent();
        }
    }

    public void setDiff(int value) {
        this.diff = value;
    }

    public void setProgressColor(int color) {
        this.progressColor = color;
    }

    public void setMiniProgressBackgroundColor(int color) {
        this.miniProgressBackgroundPaint.setColor(color);
    }

    public void setHideCurrentDrawable(boolean value) {
        this.hideCurrentDrawable = value;
    }

    public void setProgress(float value, boolean animated) {
        if (this.drawMiniProgress) {
            if (!(value == 1.0f || this.animatedAlphaValue == 0.0f || this.previousMiniDrawable == null)) {
                this.animatedAlphaValue = 0.0f;
                this.previousMiniDrawable = null;
                this.drawMiniProgress = this.currentMiniDrawable != null;
            }
        } else if (!(value == 1.0f || this.animatedAlphaValue == 0.0f || this.previousDrawable == null)) {
            this.animatedAlphaValue = 0.0f;
            this.previousDrawable = null;
        }
        if (animated) {
            if (this.animatedProgressValue > value) {
                this.animatedProgressValue = value;
            }
            this.animationProgressStart = this.animatedProgressValue;
        } else {
            this.animatedProgressValue = value;
            this.animationProgressStart = value;
        }
        this.currentProgress = value;
        this.currentProgressTime = 0;
        invalidateParent();
    }

    private void invalidateParent() {
        int offset = AndroidUtilities.dp(NUM);
        this.parent.invalidate(((int) this.progressRect.left) - offset, ((int) this.progressRect.top) - offset, ((int) this.progressRect.right) + (offset * 2), ((int) this.progressRect.bottom) + (offset * 2));
    }

    public void setCheckBackground(boolean withRound, boolean animated) {
        if (this.checkDrawable == null) {
            this.checkDrawable = new CheckDrawable();
            this.checkBackgroundDrawable = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), this.checkDrawable, 0);
        }
        Theme.setCombinedDrawableColor(this.checkBackgroundDrawable, Theme.getColor(Theme.key_chat_mediaLoaderPhoto), false);
        Theme.setCombinedDrawableColor(this.checkBackgroundDrawable, Theme.getColor(Theme.key_chat_mediaLoaderPhotoIcon), true);
        if (this.currentDrawable != this.checkBackgroundDrawable) {
            setBackground(this.checkBackgroundDrawable, withRound, animated);
            this.checkDrawable.resetProgress(animated);
        }
    }

    public boolean isDrawCheckDrawable() {
        return this.currentDrawable == this.checkBackgroundDrawable;
    }

    public void setBackground(Drawable drawable, boolean withRound, boolean animated) {
        this.lastUpdateTime = System.currentTimeMillis();
        if (!animated || this.currentDrawable == drawable) {
            this.previousDrawable = null;
            this.previousWithRound = false;
        } else {
            this.previousDrawable = this.currentDrawable;
            this.previousWithRound = this.currentWithRound;
            this.animatedAlphaValue = 1.0f;
            setProgress(1.0f, animated);
        }
        this.currentWithRound = withRound;
        this.currentDrawable = drawable;
        if (animated) {
            invalidateParent();
        } else {
            this.parent.invalidate();
        }
    }

    public void setMiniBackground(Drawable drawable, boolean withRound, boolean animated) {
        this.lastUpdateTime = System.currentTimeMillis();
        boolean z = false;
        if (!animated || this.currentMiniDrawable == drawable) {
            this.previousMiniDrawable = null;
            this.previousMiniWithRound = false;
        } else {
            this.previousMiniDrawable = this.currentMiniDrawable;
            this.previousMiniWithRound = this.currentMiniWithRound;
            this.animatedAlphaValue = 1.0f;
            setProgress(1.0f, animated);
        }
        this.currentMiniWithRound = withRound;
        this.currentMiniDrawable = drawable;
        if (this.previousMiniDrawable == null) {
            if (this.currentMiniDrawable == null) {
                this.drawMiniProgress = z;
                if (this.drawMiniProgress && this.miniDrawBitmap == null) {
                    this.miniDrawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(48.0f), AndroidUtilities.dp(48.0f), Config.ARGB_8888);
                    this.miniDrawCanvas = new Canvas(this.miniDrawBitmap);
                }
                if (animated) {
                    this.parent.invalidate();
                } else {
                    invalidateParent();
                }
            }
        }
        z = true;
        this.drawMiniProgress = z;
        try {
            this.miniDrawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(48.0f), AndroidUtilities.dp(48.0f), Config.ARGB_8888);
            this.miniDrawCanvas = new Canvas(this.miniDrawBitmap);
        } catch (Throwable th) {
        }
        if (animated) {
            invalidateParent();
        } else {
            this.parent.invalidate();
        }
    }

    public boolean swapBackground(Drawable drawable) {
        if (this.currentDrawable == drawable) {
            return false;
        }
        this.currentDrawable = drawable;
        return true;
    }

    public boolean swapMiniBackground(Drawable drawable) {
        boolean z = false;
        if (this.currentMiniDrawable == drawable) {
            return false;
        }
        this.currentMiniDrawable = drawable;
        if (this.previousMiniDrawable == null) {
            if (this.currentMiniDrawable == null) {
                this.drawMiniProgress = z;
                return true;
            }
        }
        z = true;
        this.drawMiniProgress = z;
        return true;
    }

    public float getAlpha() {
        if (this.previousDrawable == null) {
            if (this.currentDrawable == null) {
                return 0.0f;
            }
        }
        return this.animatedAlphaValue;
    }

    public void setOverrideAlpha(float alpha) {
        this.overrideAlpha = alpha;
    }

    public void draw(Canvas canvas) {
        Canvas canvas2 = canvas;
        if (!this.drawMiniProgress || r0.currentDrawable == null) {
            if (r0.previousDrawable != null) {
                if (r0.alphaForPrevious) {
                    r0.previousDrawable.setAlpha((int) ((r0.animatedAlphaValue * 255.0f) * r0.overrideAlpha));
                } else {
                    r0.previousDrawable.setAlpha((int) (r0.overrideAlpha * 255.0f));
                }
                r0.previousDrawable.setBounds((int) r0.progressRect.left, (int) r0.progressRect.top, (int) r0.progressRect.right, (int) r0.progressRect.bottom);
                r0.previousDrawable.draw(canvas2);
            }
            if (!(r0.hideCurrentDrawable || r0.currentDrawable == null)) {
                if (r0.previousDrawable != null) {
                    r0.currentDrawable.setAlpha((int) (((1.0f - r0.animatedAlphaValue) * 255.0f) * r0.overrideAlpha));
                } else {
                    r0.currentDrawable.setAlpha((int) (r0.overrideAlpha * 255.0f));
                }
                r0.currentDrawable.setBounds((int) r0.progressRect.left, (int) r0.progressRect.top, (int) r0.progressRect.right, (int) r0.progressRect.bottom);
                r0.currentDrawable.draw(canvas2);
            }
            if (!r0.currentWithRound) {
                if (!r0.previousWithRound) {
                    updateAnimation(false);
                    return;
                }
            }
            r0.progressPaint.setColor(r0.progressColor);
            if (r0.previousWithRound) {
                r0.progressPaint.setAlpha((int) ((255.0f * r0.animatedAlphaValue) * r0.overrideAlpha));
            } else {
                r0.progressPaint.setAlpha((int) (255.0f * r0.overrideAlpha));
            }
            r0.cicleRect.set(r0.progressRect.left + ((float) r0.diff), r0.progressRect.top + ((float) r0.diff), r0.progressRect.right - ((float) r0.diff), r0.progressRect.bottom - ((float) r0.diff));
            canvas2.drawArc(r0.cicleRect, -90.0f + r0.radOffset, Math.max(4.0f, r0.animatedProgressValue * 360.0f), false, r0.progressPaint);
            updateAnimation(true);
            return;
        }
        int offset;
        int size;
        int offset2;
        float cy;
        if (r0.miniDrawCanvas != null) {
            r0.miniDrawBitmap.eraseColor(0);
        }
        r0.currentDrawable.setAlpha((int) (r0.overrideAlpha * 255.0f));
        if (r0.miniDrawCanvas != null) {
            r0.currentDrawable.setBounds(0, 0, (int) r0.progressRect.width(), (int) r0.progressRect.height());
            r0.currentDrawable.draw(r0.miniDrawCanvas);
        } else {
            r0.currentDrawable.setBounds((int) r0.progressRect.left, (int) r0.progressRect.top, (int) r0.progressRect.right, (int) r0.progressRect.bottom);
            r0.currentDrawable.draw(canvas2);
        }
        if (Math.abs(r0.progressRect.width() - ((float) AndroidUtilities.dp(44.0f))) < AndroidUtilities.density) {
            offset = 0;
            size = 20;
            offset2 = r0.progressRect.centerX() + ((float) AndroidUtilities.dp((float) (16 + 0)));
            cy = r0.progressRect.centerY() + ((float) AndroidUtilities.dp((float) (16 + 0)));
        } else {
            offset = 2;
            size = 22;
            offset2 = r0.progressRect.centerX() + ((float) AndroidUtilities.dp(18.0f));
            cy = r0.progressRect.centerY() + ((float) AndroidUtilities.dp(18.0f));
        }
        int cx = offset2;
        offset2 = offset;
        int halfSize = size / 2;
        float alpha = 1.0f;
        if (r0.previousMiniDrawable != null && r0.alphaForMiniPrevious) {
            alpha = r0.animatedAlphaValue * r0.overrideAlpha;
        }
        float alpha2 = alpha;
        if (r0.miniDrawCanvas != null) {
            r0.miniDrawCanvas.drawCircle((float) AndroidUtilities.dp((float) ((18 + size) + offset2)), (float) AndroidUtilities.dp((float) ((18 + size) + offset2)), ((float) AndroidUtilities.dp((float) (halfSize + 1))) * alpha2, Theme.checkboxSquare_eraserPaint);
        } else {
            r0.miniProgressBackgroundPaint.setColor(r0.progressColor);
            if (r0.previousMiniDrawable != null && r0.currentMiniDrawable == null) {
                r0.miniProgressBackgroundPaint.setAlpha((int) ((r0.animatedAlphaValue * 255.0f) * r0.overrideAlpha));
            } else if (r0.previousMiniDrawable == null || r0.currentMiniDrawable != null) {
                r0.miniProgressBackgroundPaint.setAlpha(255);
            } else {
                r0.miniProgressBackgroundPaint.setAlpha((int) (r0.overrideAlpha * 255.0f));
            }
            canvas2.drawCircle(cx, cy, (float) AndroidUtilities.dp(12.0f), r0.miniProgressBackgroundPaint);
        }
        if (r0.miniDrawCanvas != null) {
            canvas2.drawBitmap(r0.miniDrawBitmap, (float) ((int) r0.progressRect.left), (float) ((int) r0.progressRect.top), null);
        }
        if (r0.previousMiniDrawable != null) {
            if (r0.alphaForMiniPrevious) {
                r0.previousMiniDrawable.setAlpha((int) ((r0.animatedAlphaValue * 255.0f) * r0.overrideAlpha));
            } else {
                r0.previousMiniDrawable.setAlpha((int) (r0.overrideAlpha * 255.0f));
            }
            r0.previousMiniDrawable.setBounds((int) (cx - (((float) AndroidUtilities.dp((float) halfSize)) * alpha2)), (int) (cy - (((float) AndroidUtilities.dp((float) halfSize)) * alpha2)), (int) ((((float) AndroidUtilities.dp((float) halfSize)) * alpha2) + cx), (int) ((((float) AndroidUtilities.dp((float) halfSize)) * alpha2) + cy));
            r0.previousMiniDrawable.draw(canvas2);
        }
        if (!(r0.hideCurrentDrawable || r0.currentMiniDrawable == null)) {
            if (r0.previousMiniDrawable != null) {
                r0.currentMiniDrawable.setAlpha((int) (((1.0f - r0.animatedAlphaValue) * 255.0f) * r0.overrideAlpha));
            } else {
                r0.currentMiniDrawable.setAlpha((int) (r0.overrideAlpha * 255.0f));
            }
            r0.currentMiniDrawable.setBounds((int) (cx - ((float) AndroidUtilities.dp((float) halfSize))), (int) (cy - ((float) AndroidUtilities.dp((float) halfSize))), (int) (((float) AndroidUtilities.dp((float) halfSize)) + cx), (int) (((float) AndroidUtilities.dp((float) halfSize)) + cy));
            r0.currentMiniDrawable.draw(canvas2);
        }
        if (!r0.currentMiniWithRound) {
            if (!r0.previousMiniWithRound) {
                updateAnimation(false);
            }
        }
        r0.miniProgressPaint.setColor(r0.progressColor);
        if (r0.previousMiniWithRound) {
            r0.miniProgressPaint.setAlpha((int) ((255.0f * r0.animatedAlphaValue) * r0.overrideAlpha));
        } else {
            r0.miniProgressPaint.setAlpha((int) (255.0f * r0.overrideAlpha));
        }
        r0.cicleRect.set(cx - (((float) AndroidUtilities.dp((float) (halfSize - 2))) * alpha2), cy - (((float) AndroidUtilities.dp((float) (halfSize - 2))) * alpha2), (((float) AndroidUtilities.dp((float) (halfSize - 2))) * alpha2) + cx, (((float) AndroidUtilities.dp((float) (halfSize - 2))) * alpha2) + cy);
        canvas2.drawArc(r0.cicleRect, -90.0f + r0.radOffset, Math.max(4.0f, r0.animatedProgressValue * 360.0f), false, r0.miniProgressPaint);
        updateAnimation(true);
    }
}
