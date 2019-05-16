package org.telegram.ui.Components;

import android.graphics.Bitmap;
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

        public int getOpacity() {
            return -2;
        }

        public CheckDrawable() {
            this.paint.setStyle(Style.STROKE);
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
            this.paint.setStrokeCap(Cap.ROUND);
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
            if (this.progress > 1.0f) {
                this.progress = 1.0f;
            }
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
            Canvas canvas3 = canvas;
            canvas3.drawLine((float) (((int) AndroidUtilities.dpf2(7.0f)) + centerX), (float) (((int) AndroidUtilities.dpf2(13.0f)) + centerY), (float) (centerX + ((int) (AndroidUtilities.dpf2(7.0f) + (((float) AndroidUtilities.dp(13.0f)) * f)))), (float) (centerY + ((int) (AndroidUtilities.dpf2(13.0f) - (((float) AndroidUtilities.dp(13.0f)) * f)))), this.paint);
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
        this.progressPaint.setStyle(Style.STROKE);
        this.progressPaint.setStrokeCap(Cap.ROUND);
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.miniProgressPaint = new Paint(1);
        this.miniProgressPaint.setStyle(Style.STROKE);
        this.miniProgressPaint.setStrokeCap(Cap.ROUND);
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
        if (z) {
            if (this.animatedProgressValue > f) {
                this.animatedProgressValue = f;
            }
            this.animationProgressStart = this.animatedProgressValue;
        } else {
            this.animatedProgressValue = f;
            this.animationProgressStart = f;
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
        dp *= 2;
        view.invalidate(i, i2, ((int) rectF.right) + dp, ((int) rectF.bottom) + dp);
    }

    public void setCheckBackground(boolean z, boolean z2) {
        if (this.checkDrawable == null) {
            this.checkDrawable = new CheckDrawable();
            this.checkBackgroundDrawable = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), this.checkDrawable, 0);
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

    /* JADX WARNING: Removed duplicated region for block: B:9:0x002c  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0026  */
    public void setBackground(android.graphics.drawable.Drawable r3, boolean r4, boolean r5) {
        /*
        r2 = this;
        r0 = java.lang.System.currentTimeMillis();
        r2.lastUpdateTime = r0;
        if (r5 == 0) goto L_0x001a;
    L_0x0008:
        r0 = r2.currentDrawable;
        if (r0 == r3) goto L_0x001a;
    L_0x000c:
        r2.previousDrawable = r0;
        r0 = r2.currentWithRound;
        r2.previousWithRound = r0;
        r0 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2.animatedAlphaValue = r0;
        r2.setProgress(r0, r5);
        goto L_0x0020;
    L_0x001a:
        r0 = 0;
        r2.previousDrawable = r0;
        r0 = 0;
        r2.previousWithRound = r0;
    L_0x0020:
        r2.currentWithRound = r4;
        r2.currentDrawable = r3;
        if (r5 != 0) goto L_0x002c;
    L_0x0026:
        r3 = r2.parent;
        r3.invalidate();
        goto L_0x002f;
    L_0x002c:
        r2.invalidateParent();
    L_0x002f:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RadialProgress.setBackground(android.graphics.drawable.Drawable, boolean, boolean):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x005c  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0056  */
    public void setMiniBackground(android.graphics.drawable.Drawable r3, boolean r4, boolean r5) {
        /*
        r2 = this;
        r0 = java.lang.System.currentTimeMillis();
        r2.lastUpdateTime = r0;
        r0 = 0;
        if (r5 == 0) goto L_0x001b;
    L_0x0009:
        r1 = r2.currentMiniDrawable;
        if (r1 == r3) goto L_0x001b;
    L_0x000d:
        r2.previousMiniDrawable = r1;
        r1 = r2.currentMiniWithRound;
        r2.previousMiniWithRound = r1;
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2.animatedAlphaValue = r1;
        r2.setProgress(r1, r5);
        goto L_0x0020;
    L_0x001b:
        r1 = 0;
        r2.previousMiniDrawable = r1;
        r2.previousMiniWithRound = r0;
    L_0x0020:
        r2.currentMiniWithRound = r4;
        r2.currentMiniDrawable = r3;
        r3 = r2.previousMiniDrawable;
        if (r3 != 0) goto L_0x002c;
    L_0x0028:
        r3 = r2.currentMiniDrawable;
        if (r3 == 0) goto L_0x002d;
    L_0x002c:
        r0 = 1;
    L_0x002d:
        r2.drawMiniProgress = r0;
        r3 = r2.drawMiniProgress;
        if (r3 == 0) goto L_0x0054;
    L_0x0033:
        r3 = r2.miniDrawBitmap;
        if (r3 != 0) goto L_0x0054;
    L_0x0037:
        r3 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r3);	 Catch:{ Throwable -> 0x0053 }
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);	 Catch:{ Throwable -> 0x0053 }
        r0 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0053 }
        r3 = android.graphics.Bitmap.createBitmap(r4, r3, r0);	 Catch:{ Throwable -> 0x0053 }
        r2.miniDrawBitmap = r3;	 Catch:{ Throwable -> 0x0053 }
        r3 = new android.graphics.Canvas;	 Catch:{ Throwable -> 0x0053 }
        r4 = r2.miniDrawBitmap;	 Catch:{ Throwable -> 0x0053 }
        r3.<init>(r4);	 Catch:{ Throwable -> 0x0053 }
        r2.miniDrawCanvas = r3;	 Catch:{ Throwable -> 0x0053 }
        goto L_0x0054;
    L_0x0054:
        if (r5 != 0) goto L_0x005c;
    L_0x0056:
        r3 = r2.parent;
        r3.invalidate();
        goto L_0x005f;
    L_0x005c:
        r2.invalidateParent();
    L_0x005f:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RadialProgress.setMiniBackground(android.graphics.drawable.Drawable, boolean, boolean):void");
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

    public void setOverrideAlpha(float f) {
        this.overrideAlpha = f;
    }

    public void draw(Canvas canvas) {
        Canvas canvas2 = canvas;
        Drawable drawable;
        RectF rectF;
        if (!this.drawMiniProgress || this.currentDrawable == null) {
            drawable = this.previousDrawable;
            if (drawable != null) {
                if (this.alphaForPrevious) {
                    drawable.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.overrideAlpha));
                } else {
                    drawable.setAlpha((int) (this.overrideAlpha * 255.0f));
                }
                drawable = this.previousDrawable;
                rectF = this.progressRect;
                drawable.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                this.previousDrawable.draw(canvas2);
            }
            if (!this.hideCurrentDrawable) {
                drawable = this.currentDrawable;
                if (drawable != null) {
                    if (this.previousDrawable != null) {
                        drawable.setAlpha((int) (((1.0f - this.animatedAlphaValue) * 255.0f) * this.overrideAlpha));
                    } else {
                        drawable.setAlpha((int) (this.overrideAlpha * 255.0f));
                    }
                    drawable = this.currentDrawable;
                    rectF = this.progressRect;
                    drawable.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                    this.currentDrawable.draw(canvas2);
                }
            }
            if (this.currentWithRound || this.previousWithRound) {
                this.progressPaint.setColor(this.progressColor);
                if (this.previousWithRound) {
                    this.progressPaint.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.overrideAlpha));
                } else {
                    this.progressPaint.setAlpha((int) (this.overrideAlpha * 255.0f));
                }
                RectF rectF2 = this.cicleRect;
                rectF = this.progressRect;
                float f = rectF.left;
                int i = this.diff;
                rectF2.set(f + ((float) i), rectF.top + ((float) i), rectF.right - ((float) i), rectF.bottom - ((float) i));
                canvas.drawArc(this.cicleRect, this.radOffset - 0.049804688f, Math.max(4.0f, this.animatedProgressValue * 360.0f), false, this.progressPaint);
                updateAnimation(true);
                return;
            }
            updateAnimation(false);
            return;
        }
        int i2;
        float f2;
        float centerY;
        int i3;
        float f3;
        float f4;
        if (this.miniDrawCanvas != null) {
            this.miniDrawBitmap.eraseColor(0);
        }
        this.currentDrawable.setAlpha((int) (this.overrideAlpha * 255.0f));
        if (this.miniDrawCanvas != null) {
            this.currentDrawable.setBounds(0, 0, (int) this.progressRect.width(), (int) this.progressRect.height());
            this.currentDrawable.draw(this.miniDrawCanvas);
        } else {
            drawable = this.currentDrawable;
            RectF rectF3 = this.progressRect;
            drawable.setBounds((int) rectF3.left, (int) rectF3.top, (int) rectF3.right, (int) rectF3.bottom);
            this.currentDrawable.draw(canvas2);
        }
        if (Math.abs(this.progressRect.width() - ((float) AndroidUtilities.dp(44.0f))) < AndroidUtilities.density) {
            i2 = 20;
            f2 = (float) 16;
            centerY = this.progressRect.centerY() + ((float) AndroidUtilities.dp(f2));
            f2 = this.progressRect.centerX() + ((float) AndroidUtilities.dp(f2));
            i3 = 0;
        } else {
            i2 = 22;
            centerY = this.progressRect.centerY() + ((float) AndroidUtilities.dp(18.0f));
            f2 = this.progressRect.centerX() + ((float) AndroidUtilities.dp(18.0f));
            i3 = 2;
        }
        int i4 = i2 / 2;
        if (this.previousMiniDrawable == null || !this.alphaForMiniPrevious) {
            f3 = 1.0f;
        } else {
            f3 = this.overrideAlpha * this.animatedAlphaValue;
        }
        Canvas canvas3 = this.miniDrawCanvas;
        if (canvas3 != null) {
            float f5 = (float) ((i2 + 18) + i3);
            canvas3.drawCircle((float) AndroidUtilities.dp(f5), (float) AndroidUtilities.dp(f5), ((float) AndroidUtilities.dp((float) (i4 + 1))) * f3, Theme.checkboxSquare_eraserPaint);
        } else {
            this.miniProgressBackgroundPaint.setColor(this.progressColor);
            if (this.previousMiniDrawable == null || this.currentMiniDrawable != null) {
                this.miniProgressBackgroundPaint.setAlpha(255);
            } else {
                this.miniProgressBackgroundPaint.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.overrideAlpha));
            }
            canvas2.drawCircle(f2, centerY, (float) AndroidUtilities.dp(12.0f), this.miniProgressBackgroundPaint);
        }
        if (this.miniDrawCanvas != null) {
            Bitmap bitmap = this.miniDrawBitmap;
            rectF = this.progressRect;
            canvas2.drawBitmap(bitmap, (float) ((int) rectF.left), (float) ((int) rectF.top), null);
        }
        drawable = this.previousMiniDrawable;
        if (drawable != null) {
            if (this.alphaForMiniPrevious) {
                drawable.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.overrideAlpha));
            } else {
                drawable.setAlpha((int) (this.overrideAlpha * 255.0f));
            }
            f4 = (float) i4;
            this.previousMiniDrawable.setBounds((int) (f2 - (((float) AndroidUtilities.dp(f4)) * f3)), (int) (centerY - (((float) AndroidUtilities.dp(f4)) * f3)), (int) ((((float) AndroidUtilities.dp(f4)) * f3) + f2), (int) ((((float) AndroidUtilities.dp(f4)) * f3) + centerY));
            this.previousMiniDrawable.draw(canvas2);
        }
        if (!this.hideCurrentDrawable) {
            drawable = this.currentMiniDrawable;
            if (drawable != null) {
                if (this.previousMiniDrawable != null) {
                    drawable.setAlpha((int) (((1.0f - this.animatedAlphaValue) * 255.0f) * this.overrideAlpha));
                } else {
                    drawable.setAlpha((int) (this.overrideAlpha * 255.0f));
                }
                f4 = (float) i4;
                this.currentMiniDrawable.setBounds((int) (f2 - ((float) AndroidUtilities.dp(f4))), (int) (centerY - ((float) AndroidUtilities.dp(f4))), (int) (((float) AndroidUtilities.dp(f4)) + f2), (int) (((float) AndroidUtilities.dp(f4)) + centerY));
                this.currentMiniDrawable.draw(canvas2);
            }
        }
        if (this.currentMiniWithRound || this.previousMiniWithRound) {
            this.miniProgressPaint.setColor(this.progressColor);
            if (this.previousMiniWithRound) {
                this.miniProgressPaint.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.overrideAlpha));
            } else {
                this.miniProgressPaint.setAlpha((int) (this.overrideAlpha * 255.0f));
            }
            f4 = (float) (i4 - 2);
            this.cicleRect.set(f2 - (((float) AndroidUtilities.dp(f4)) * f3), centerY - (((float) AndroidUtilities.dp(f4)) * f3), f2 + (((float) AndroidUtilities.dp(f4)) * f3), centerY + (((float) AndroidUtilities.dp(f4)) * f3));
            canvas.drawArc(this.cicleRect, this.radOffset - 0.049804688f, Math.max(4.0f, this.animatedProgressValue * 360.0f), false, this.miniProgressPaint);
            updateAnimation(true);
            return;
        }
        updateAnimation(false);
    }
}
