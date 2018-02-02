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
    private boolean drawCheckDrawable;
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
            float p = 1.0f;
            int x = getBounds().centerX() - AndroidUtilities.dp(12.0f);
            int y = getBounds().centerY() - AndroidUtilities.dp(6.0f);
            if (this.progress != 1.0f) {
                p = RadialProgress.decelerateInterpolator.getInterpolation(this.progress);
            }
            Canvas canvas2 = canvas;
            canvas2.drawLine((float) (AndroidUtilities.dp(7.0f) + x), (float) (((int) AndroidUtilities.dpf2(13.0f)) + y), (float) (x + ((int) (((float) AndroidUtilities.dp(7.0f)) - (((float) AndroidUtilities.dp(6.0f)) * p)))), (float) (y + ((int) (AndroidUtilities.dpf2(13.0f) - (((float) AndroidUtilities.dp(6.0f)) * p)))), this.paint);
            canvas2 = canvas;
            canvas2.drawLine((float) (((int) AndroidUtilities.dpf2(7.0f)) + x), (float) (((int) AndroidUtilities.dpf2(13.0f)) + y), (float) (x + ((int) (AndroidUtilities.dpf2(7.0f) + (((float) AndroidUtilities.dp(13.0f)) * p)))), (float) (y + ((int) (AndroidUtilities.dpf2(13.0f) - (((float) AndroidUtilities.dp(13.0f)) * p)))), this.paint);
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
        boolean z = false;
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (this.checkBackgroundDrawable != null && ((this.currentDrawable == this.checkBackgroundDrawable || this.previousDrawable == this.checkBackgroundDrawable) && this.checkDrawable.updateAnimation(dt))) {
            invalidateParent();
        }
        if (progress) {
            if (this.animatedProgressValue != 1.0f) {
                this.radOffset += ((float) (360 * dt)) / 3000.0f;
                float progressDiff = this.currentProgress - this.animationProgressStart;
                if (progressDiff > 0.0f) {
                    this.currentProgressTime += dt;
                    if (this.currentProgressTime >= 300) {
                        this.animatedProgressValue = this.currentProgress;
                        this.animationProgressStart = this.currentProgress;
                        this.currentProgressTime = 0;
                    } else {
                        this.animatedProgressValue = this.animationProgressStart + (decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / 300.0f) * progressDiff);
                    }
                }
                invalidateParent();
            }
            if (this.drawMiniProgress) {
                if (this.animatedProgressValue >= 1.0f && this.previousMiniDrawable != null) {
                    this.animatedAlphaValue -= ((float) dt) / 200.0f;
                    if (this.animatedAlphaValue <= 0.0f) {
                        this.animatedAlphaValue = 0.0f;
                        this.previousMiniDrawable = null;
                        if (this.currentMiniDrawable != null) {
                            z = true;
                        }
                        this.drawMiniProgress = z;
                    }
                    invalidateParent();
                }
            } else if (this.animatedProgressValue >= 1.0f && this.previousDrawable != null) {
                this.animatedAlphaValue -= ((float) dt) / 200.0f;
                if (this.animatedAlphaValue <= 0.0f) {
                    this.animatedAlphaValue = 0.0f;
                    this.previousDrawable = null;
                }
                invalidateParent();
            }
        } else if (this.drawMiniProgress) {
            if (this.previousMiniDrawable != null) {
                this.animatedAlphaValue -= ((float) dt) / 200.0f;
                if (this.animatedAlphaValue <= 0.0f) {
                    this.animatedAlphaValue = 0.0f;
                    this.previousMiniDrawable = null;
                    if (this.currentMiniDrawable != null) {
                        z = true;
                    }
                    this.drawMiniProgress = z;
                }
                invalidateParent();
            }
        } else if (this.previousDrawable != null) {
            this.animatedAlphaValue -= ((float) dt) / 200.0f;
            if (this.animatedAlphaValue <= 0.0f) {
                this.animatedAlphaValue = 0.0f;
                this.previousDrawable = null;
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
        int offset = AndroidUtilities.dp(2.0f);
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
        boolean z = false;
        this.lastUpdateTime = System.currentTimeMillis();
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
        if (!(this.previousMiniDrawable == null && this.currentMiniDrawable == null)) {
            z = true;
        }
        this.drawMiniProgress = z;
        if (this.drawMiniProgress && this.miniDrawBitmap == null) {
            try {
                this.miniDrawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(48.0f), AndroidUtilities.dp(48.0f), Config.ARGB_8888);
                this.miniDrawCanvas = new Canvas(this.miniDrawBitmap);
            } catch (Throwable th) {
            }
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
        if (!(this.previousMiniDrawable == null && this.currentMiniDrawable == null)) {
            z = true;
        }
        this.drawMiniProgress = z;
        return true;
    }

    public float getAlpha() {
        return (this.previousDrawable == null && this.currentDrawable == null) ? 0.0f : this.animatedAlphaValue;
    }

    public void setOverrideAlpha(float alpha) {
        this.overrideAlpha = alpha;
    }

    public void draw(Canvas canvas) {
        if (!this.drawMiniProgress || this.currentDrawable == null) {
            if (this.previousDrawable != null) {
                if (this.alphaForPrevious) {
                    this.previousDrawable.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.overrideAlpha));
                } else {
                    this.previousDrawable.setAlpha((int) (this.overrideAlpha * 255.0f));
                }
                this.previousDrawable.setBounds((int) this.progressRect.left, (int) this.progressRect.top, (int) this.progressRect.right, (int) this.progressRect.bottom);
                this.previousDrawable.draw(canvas);
            }
            if (!(this.hideCurrentDrawable || this.currentDrawable == null)) {
                if (this.previousDrawable != null) {
                    this.currentDrawable.setAlpha((int) (((1.0f - this.animatedAlphaValue) * 255.0f) * this.overrideAlpha));
                } else {
                    this.currentDrawable.setAlpha((int) (this.overrideAlpha * 255.0f));
                }
                this.currentDrawable.setBounds((int) this.progressRect.left, (int) this.progressRect.top, (int) this.progressRect.right, (int) this.progressRect.bottom);
                this.currentDrawable.draw(canvas);
            }
            if (this.currentWithRound || this.previousWithRound) {
                this.progressPaint.setColor(this.progressColor);
                if (this.previousWithRound) {
                    this.progressPaint.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.overrideAlpha));
                } else {
                    this.progressPaint.setAlpha((int) (this.overrideAlpha * 255.0f));
                }
                this.cicleRect.set(this.progressRect.left + ((float) this.diff), this.progressRect.top + ((float) this.diff), this.progressRect.right - ((float) this.diff), this.progressRect.bottom - ((float) this.diff));
                canvas.drawArc(this.cicleRect, -90.0f + this.radOffset, Math.max(4.0f, 360.0f * this.animatedProgressValue), false, this.progressPaint);
                updateAnimation(true);
                return;
            }
            updateAnimation(false);
            return;
        }
        int offset;
        if (this.miniDrawCanvas != null) {
            this.miniDrawBitmap.eraseColor(0);
        }
        this.currentDrawable.setAlpha((int) (this.overrideAlpha * 255.0f));
        if (this.miniDrawCanvas != null) {
            this.currentDrawable.setBounds(0, 0, (int) this.progressRect.width(), (int) this.progressRect.height());
            this.currentDrawable.draw(this.miniDrawCanvas);
        } else {
            this.currentDrawable.setBounds((int) this.progressRect.left, (int) this.progressRect.top, (int) this.progressRect.right, (int) this.progressRect.bottom);
            this.currentDrawable.draw(canvas);
        }
        if (this.progressRect.width() == ((float) AndroidUtilities.dp(44.0f))) {
            offset = 0;
        } else {
            offset = 2;
        }
        float alpha = 1.0f;
        if (this.previousMiniDrawable != null && this.alphaForMiniPrevious) {
            alpha = this.animatedAlphaValue * this.overrideAlpha;
        }
        float cx = this.progressRect.centerX() + ((float) AndroidUtilities.dp((float) (offset + 15)));
        float cy = this.progressRect.centerY() + ((float) AndroidUtilities.dp((float) (offset + 15)));
        if (this.miniDrawCanvas != null) {
            this.miniDrawCanvas.drawCircle((float) AndroidUtilities.dp((float) ((offset * 2) + 37)), (float) AndroidUtilities.dp((float) ((offset * 2) + 37)), ((float) AndroidUtilities.dp(9.0f)) * alpha, Theme.checkboxSquare_eraserPaint);
        } else {
            this.miniProgressBackgroundPaint.setColor(this.progressColor);
            if (this.previousMiniDrawable != null && this.currentMiniDrawable == null) {
                this.miniProgressBackgroundPaint.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.overrideAlpha));
            } else if (this.previousMiniDrawable == null || this.currentMiniDrawable != null) {
                this.miniProgressBackgroundPaint.setAlpha(255);
            } else {
                this.miniProgressBackgroundPaint.setAlpha((int) (this.overrideAlpha * 255.0f));
            }
            canvas.drawCircle(cx, cy, (float) AndroidUtilities.dp(9.0f), this.miniProgressBackgroundPaint);
        }
        if (this.miniDrawCanvas != null) {
            canvas.drawBitmap(this.miniDrawBitmap, (float) ((int) this.progressRect.left), (float) ((int) this.progressRect.top), null);
        }
        if (this.previousMiniDrawable != null) {
            if (this.alphaForMiniPrevious) {
                this.previousMiniDrawable.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.overrideAlpha));
            } else {
                this.previousMiniDrawable.setAlpha((int) (this.overrideAlpha * 255.0f));
            }
            this.previousMiniDrawable.setBounds((int) (cx - (((float) AndroidUtilities.dp(7.5f)) * alpha)), (int) (cy - (((float) AndroidUtilities.dp(7.5f)) * alpha)), (int) ((((float) AndroidUtilities.dp(7.5f)) * alpha) + cx), (int) ((((float) AndroidUtilities.dp(7.5f)) * alpha) + cy));
            this.previousMiniDrawable.draw(canvas);
        }
        if (!(this.hideCurrentDrawable || this.currentMiniDrawable == null)) {
            if (this.previousMiniDrawable != null) {
                this.currentMiniDrawable.setAlpha((int) (((1.0f - this.animatedAlphaValue) * 255.0f) * this.overrideAlpha));
            } else {
                this.currentMiniDrawable.setAlpha((int) (this.overrideAlpha * 255.0f));
            }
            this.currentMiniDrawable.setBounds((int) (cx - ((float) AndroidUtilities.dp(7.5f))), (int) (cy - ((float) AndroidUtilities.dp(7.5f))), (int) (((float) AndroidUtilities.dp(7.5f)) + cx), (int) (((float) AndroidUtilities.dp(7.5f)) + cy));
            this.currentMiniDrawable.draw(canvas);
        }
        if (this.currentMiniWithRound || this.previousMiniWithRound) {
            this.miniProgressPaint.setColor(this.progressColor);
            if (this.previousMiniWithRound) {
                this.miniProgressPaint.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.overrideAlpha));
            } else {
                this.miniProgressPaint.setAlpha((int) (this.overrideAlpha * 255.0f));
            }
            this.cicleRect.set(cx - (((float) AndroidUtilities.dp(5.5f)) * alpha), cy - (((float) AndroidUtilities.dp(5.5f)) * alpha), (((float) AndroidUtilities.dp(5.5f)) * alpha) + cx, (((float) AndroidUtilities.dp(5.5f)) * alpha) + cy);
            canvas.drawArc(this.cicleRect, -90.0f + this.radOffset, Math.max(4.0f, 360.0f * this.animatedProgressValue), false, this.miniProgressPaint);
            updateAnimation(true);
            return;
        }
        updateAnimation(false);
    }
}
