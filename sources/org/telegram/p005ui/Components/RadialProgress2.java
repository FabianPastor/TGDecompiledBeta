package org.telegram.p005ui.Components;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.tgnet.TLObject;

/* renamed from: org.telegram.ui.Components.RadialProgress2 */
public class RadialProgress2 {
    private static DecelerateInterpolator decelerateInterpolator;
    private float animatedProgressValue = 0.0f;
    private float animationProgressStart = 0.0f;
    private Drawable checkBackgroundDrawable;
    private CheckDrawable checkDrawable;
    private RectF cicleRect = new RectF();
    private int circleColor;
    private String circleColorKey;
    private Paint circleMiniPaint = new Paint(1);
    private Paint circlePaint = new Paint(1);
    private int circlePressedColor;
    private String circlePressedColorKey;
    private int circleRadius;
    private float currentProgress = 0.0f;
    private long currentProgressTime = 0;
    private int diff = AndroidUtilities.m9dp(4.0f);
    private boolean drawMiniIcon;
    private boolean drawMiniProgress;
    private boolean drawProgress;
    private boolean hideProgress;
    private int iconColor;
    private String iconColorKey;
    private int iconPressedColor;
    private String iconPressedColorKey;
    private boolean isPressed;
    private boolean isPressedMini;
    private long lastUpdateTime = 0;
    private MediaActionDrawable mediaActionDrawable;
    private Bitmap miniDrawBitmap;
    private Canvas miniDrawCanvas;
    private MediaActionDrawable miniMediaActionDrawable;
    private Paint miniProgressBackgroundPaint;
    private Paint miniProgressPaint;
    private ImageReceiver overlayImageView;
    private Paint overlayPaint = new Paint(1);
    private float overrideAlpha = 1.0f;
    private View parent;
    private boolean previousCheckDrawable;
    private float progressAlpha = 1.0f;
    private int progressColor = -1;
    private Paint progressPaint;
    private RectF progressRect = new RectF();
    private float radOffset = 0.0f;
    private float wholeAlpha = 1.0f;

    /* renamed from: org.telegram.ui.Components.RadialProgress2$CheckDrawable */
    private class CheckDrawable extends Drawable {
        private Paint paint = new Paint(1);
        private float progress;

        public CheckDrawable() {
            this.paint.setStyle(Style.STROKE);
            this.paint.setStrokeWidth((float) AndroidUtilities.m9dp(3.0f));
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
            int x = getBounds().centerX() - AndroidUtilities.m9dp(12.0f);
            int y = getBounds().centerY() - AndroidUtilities.m9dp(6.0f);
            if (this.progress != 1.0f) {
                p = RadialProgress2.decelerateInterpolator.getInterpolation(this.progress);
            }
            Canvas canvas2 = canvas;
            canvas2.drawLine((float) (AndroidUtilities.m9dp(7.0f) + x), (float) (((int) AndroidUtilities.dpf2(13.0f)) + y), (float) (x + ((int) (((float) AndroidUtilities.m9dp(7.0f)) - (((float) AndroidUtilities.m9dp(6.0f)) * p)))), (float) (y + ((int) (AndroidUtilities.dpf2(13.0f) - (((float) AndroidUtilities.m9dp(6.0f)) * p)))), this.paint);
            canvas2 = canvas;
            canvas2.drawLine((float) (((int) AndroidUtilities.dpf2(7.0f)) + x), (float) (((int) AndroidUtilities.dpf2(13.0f)) + y), (float) (x + ((int) (AndroidUtilities.dpf2(7.0f) + (((float) AndroidUtilities.m9dp(13.0f)) * p)))), (float) (y + ((int) (AndroidUtilities.dpf2(13.0f) - (((float) AndroidUtilities.m9dp(13.0f)) * p)))), this.paint);
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
            return AndroidUtilities.m9dp(48.0f);
        }

        public int getIntrinsicHeight() {
            return AndroidUtilities.m9dp(48.0f);
        }
    }

    public RadialProgress2(View parentView) {
        if (decelerateInterpolator == null) {
            decelerateInterpolator = new DecelerateInterpolator();
        }
        this.progressPaint = new Paint(1);
        this.progressPaint.setStyle(Style.STROKE);
        this.progressPaint.setStrokeCap(Cap.ROUND);
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.m9dp(3.0f));
        this.miniProgressPaint = new Paint(1);
        this.miniProgressPaint.setStyle(Style.STROKE);
        this.miniProgressPaint.setStrokeCap(Cap.ROUND);
        this.miniProgressPaint.setStrokeWidth((float) AndroidUtilities.m9dp(2.0f));
        this.miniProgressBackgroundPaint = new Paint(1);
        this.parent = parentView;
        this.overlayImageView = new ImageReceiver(parentView);
        this.overlayImageView.setInvalidateAll(true);
        this.mediaActionDrawable = new MediaActionDrawable();
        MediaActionDrawable mediaActionDrawable = this.mediaActionDrawable;
        parentView.getClass();
        mediaActionDrawable.setDelegate(RadialProgress2$$Lambda$0.get$Lambda(parentView));
        this.miniMediaActionDrawable = new MediaActionDrawable();
        mediaActionDrawable = this.miniMediaActionDrawable;
        parentView.getClass();
        mediaActionDrawable.setDelegate(RadialProgress2$$Lambda$1.get$Lambda(parentView));
        this.miniMediaActionDrawable.setMini(true);
        this.circleRadius = AndroidUtilities.m9dp(22.0f);
        this.overlayImageView.setRoundRadius(this.circleRadius);
        this.overlayPaint.setColor(NUM);
    }

    public void setCircleRadius(int value) {
        this.circleRadius = value;
        this.overlayImageView.setRoundRadius(this.circleRadius);
    }

    public void setImageOverlay(TLObject image, Object parentObject) {
        String format;
        ImageReceiver imageReceiver = this.overlayImageView;
        if (image != null) {
            format = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(this.circleRadius * 2), Integer.valueOf(this.circleRadius * 2)});
        } else {
            format = null;
        }
        imageReceiver.setImage(image, format, null, null, parentObject, 1);
    }

    public void setImageOverlay(String url) {
        String format;
        ImageReceiver imageReceiver = this.overlayImageView;
        if (url != null) {
            format = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(this.circleRadius * 2), Integer.valueOf(this.circleRadius * 2)});
        } else {
            format = null;
        }
        imageReceiver.setImage(url, format, null, null, -1);
    }

    public void onAttachedToWindow() {
        this.overlayImageView.onAttachedToWindow();
    }

    public void onDetachedFromWindow() {
        this.overlayImageView.onDetachedFromWindow();
    }

    public void setColors(int circle, int circlePressed, int icon, int iconPressed) {
        this.circleColor = circle;
        this.circlePressedColor = circlePressed;
        this.iconColor = icon;
        this.iconPressedColor = iconPressed;
        this.circleColorKey = null;
        this.circlePressedColorKey = null;
        this.iconColorKey = null;
        this.iconPressedColorKey = null;
    }

    public void setColors(String circle, String circlePressed, String icon, String iconPressed) {
        this.circleColorKey = circle;
        this.circlePressedColorKey = circlePressed;
        this.iconColorKey = icon;
        this.iconPressedColorKey = iconPressed;
    }

    public void setStrokeWidth(int width) {
        this.progressPaint.setStrokeWidth((float) width);
    }

    public void setProgressRect(int left, int top, int right, int bottom) {
        this.progressRect.set((float) left, (float) top, (float) right, (float) bottom);
    }

    public RectF getProgressRect() {
        return this.progressRect;
    }

    private void updateAnimation(boolean progress) {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (progress && this.animatedProgressValue != 1.0f) {
            this.radOffset += ((float) (360 * dt)) / 3000.0f;
            float progressDiff = this.currentProgress - this.animationProgressStart;
            if (progressDiff > 0.0f) {
                this.currentProgressTime += dt;
                if (this.currentProgressTime >= 200) {
                    this.animatedProgressValue = this.currentProgress;
                    this.animationProgressStart = this.currentProgress;
                    this.currentProgressTime = 0;
                } else {
                    this.animatedProgressValue = this.animationProgressStart + (decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / 200.0f) * progressDiff);
                }
            }
            invalidateParent();
        }
        if (this.mediaActionDrawable.getCurrentIcon() == 4 && this.wholeAlpha != 0.0f && (!progress || this.animatedProgressValue >= 1.0f)) {
            this.wholeAlpha -= ((float) dt) / 200.0f;
            if (this.wholeAlpha < 0.0f) {
                this.wholeAlpha = 0.0f;
            }
            invalidateParent();
        }
        if (this.hideProgress) {
            this.progressAlpha -= ((float) dt) / 200.0f;
            if (this.progressAlpha <= 0.0f) {
                this.progressAlpha = 0.0f;
                this.hideProgress = false;
                if (this.drawMiniProgress) {
                    this.drawMiniProgress = this.drawMiniIcon;
                }
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

    public void setProgress(float value, boolean animated) {
        if (!this.hideProgress || this.drawProgress || value >= this.animatedProgressValue) {
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
    }

    private void invalidateParent() {
        int offset = AndroidUtilities.m9dp(2.0f);
        this.parent.invalidate(((int) this.progressRect.left) - offset, ((int) this.progressRect.top) - offset, ((int) this.progressRect.right) + (offset * 2), ((int) this.progressRect.bottom) + (offset * 2));
    }

    public void setIcon(int icon, boolean withRound, boolean ifSame, boolean animated) {
        float f = 0.0f;
        if (!ifSame || icon != this.mediaActionDrawable.getCurrentIcon()) {
            this.lastUpdateTime = System.currentTimeMillis();
            this.mediaActionDrawable.setIcon(icon, animated);
            if (icon != 4) {
                this.wholeAlpha = 1.0f;
                if (!(this.drawMiniIcon || withRound == this.drawProgress)) {
                    if (animated) {
                        this.hideProgress = this.drawProgress;
                        if (withRound) {
                            this.progressAlpha = 1.0f;
                        }
                    } else {
                        this.hideProgress = false;
                        if (withRound) {
                            f = 1.0f;
                        }
                        this.progressAlpha = f;
                    }
                }
                if (!this.drawMiniIcon) {
                    this.drawProgress = withRound;
                }
            } else if (!animated) {
                this.wholeAlpha = 0.0f;
            }
            if (animated) {
                invalidateParent();
            } else {
                this.parent.invalidate();
            }
        }
    }

    public void setMiniIcon(int icon, boolean withRound, boolean ifSame, boolean animated) {
        float f = 1.0f;
        if (icon != 2 && icon != 3 && icon >= 0) {
            return;
        }
        if (!ifSame || icon != this.miniMediaActionDrawable.getCurrentIcon()) {
            boolean z;
            this.lastUpdateTime = System.currentTimeMillis();
            if (icon >= 0) {
                this.drawMiniIcon = true;
                this.miniMediaActionDrawable.setIcon(icon, animated);
                if (withRound != this.drawProgress) {
                    if (animated) {
                        this.hideProgress = this.drawProgress;
                        if (withRound) {
                            this.progressAlpha = 1.0f;
                        }
                    } else {
                        this.hideProgress = false;
                        if (!withRound) {
                            f = 0.0f;
                        }
                        this.progressAlpha = f;
                    }
                }
            } else {
                if (this.drawMiniIcon && animated) {
                    this.hideProgress = this.drawProgress;
                    this.progressAlpha = 1.0f;
                } else {
                    this.hideProgress = false;
                }
                this.drawMiniIcon = false;
            }
            this.drawProgress = withRound;
            if (this.hideProgress || this.drawMiniIcon) {
                z = true;
            } else {
                z = false;
            }
            this.drawMiniProgress = z;
            if (this.drawMiniProgress && this.miniDrawBitmap == null) {
                try {
                    this.miniDrawBitmap = Bitmap.createBitmap(AndroidUtilities.m9dp(48.0f), AndroidUtilities.m9dp(48.0f), Config.ARGB_8888);
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
    }

    public boolean swapIcon(int icon) {
        if (this.mediaActionDrawable.setIcon(icon, false)) {
            return true;
        }
        return false;
    }

    public void setPressed(boolean value, boolean mini) {
        if (mini) {
            this.isPressedMini = value;
        } else {
            this.isPressed = value;
        }
        invalidateParent();
    }

    public float getAlpha() {
        return 1.0f;
    }

    public void setOverrideAlpha(float alpha) {
        this.overrideAlpha = alpha;
    }

    public void draw(Canvas canvas) {
        if (this.mediaActionDrawable.getCurrentIcon() != 4 || this.wholeAlpha > 0.0f) {
            int color;
            int centerX;
            int centerY;
            float alpha;
            if (this.isPressedMini) {
                if (this.iconPressedColorKey != null) {
                    this.miniMediaActionDrawable.setColor(Theme.getColor(this.iconPressedColorKey));
                } else {
                    this.miniMediaActionDrawable.setColor(this.iconPressedColor);
                }
                if (this.circlePressedColorKey != null) {
                    this.circleMiniPaint.setColor(Theme.getColor(this.circlePressedColorKey));
                } else {
                    this.circleMiniPaint.setColor(this.circlePressedColor);
                }
            } else {
                if (this.iconColorKey != null) {
                    this.miniMediaActionDrawable.setColor(Theme.getColor(this.iconColorKey));
                } else {
                    this.miniMediaActionDrawable.setColor(this.iconColor);
                }
                if (this.circleColorKey != null) {
                    this.circleMiniPaint.setColor(Theme.getColor(this.circleColorKey));
                } else {
                    this.circleMiniPaint.setColor(this.circleColor);
                }
            }
            MediaActionDrawable mediaActionDrawable;
            if (this.isPressed) {
                if (this.iconPressedColorKey != null) {
                    mediaActionDrawable = this.mediaActionDrawable;
                    color = Theme.getColor(this.iconPressedColorKey);
                    mediaActionDrawable.setColor(color);
                } else {
                    mediaActionDrawable = this.mediaActionDrawable;
                    color = this.iconPressedColor;
                    mediaActionDrawable.setColor(color);
                }
                if (this.circlePressedColorKey != null) {
                    this.circlePaint.setColor(Theme.getColor(this.circlePressedColorKey));
                } else {
                    this.circlePaint.setColor(this.circlePressedColor);
                }
            } else {
                if (this.iconColorKey != null) {
                    mediaActionDrawable = this.mediaActionDrawable;
                    color = Theme.getColor(this.iconColorKey);
                    mediaActionDrawable.setColor(color);
                } else {
                    mediaActionDrawable = this.mediaActionDrawable;
                    color = this.iconColor;
                    mediaActionDrawable.setColor(color);
                }
                if (this.circleColorKey != null) {
                    this.circlePaint.setColor(Theme.getColor(this.circleColorKey));
                } else {
                    this.circlePaint.setColor(this.circleColor);
                }
            }
            if (this.drawMiniProgress && this.miniDrawCanvas != null) {
                this.miniDrawBitmap.eraseColor(0);
            }
            this.circlePaint.setAlpha((int) ((((float) this.circlePaint.getAlpha()) * this.wholeAlpha) * this.overrideAlpha));
            this.circleMiniPaint.setAlpha((int) ((((float) this.circleMiniPaint.getAlpha()) * this.wholeAlpha) * this.overrideAlpha));
            boolean drawCircle = true;
            if (!this.drawMiniProgress || this.miniDrawCanvas == null) {
                centerX = (int) this.progressRect.centerX();
                centerY = (int) this.progressRect.centerY();
            } else {
                centerX = (int) (this.progressRect.width() / 2.0f);
                centerY = (int) (this.progressRect.height() / 2.0f);
            }
            if (this.overlayImageView.hasBitmapImage()) {
                int c;
                alpha = this.overlayImageView.getCurrentAlpha();
                this.overlayPaint.setAlpha((int) (((100.0f * alpha) * this.wholeAlpha) * this.overrideAlpha));
                if (alpha >= 1.0f) {
                    drawCircle = false;
                    c = -1;
                } else {
                    int r = Color.red(color);
                    int g = Color.green(color);
                    int b = Color.blue(color);
                    int a = Color.alpha(color);
                    c = Color.argb(a + ((int) (((float) (255 - a)) * alpha)), r + ((int) (((float) (255 - r)) * alpha)), g + ((int) (((float) (255 - g)) * alpha)), b + ((int) (((float) (255 - b)) * alpha)));
                }
                this.mediaActionDrawable.setColor(c);
                this.overlayImageView.setImageCoords(centerX - this.circleRadius, centerY - this.circleRadius, this.circleRadius * 2, this.circleRadius * 2);
            }
            if (drawCircle) {
                if (this.drawMiniProgress && this.miniDrawCanvas != null) {
                    this.miniDrawCanvas.drawCircle((float) centerX, (float) centerY, (float) this.circleRadius, this.circlePaint);
                } else if (!(this.mediaActionDrawable.getCurrentIcon() == 4 && this.wholeAlpha == 0.0f)) {
                    canvas.drawCircle((float) centerX, (float) centerY, (float) this.circleRadius, this.circlePaint);
                }
            }
            if (this.overlayImageView.hasBitmapImage()) {
                this.overlayImageView.setAlpha(this.wholeAlpha * this.overrideAlpha);
                if (!this.drawMiniProgress || this.miniDrawCanvas == null) {
                    this.overlayImageView.draw(canvas);
                    canvas.drawCircle((float) centerX, (float) centerY, (float) this.circleRadius, this.overlayPaint);
                } else {
                    this.overlayImageView.draw(this.miniDrawCanvas);
                    this.miniDrawCanvas.drawCircle((float) centerX, (float) centerY, (float) this.circleRadius, this.overlayPaint);
                }
            }
            this.mediaActionDrawable.setBounds(centerX - AndroidUtilities.m9dp(16.0f), centerY - AndroidUtilities.m9dp(16.0f), AndroidUtilities.m9dp(16.0f) + centerX, AndroidUtilities.m9dp(16.0f) + centerY);
            if (this.drawMiniProgress) {
                this.mediaActionDrawable.setAlpha((int) ((255.0f * this.wholeAlpha) * this.overrideAlpha));
                if (this.miniDrawCanvas != null) {
                    this.mediaActionDrawable.draw(this.miniDrawCanvas);
                } else {
                    this.mediaActionDrawable.draw(canvas);
                }
            } else {
                this.mediaActionDrawable.draw(canvas);
            }
            if (this.drawMiniProgress) {
                int offset;
                int size;
                float cx;
                float cy;
                if (Math.abs(this.progressRect.width() - ((float) AndroidUtilities.m9dp(44.0f))) < AndroidUtilities.density) {
                    offset = 0;
                    size = 20;
                    cx = this.progressRect.centerX() + ((float) AndroidUtilities.m9dp((float) 16));
                    cy = this.progressRect.centerY() + ((float) AndroidUtilities.m9dp((float) 16));
                } else {
                    offset = 2;
                    size = 22;
                    cx = this.progressRect.centerX() + ((float) AndroidUtilities.m9dp(18.0f));
                    cy = this.progressRect.centerY() + ((float) AndroidUtilities.m9dp(18.0f));
                }
                int halfSize = size / 2;
                alpha = this.drawMiniIcon ? 1.0f : this.progressAlpha;
                if (this.miniDrawCanvas != null) {
                    this.miniDrawCanvas.drawCircle((float) AndroidUtilities.m9dp((float) ((size + 18) + offset)), (float) AndroidUtilities.m9dp((float) ((size + 18) + offset)), ((float) AndroidUtilities.m9dp((float) (halfSize + 1))) * alpha, Theme.checkboxSquare_eraserPaint);
                } else {
                    this.miniProgressBackgroundPaint.setColor(this.progressColor);
                    this.miniProgressBackgroundPaint.setAlpha((int) (((255.0f * this.progressAlpha) * this.wholeAlpha) * this.overrideAlpha));
                    canvas.drawCircle(cx, cy, (float) AndroidUtilities.m9dp(12.0f), this.miniProgressBackgroundPaint);
                }
                if (this.miniDrawCanvas != null) {
                    canvas.drawBitmap(this.miniDrawBitmap, (float) ((int) this.progressRect.left), (float) ((int) this.progressRect.top), null);
                }
                canvas.drawCircle(cx, cy, ((float) AndroidUtilities.m9dp((float) halfSize)) * alpha, this.circleMiniPaint);
                this.miniMediaActionDrawable.setAlpha((int) ((255.0f * this.wholeAlpha) * this.overrideAlpha));
                this.miniMediaActionDrawable.setScale(alpha);
                this.miniMediaActionDrawable.setBounds((int) (cx - (((float) AndroidUtilities.m9dp((float) halfSize)) * alpha)), (int) (cy - (((float) AndroidUtilities.m9dp((float) halfSize)) * alpha)), (int) ((((float) AndroidUtilities.m9dp((float) halfSize)) * alpha) + cx), (int) ((((float) AndroidUtilities.m9dp((float) halfSize)) * alpha) + cy));
                this.miniMediaActionDrawable.draw(canvas);
                if (this.drawProgress || this.hideProgress) {
                    this.miniProgressPaint.setColor(this.progressColor);
                    this.miniProgressPaint.setAlpha((int) (((255.0f * this.progressAlpha) * this.wholeAlpha) * this.overrideAlpha));
                    this.cicleRect.set(cx - (((float) AndroidUtilities.m9dp((float) (halfSize - 2))) * alpha), cy - (((float) AndroidUtilities.m9dp((float) (halfSize - 2))) * alpha), (((float) AndroidUtilities.m9dp((float) (halfSize - 2))) * alpha) + cx, (((float) AndroidUtilities.m9dp((float) (halfSize - 2))) * alpha) + cy);
                    canvas.drawArc(this.cicleRect, this.radOffset - 0.049804688f, Math.max(4.0f, 360.0f * this.animatedProgressValue), false, this.miniProgressPaint);
                    updateAnimation(true);
                    return;
                }
                updateAnimation(false);
            } else if (this.drawProgress || this.hideProgress) {
                this.progressPaint.setColor(this.progressColor);
                this.progressPaint.setAlpha((int) (((255.0f * this.progressAlpha) * this.wholeAlpha) * this.overrideAlpha));
                this.cicleRect.set(this.progressRect.left + ((float) this.diff), this.progressRect.top + ((float) this.diff), this.progressRect.right - ((float) this.diff), this.progressRect.bottom - ((float) this.diff));
                canvas.drawArc(this.cicleRect, this.radOffset - 0.049804688f, Math.max(4.0f, 360.0f * this.animatedProgressValue), false, this.progressPaint);
                updateAnimation(true);
            } else {
                updateAnimation(false);
            }
        }
    }
}
