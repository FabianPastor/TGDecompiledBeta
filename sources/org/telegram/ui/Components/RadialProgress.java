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
            this.progress = z ? false : true;
        }

        public boolean updateAnimation(long j) {
            if (this.progress >= 1.0f) {
                return 0;
            }
            this.progress += ((float) j) / 700.0f;
            if (this.progress > NUM) {
                this.progress = 1.0f;
            }
            return 1;
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

    public void setStrikeWidth(int i) {
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
        if (this.checkBackgroundDrawable != null && ((this.currentDrawable == this.checkBackgroundDrawable || this.previousDrawable == this.checkBackgroundDrawable) && this.checkDrawable.updateAnimation(j))) {
            invalidateParent();
        }
        boolean z2 = false;
        if (z) {
            if (!this.animatedProgressValue) {
                this.radOffset += ((float) (360 * j)) / 3000.0f;
                z = this.currentProgress - this.animationProgressStart;
                if (z <= false) {
                    this.currentProgressTime += j;
                    if (this.currentProgressTime >= 300) {
                        this.animatedProgressValue = this.currentProgress;
                        this.animationProgressStart = this.currentProgress;
                        this.currentProgressTime = 0;
                    } else {
                        this.animatedProgressValue = this.animationProgressStart + (z * decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / 300.0f));
                    }
                }
                invalidateParent();
            }
            if (this.drawMiniProgress) {
                if (this.animatedProgressValue >= true && this.previousMiniDrawable) {
                    this.animatedAlphaValue -= ((float) j) / 200.0f;
                    if (this.animatedAlphaValue > false) {
                        this.animatedAlphaValue = 0.0f;
                        this.previousMiniDrawable = null;
                        if (this.currentMiniDrawable) {
                            z2 = true;
                        }
                        this.drawMiniProgress = z2;
                    }
                    invalidateParent();
                }
            } else if (this.animatedProgressValue >= true && this.previousDrawable) {
                this.animatedAlphaValue -= ((float) j) / 200.0f;
                if (this.animatedAlphaValue > false) {
                    this.animatedAlphaValue = 0.0f;
                    this.previousDrawable = null;
                }
                invalidateParent();
            }
        } else if (this.drawMiniProgress) {
            if (this.previousMiniDrawable) {
                this.animatedAlphaValue -= ((float) j) / 200.0f;
                if (this.animatedAlphaValue > false) {
                    this.animatedAlphaValue = 0.0f;
                    this.previousMiniDrawable = null;
                    if (this.currentMiniDrawable) {
                        z2 = true;
                    }
                    this.drawMiniProgress = z2;
                }
                invalidateParent();
            }
        } else if (this.previousDrawable) {
            this.animatedAlphaValue -= ((float) j) / 200.0f;
            if (this.animatedAlphaValue > false) {
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
        this.currentProgressTime = 0.0f;
        invalidateParent();
    }

    private void invalidateParent() {
        int dp = AndroidUtilities.dp(2.0f);
        int i = ((int) this.progressRect.left) - dp;
        int i2 = ((int) this.progressRect.top) - dp;
        dp *= 2;
        this.parent.invalidate(i, i2, ((int) this.progressRect.right) + dp, ((int) this.progressRect.bottom) + dp);
    }

    public void setCheckBackground(boolean z, boolean z2) {
        if (this.checkDrawable == null) {
            this.checkDrawable = new CheckDrawable();
            this.checkBackgroundDrawable = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), this.checkDrawable, 0);
        }
        Theme.setCombinedDrawableColor(this.checkBackgroundDrawable, Theme.getColor(Theme.key_chat_mediaLoaderPhoto), false);
        Theme.setCombinedDrawableColor(this.checkBackgroundDrawable, Theme.getColor(Theme.key_chat_mediaLoaderPhotoIcon), true);
        if (this.currentDrawable != this.checkBackgroundDrawable) {
            setBackground(this.checkBackgroundDrawable, z, z2);
            this.checkDrawable.resetProgress(z2);
        }
    }

    public boolean isDrawCheckDrawable() {
        return this.currentDrawable == this.checkBackgroundDrawable;
    }

    public void setBackground(Drawable drawable, boolean z, boolean z2) {
        this.lastUpdateTime = System.currentTimeMillis();
        if (!z2 || this.currentDrawable == drawable) {
            this.previousDrawable = null;
            this.previousWithRound = false;
        } else {
            this.previousDrawable = this.currentDrawable;
            this.previousWithRound = this.currentWithRound;
            this.animatedAlphaValue = 1.0f;
            setProgress(1.0f, z2);
        }
        this.currentWithRound = z;
        this.currentDrawable = drawable;
        if (z2) {
            invalidateParent();
        } else {
            this.parent.invalidate();
        }
    }

    public void setMiniBackground(android.graphics.drawable.Drawable r3, boolean r4, boolean r5) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r2 = this;
        r0 = java.lang.System.currentTimeMillis();
        r2.lastUpdateTime = r0;
        r0 = 0;
        if (r5 == 0) goto L_0x001d;
    L_0x0009:
        r1 = r2.currentMiniDrawable;
        if (r1 == r3) goto L_0x001d;
    L_0x000d:
        r1 = r2.currentMiniDrawable;
        r2.previousMiniDrawable = r1;
        r1 = r2.currentMiniWithRound;
        r2.previousMiniWithRound = r1;
        r1 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r2.animatedAlphaValue = r1;
        r2.setProgress(r1, r5);
        goto L_0x0022;
    L_0x001d:
        r1 = 0;
        r2.previousMiniDrawable = r1;
        r2.previousMiniWithRound = r0;
    L_0x0022:
        r2.currentMiniWithRound = r4;
        r2.currentMiniDrawable = r3;
        r3 = r2.previousMiniDrawable;
        if (r3 != 0) goto L_0x002e;
    L_0x002a:
        r3 = r2.currentMiniDrawable;
        if (r3 == 0) goto L_0x002f;
    L_0x002e:
        r0 = 1;
    L_0x002f:
        r2.drawMiniProgress = r0;
        r3 = r2.drawMiniProgress;
        if (r3 == 0) goto L_0x0054;
    L_0x0035:
        r3 = r2.miniDrawBitmap;
        if (r3 != 0) goto L_0x0054;
    L_0x0039:
        r3 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r3);	 Catch:{ Throwable -> 0x0054 }
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);	 Catch:{ Throwable -> 0x0054 }
        r0 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0054 }
        r3 = android.graphics.Bitmap.createBitmap(r4, r3, r0);	 Catch:{ Throwable -> 0x0054 }
        r2.miniDrawBitmap = r3;	 Catch:{ Throwable -> 0x0054 }
        r3 = new android.graphics.Canvas;	 Catch:{ Throwable -> 0x0054 }
        r4 = r2.miniDrawBitmap;	 Catch:{ Throwable -> 0x0054 }
        r3.<init>(r4);	 Catch:{ Throwable -> 0x0054 }
        r2.miniDrawCanvas = r3;	 Catch:{ Throwable -> 0x0054 }
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
            return null;
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
        if (this.previousDrawable == null) {
            if (this.currentDrawable == null) {
                return 0.0f;
            }
        }
        return this.animatedAlphaValue;
    }

    public void setOverrideAlpha(float f) {
        this.overrideAlpha = f;
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
            canvas2.drawArc(r0.cicleRect, r0.radOffset - 0.049804688f, Math.max(4.0f, r0.animatedProgressValue * 360.0f), false, r0.progressPaint);
            updateAnimation(true);
            return;
        }
        int i;
        float f;
        float centerY;
        int i2;
        float f2;
        float f3;
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
            i = 20;
            f = (float) 16;
            centerY = r0.progressRect.centerY() + ((float) AndroidUtilities.dp(f));
            f = r0.progressRect.centerX() + ((float) AndroidUtilities.dp(f));
            i2 = 0;
        } else {
            i = 22;
            centerY = r0.progressRect.centerY() + ((float) AndroidUtilities.dp(18.0f));
            f = r0.progressRect.centerX() + ((float) AndroidUtilities.dp(18.0f));
            i2 = 2;
        }
        int i3 = i / 2;
        if (r0.previousMiniDrawable == null || !r0.alphaForMiniPrevious) {
            f2 = 1.0f;
        } else {
            f2 = r0.overrideAlpha * r0.animatedAlphaValue;
        }
        if (r0.miniDrawCanvas != null) {
            float f4 = (float) ((18 + i) + i2);
            r0.miniDrawCanvas.drawCircle((float) AndroidUtilities.dp(f4), (float) AndroidUtilities.dp(f4), ((float) AndroidUtilities.dp((float) (i3 + 1))) * f2, Theme.checkboxSquare_eraserPaint);
        } else {
            r0.miniProgressBackgroundPaint.setColor(r0.progressColor);
            if (r0.previousMiniDrawable != null && r0.currentMiniDrawable == null) {
                r0.miniProgressBackgroundPaint.setAlpha((int) ((r0.animatedAlphaValue * 255.0f) * r0.overrideAlpha));
            } else if (r0.previousMiniDrawable == null || r0.currentMiniDrawable != null) {
                r0.miniProgressBackgroundPaint.setAlpha(255);
            } else {
                r0.miniProgressBackgroundPaint.setAlpha((int) (r0.overrideAlpha * 255.0f));
            }
            canvas2.drawCircle(f, centerY, (float) AndroidUtilities.dp(12.0f), r0.miniProgressBackgroundPaint);
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
            f3 = (float) i3;
            r0.previousMiniDrawable.setBounds((int) (f - (((float) AndroidUtilities.dp(f3)) * f2)), (int) (centerY - (((float) AndroidUtilities.dp(f3)) * f2)), (int) ((((float) AndroidUtilities.dp(f3)) * f2) + f), (int) ((((float) AndroidUtilities.dp(f3)) * f2) + centerY));
            r0.previousMiniDrawable.draw(canvas2);
        }
        if (!(r0.hideCurrentDrawable || r0.currentMiniDrawable == null)) {
            if (r0.previousMiniDrawable != null) {
                r0.currentMiniDrawable.setAlpha((int) (((1.0f - r0.animatedAlphaValue) * 255.0f) * r0.overrideAlpha));
            } else {
                r0.currentMiniDrawable.setAlpha((int) (r0.overrideAlpha * 255.0f));
            }
            f3 = (float) i3;
            r0.currentMiniDrawable.setBounds((int) (f - ((float) AndroidUtilities.dp(f3))), (int) (centerY - ((float) AndroidUtilities.dp(f3))), (int) (((float) AndroidUtilities.dp(f3)) + f), (int) (((float) AndroidUtilities.dp(f3)) + centerY));
            r0.currentMiniDrawable.draw(canvas2);
        }
        if (!r0.currentMiniWithRound) {
            if (!r0.previousMiniWithRound) {
                updateAnimation(false);
                return;
            }
        }
        r0.miniProgressPaint.setColor(r0.progressColor);
        if (r0.previousMiniWithRound) {
            r0.miniProgressPaint.setAlpha((int) ((255.0f * r0.animatedAlphaValue) * r0.overrideAlpha));
        } else {
            r0.miniProgressPaint.setAlpha((int) (255.0f * r0.overrideAlpha));
        }
        f3 = (float) (i3 - 2);
        r0.cicleRect.set(f - (((float) AndroidUtilities.dp(f3)) * f2), centerY - (((float) AndroidUtilities.dp(f3)) * f2), f + (((float) AndroidUtilities.dp(f3)) * f2), centerY + (((float) AndroidUtilities.dp(f3)) * f2));
        canvas2.drawArc(r0.cicleRect, r0.radOffset - 0.049804688f, Math.max(4.0f, r0.animatedProgressValue * 360.0f), false, r0.miniProgressPaint);
        updateAnimation(true);
    }
}
