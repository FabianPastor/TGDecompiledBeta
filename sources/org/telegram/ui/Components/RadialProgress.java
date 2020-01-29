package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class RadialProgress {
    /* access modifiers changed from: private */
    public static DecelerateInterpolator decelerateInterpolator;
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

        public int getOpacity() {
            return -2;
        }

        public CheckDrawable() {
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
            this.paint.setStrokeCap(Paint.Cap.ROUND);
            this.paint.setColor(-1);
        }

        public void resetProgress(boolean z) {
            this.progress = z ? 0.0f : 1.0f;
        }

        public boolean updateAnimation(long j) {
            float f = this.progress;
            if (f >= 1.0f) {
                return false;
            }
            this.progress = f + (((float) j) / 700.0f);
            if (this.progress <= 1.0f) {
                return true;
            }
            this.progress = 1.0f;
            return true;
        }

        public void draw(Canvas canvas) {
            int centerX = getBounds().centerX() - AndroidUtilities.dp(12.0f);
            int centerY = getBounds().centerY() - AndroidUtilities.dp(6.0f);
            float f = 1.0f;
            if (this.progress != 1.0f) {
                f = RadialProgress.decelerateInterpolator.getInterpolation(this.progress);
            }
            Canvas canvas2 = canvas;
            canvas2.drawLine((float) (AndroidUtilities.dp(7.0f) + centerX), (float) (((int) AndroidUtilities.dpf2(13.0f)) + centerY), (float) (((int) (((float) AndroidUtilities.dp(7.0f)) - (((float) AndroidUtilities.dp(6.0f)) * f))) + centerX), (float) (((int) (AndroidUtilities.dpf2(13.0f) - (((float) AndroidUtilities.dp(6.0f)) * f))) + centerY), this.paint);
            canvas.drawLine((float) (((int) AndroidUtilities.dpf2(7.0f)) + centerX), (float) (((int) AndroidUtilities.dpf2(13.0f)) + centerY), (float) (centerX + ((int) (AndroidUtilities.dpf2(7.0f) + (((float) AndroidUtilities.dp(13.0f)) * f)))), (float) (centerY + ((int) (AndroidUtilities.dpf2(13.0f) - (((float) AndroidUtilities.dp(13.0f)) * f)))), this.paint);
        }

        public void setAlpha(int i) {
            this.paint.setAlpha(i);
        }

        public void setColorFilter(ColorFilter colorFilter) {
            this.paint.setColorFilter(colorFilter);
        }

        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(48.0f);
        }

        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(48.0f);
        }
    }

    public RadialProgress(View view) {
        if (decelerateInterpolator == null) {
            decelerateInterpolator = new DecelerateInterpolator();
        }
        this.progressPaint = new Paint(1);
        this.progressPaint.setStyle(Paint.Style.STROKE);
        this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.miniProgressPaint = new Paint(1);
        this.miniProgressPaint.setStyle(Paint.Style.STROKE);
        this.miniProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        this.miniProgressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.miniProgressBackgroundPaint = new Paint(1);
        this.parent = view;
    }

    public void setStrokeWidth(int i) {
        this.progressPaint.setStrokeWidth((float) i);
    }

    public void setProgressRect(int i, int i2, int i3, int i4) {
        this.progressRect.set((float) i, (float) i2, (float) i3, (float) i4);
    }

    public RectF getProgressRect() {
        return this.progressRect;
    }

    public void setAlphaForPrevious(boolean z) {
        this.alphaForPrevious = z;
    }

    public void setAlphaForMiniPrevious(boolean z) {
        this.alphaForMiniPrevious = z;
    }

    private void updateAnimation(boolean z) {
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.lastUpdateTime;
        this.lastUpdateTime = currentTimeMillis;
        Drawable drawable = this.checkBackgroundDrawable;
        if (drawable != null && ((this.currentDrawable == drawable || this.previousDrawable == drawable) && this.checkDrawable.updateAnimation(j))) {
            invalidateParent();
        }
        boolean z2 = true;
        if (z) {
            if (this.animatedProgressValue != 1.0f) {
                this.radOffset += ((float) (360 * j)) / 3000.0f;
                float f = this.currentProgress;
                float f2 = this.animationProgressStart;
                float f3 = f - f2;
                if (f3 > 0.0f) {
                    this.currentProgressTime += j;
                    long j2 = this.currentProgressTime;
                    if (j2 >= 300) {
                        this.animatedProgressValue = f;
                        this.animationProgressStart = f;
                        this.currentProgressTime = 0;
                    } else {
                        this.animatedProgressValue = f2 + (f3 * decelerateInterpolator.getInterpolation(((float) j2) / 300.0f));
                    }
                }
                invalidateParent();
            }
            if (this.drawMiniProgress) {
                if (this.animatedProgressValue >= 1.0f && this.previousMiniDrawable != null) {
                    this.animatedAlphaValue -= ((float) j) / 200.0f;
                    if (this.animatedAlphaValue <= 0.0f) {
                        this.animatedAlphaValue = 0.0f;
                        this.previousMiniDrawable = null;
                        if (this.currentMiniDrawable == null) {
                            z2 = false;
                        }
                        this.drawMiniProgress = z2;
                    }
                    invalidateParent();
                }
            } else if (this.animatedProgressValue >= 1.0f && this.previousDrawable != null) {
                this.animatedAlphaValue -= ((float) j) / 200.0f;
                if (this.animatedAlphaValue <= 0.0f) {
                    this.animatedAlphaValue = 0.0f;
                    this.previousDrawable = null;
                }
                invalidateParent();
            }
        } else if (this.drawMiniProgress) {
            if (this.previousMiniDrawable != null) {
                this.animatedAlphaValue -= ((float) j) / 200.0f;
                if (this.animatedAlphaValue <= 0.0f) {
                    this.animatedAlphaValue = 0.0f;
                    this.previousMiniDrawable = null;
                    if (this.currentMiniDrawable == null) {
                        z2 = false;
                    }
                    this.drawMiniProgress = z2;
                }
                invalidateParent();
            }
        } else if (this.previousDrawable != null) {
            this.animatedAlphaValue -= ((float) j) / 200.0f;
            if (this.animatedAlphaValue <= 0.0f) {
                this.animatedAlphaValue = 0.0f;
                this.previousDrawable = null;
            }
            invalidateParent();
        }
    }

    public void setDiff(int i) {
        this.diff = i;
    }

    public void setProgressColor(int i) {
        this.progressColor = i;
    }

    public void setMiniProgressBackgroundColor(int i) {
        this.miniProgressBackgroundPaint.setColor(i);
    }

    public void setHideCurrentDrawable(boolean z) {
        this.hideCurrentDrawable = z;
    }

    public void setProgress(float f, boolean z) {
        if (this.drawMiniProgress) {
            if (!(f == 1.0f || this.animatedAlphaValue == 0.0f || this.previousMiniDrawable == null)) {
                this.animatedAlphaValue = 0.0f;
                this.previousMiniDrawable = null;
                this.drawMiniProgress = this.currentMiniDrawable != null;
            }
        } else if (!(f == 1.0f || this.animatedAlphaValue == 0.0f || this.previousDrawable == null)) {
            this.animatedAlphaValue = 0.0f;
            this.previousDrawable = null;
        }
        if (!z) {
            this.animatedProgressValue = f;
            this.animationProgressStart = f;
        } else {
            if (this.animatedProgressValue > f) {
                this.animatedProgressValue = f;
            }
            this.animationProgressStart = this.animatedProgressValue;
        }
        this.currentProgress = f;
        this.currentProgressTime = 0;
        invalidateParent();
    }

    private void invalidateParent() {
        int dp = AndroidUtilities.dp(2.0f);
        View view = this.parent;
        RectF rectF = this.progressRect;
        int i = ((int) rectF.left) - dp;
        int i2 = ((int) rectF.top) - dp;
        int i3 = dp * 2;
        view.invalidate(i, i2, ((int) rectF.right) + i3, ((int) rectF.bottom) + i3);
    }

    public void setCheckBackground(boolean z, boolean z2) {
        if (this.checkDrawable == null) {
            this.checkDrawable = new CheckDrawable();
            this.checkBackgroundDrawable = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), (Drawable) this.checkDrawable, 0);
        }
        Theme.setCombinedDrawableColor(this.checkBackgroundDrawable, Theme.getColor("chat_mediaLoaderPhoto"), false);
        Theme.setCombinedDrawableColor(this.checkBackgroundDrawable, Theme.getColor("chat_mediaLoaderPhotoIcon"), true);
        Drawable drawable = this.currentDrawable;
        Drawable drawable2 = this.checkBackgroundDrawable;
        if (drawable != drawable2) {
            setBackground(drawable2, z, z2);
            this.checkDrawable.resetProgress(z2);
        }
    }

    public boolean isDrawCheckDrawable() {
        return this.currentDrawable == this.checkBackgroundDrawable;
    }

    public void setBackground(Drawable drawable, boolean z, boolean z2) {
        Drawable drawable2;
        this.lastUpdateTime = System.currentTimeMillis();
        if (!z2 || (drawable2 = this.currentDrawable) == drawable) {
            this.previousDrawable = null;
            this.previousWithRound = false;
        } else {
            this.previousDrawable = drawable2;
            this.previousWithRound = this.currentWithRound;
            this.animatedAlphaValue = 1.0f;
            setProgress(1.0f, z2);
        }
        this.currentWithRound = z;
        this.currentDrawable = drawable;
        if (!z2) {
            this.parent.invalidate();
        } else {
            invalidateParent();
        }
    }

    public void setMiniBackground(Drawable drawable, boolean z, boolean z2) {
        Drawable drawable2;
        this.lastUpdateTime = System.currentTimeMillis();
        boolean z3 = false;
        if (!z2 || (drawable2 = this.currentMiniDrawable) == drawable) {
            this.previousMiniDrawable = null;
            this.previousMiniWithRound = false;
        } else {
            this.previousMiniDrawable = drawable2;
            this.previousMiniWithRound = this.currentMiniWithRound;
            this.animatedAlphaValue = 1.0f;
            setProgress(1.0f, z2);
        }
        this.currentMiniWithRound = z;
        this.currentMiniDrawable = drawable;
        if (!(this.previousMiniDrawable == null && this.currentMiniDrawable == null)) {
            z3 = true;
        }
        this.drawMiniProgress = z3;
        if (this.drawMiniProgress && this.miniDrawBitmap == null) {
            try {
                this.miniDrawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(48.0f), AndroidUtilities.dp(48.0f), Bitmap.Config.ARGB_8888);
                this.miniDrawCanvas = new Canvas(this.miniDrawBitmap);
            } catch (Throwable unused) {
            }
        }
        if (!z2) {
            this.parent.invalidate();
        } else {
            invalidateParent();
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
        if (this.previousDrawable == null && this.currentDrawable == null) {
            return 0.0f;
        }
        return this.animatedAlphaValue;
    }

    public void setOverrideAlpha(float f) {
        this.overrideAlpha = f;
    }

    public void draw(Canvas canvas) {
        Drawable drawable;
        float f;
        float f2;
        int i;
        int i2;
        float f3;
        Drawable drawable2;
        Canvas canvas2 = canvas;
        if (!this.drawMiniProgress || this.currentDrawable == null) {
            Drawable drawable3 = this.previousDrawable;
            if (drawable3 != null) {
                if (this.alphaForPrevious) {
                    drawable3.setAlpha((int) (this.animatedAlphaValue * 255.0f * this.overrideAlpha));
                } else {
                    drawable3.setAlpha((int) (this.overrideAlpha * 255.0f));
                }
                Drawable drawable4 = this.previousDrawable;
                RectF rectF = this.progressRect;
                drawable4.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                this.previousDrawable.draw(canvas2);
            }
            if (!this.hideCurrentDrawable && (drawable = this.currentDrawable) != null) {
                if (this.previousDrawable != null) {
                    drawable.setAlpha((int) ((1.0f - this.animatedAlphaValue) * 255.0f * this.overrideAlpha));
                } else {
                    drawable.setAlpha((int) (this.overrideAlpha * 255.0f));
                }
                Drawable drawable5 = this.currentDrawable;
                RectF rectF2 = this.progressRect;
                drawable5.setBounds((int) rectF2.left, (int) rectF2.top, (int) rectF2.right, (int) rectF2.bottom);
                this.currentDrawable.draw(canvas2);
            }
            if (this.currentWithRound || this.previousWithRound) {
                this.progressPaint.setColor(this.progressColor);
                if (this.previousWithRound) {
                    this.progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f * this.overrideAlpha));
                } else {
                    this.progressPaint.setAlpha((int) (this.overrideAlpha * 255.0f));
                }
                RectF rectF3 = this.cicleRect;
                RectF rectF4 = this.progressRect;
                float f4 = rectF4.left;
                int i3 = this.diff;
                rectF3.set(f4 + ((float) i3), rectF4.top + ((float) i3), rectF4.right - ((float) i3), rectF4.bottom - ((float) i3));
                canvas.drawArc(this.cicleRect, this.radOffset - 0.049804688f, Math.max(4.0f, this.animatedProgressValue * 360.0f), false, this.progressPaint);
                updateAnimation(true);
                return;
            }
            updateAnimation(false);
            return;
        }
        if (this.miniDrawCanvas != null) {
            this.miniDrawBitmap.eraseColor(0);
        }
        this.currentDrawable.setAlpha((int) (this.overrideAlpha * 255.0f));
        if (this.miniDrawCanvas != null) {
            this.currentDrawable.setBounds(0, 0, (int) this.progressRect.width(), (int) this.progressRect.height());
            this.currentDrawable.draw(this.miniDrawCanvas);
        } else {
            Drawable drawable6 = this.currentDrawable;
            RectF rectF5 = this.progressRect;
            drawable6.setBounds((int) rectF5.left, (int) rectF5.top, (int) rectF5.right, (int) rectF5.bottom);
            this.currentDrawable.draw(canvas2);
        }
        if (Math.abs(this.progressRect.width() - ((float) AndroidUtilities.dp(44.0f))) < AndroidUtilities.density) {
            i2 = 20;
            float f5 = (float) 16;
            float centerX = this.progressRect.centerX() + ((float) AndroidUtilities.dp(f5));
            f = this.progressRect.centerY() + ((float) AndroidUtilities.dp(f5));
            f2 = centerX;
            i = 0;
        } else {
            i2 = 22;
            float centerX2 = this.progressRect.centerX() + ((float) AndroidUtilities.dp(18.0f));
            f = this.progressRect.centerY() + ((float) AndroidUtilities.dp(18.0f));
            f2 = centerX2;
            i = 2;
        }
        int i4 = i2 / 2;
        if (this.previousMiniDrawable == null || !this.alphaForMiniPrevious) {
            f3 = 1.0f;
        } else {
            f3 = this.overrideAlpha * this.animatedAlphaValue;
        }
        Canvas canvas3 = this.miniDrawCanvas;
        if (canvas3 != null) {
            float f6 = (float) (i2 + 18 + i);
            canvas3.drawCircle((float) AndroidUtilities.dp(f6), (float) AndroidUtilities.dp(f6), ((float) AndroidUtilities.dp((float) (i4 + 1))) * f3, Theme.checkboxSquare_eraserPaint);
        } else {
            this.miniProgressBackgroundPaint.setColor(this.progressColor);
            if (this.previousMiniDrawable == null || this.currentMiniDrawable != null) {
                this.miniProgressBackgroundPaint.setAlpha(255);
            } else {
                this.miniProgressBackgroundPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f * this.overrideAlpha));
            }
            canvas2.drawCircle(f2, f, (float) AndroidUtilities.dp(12.0f), this.miniProgressBackgroundPaint);
        }
        if (this.miniDrawCanvas != null) {
            Bitmap bitmap = this.miniDrawBitmap;
            RectF rectF6 = this.progressRect;
            canvas2.drawBitmap(bitmap, (float) ((int) rectF6.left), (float) ((int) rectF6.top), (Paint) null);
        }
        Drawable drawable7 = this.previousMiniDrawable;
        if (drawable7 != null) {
            if (this.alphaForMiniPrevious) {
                drawable7.setAlpha((int) (this.animatedAlphaValue * 255.0f * this.overrideAlpha));
            } else {
                drawable7.setAlpha((int) (this.overrideAlpha * 255.0f));
            }
            float f7 = (float) i4;
            this.previousMiniDrawable.setBounds((int) (f2 - (((float) AndroidUtilities.dp(f7)) * f3)), (int) (f - (((float) AndroidUtilities.dp(f7)) * f3)), (int) ((((float) AndroidUtilities.dp(f7)) * f3) + f2), (int) ((((float) AndroidUtilities.dp(f7)) * f3) + f));
            this.previousMiniDrawable.draw(canvas2);
        }
        if (!this.hideCurrentDrawable && (drawable2 = this.currentMiniDrawable) != null) {
            if (this.previousMiniDrawable != null) {
                drawable2.setAlpha((int) ((1.0f - this.animatedAlphaValue) * 255.0f * this.overrideAlpha));
            } else {
                drawable2.setAlpha((int) (this.overrideAlpha * 255.0f));
            }
            float f8 = (float) i4;
            this.currentMiniDrawable.setBounds((int) (f2 - ((float) AndroidUtilities.dp(f8))), (int) (f - ((float) AndroidUtilities.dp(f8))), (int) (((float) AndroidUtilities.dp(f8)) + f2), (int) (((float) AndroidUtilities.dp(f8)) + f));
            this.currentMiniDrawable.draw(canvas2);
        }
        if (this.currentMiniWithRound || this.previousMiniWithRound) {
            this.miniProgressPaint.setColor(this.progressColor);
            if (this.previousMiniWithRound) {
                this.miniProgressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f * this.overrideAlpha));
            } else {
                this.miniProgressPaint.setAlpha((int) (this.overrideAlpha * 255.0f));
            }
            float f9 = (float) (i4 - 2);
            this.cicleRect.set(f2 - (((float) AndroidUtilities.dp(f9)) * f3), f - (((float) AndroidUtilities.dp(f9)) * f3), f2 + (((float) AndroidUtilities.dp(f9)) * f3), f + (((float) AndroidUtilities.dp(f9)) * f3));
            canvas.drawArc(this.cicleRect, this.radOffset - 0.049804688f, Math.max(4.0f, this.animatedProgressValue * 360.0f), false, this.miniProgressPaint);
            updateAnimation(true);
            return;
        }
        updateAnimation(false);
    }
}
