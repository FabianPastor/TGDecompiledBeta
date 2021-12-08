package org.telegram.ui.ActionBar;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.MediaActionDrawable;

public class MenuDrawable extends Drawable {
    public static int TYPE_DEFAULT = 0;
    public static int TYPE_UDPATE_AVAILABLE = 1;
    public static int TYPE_UDPATE_DOWNLOADING = 2;
    private int alpha;
    private float animatedDownloadProgress;
    private boolean animationInProgress;
    private int backColor;
    private Paint backPaint;
    private int currentAnimationTime;
    private float currentRotation;
    private float downloadProgress;
    private float downloadProgressAnimationStart;
    private float downloadProgressTime;
    private float downloadRadOffset;
    private float finalRotation;
    private int iconColor;
    private DecelerateInterpolator interpolator;
    private long lastFrameTime;
    private boolean miniIcon;
    private Paint paint;
    private int previousType;
    private RectF rect;
    private boolean reverseAngle;
    private boolean rotateToBack;
    private int type;
    private float typeAnimationProgress;

    public MenuDrawable() {
        this(TYPE_DEFAULT);
    }

    public MenuDrawable(int type2) {
        this.paint = new Paint(1);
        this.backPaint = new Paint(1);
        this.rotateToBack = true;
        this.interpolator = new DecelerateInterpolator();
        this.rect = new RectF();
        this.alpha = 255;
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.backPaint.setStrokeWidth(AndroidUtilities.density * 1.66f);
        this.backPaint.setStrokeCap(Paint.Cap.ROUND);
        this.backPaint.setStyle(Paint.Style.STROKE);
        this.previousType = TYPE_DEFAULT;
        this.type = type2;
        this.typeAnimationProgress = 1.0f;
    }

    public void setRotateToBack(boolean value) {
        this.rotateToBack = value;
    }

    public float getCurrentRotation() {
        return this.currentRotation;
    }

    public void setRotation(float rotation, boolean animated) {
        this.lastFrameTime = 0;
        float f = this.currentRotation;
        if (f == 1.0f) {
            this.reverseAngle = true;
        } else if (f == 0.0f) {
            this.reverseAngle = false;
        }
        this.lastFrameTime = 0;
        if (animated) {
            if (f < rotation) {
                this.currentAnimationTime = (int) (f * 200.0f);
            } else {
                this.currentAnimationTime = (int) ((1.0f - f) * 200.0f);
            }
            this.lastFrameTime = SystemClock.elapsedRealtime();
            this.finalRotation = rotation;
        } else {
            this.currentRotation = rotation;
            this.finalRotation = rotation;
        }
        invalidateSelf();
    }

    public void setType(int value, boolean animated) {
        int i = this.type;
        if (i != value) {
            this.previousType = i;
            this.type = value;
            if (animated) {
                this.typeAnimationProgress = 0.0f;
            } else {
                this.typeAnimationProgress = 1.0f;
            }
            invalidateSelf();
        }
    }

    public void draw(Canvas canvas) {
        float diffMiddle;
        float diffUp;
        float startXDiff;
        float endXDiff;
        float endYDiff;
        float startYDiff;
        int backColor1;
        int backColor12;
        float rad;
        float cy;
        float cx;
        int backColor13;
        Canvas canvas2 = canvas;
        long newTime = SystemClock.elapsedRealtime();
        long j = this.lastFrameTime;
        long dt = newTime - j;
        float f = this.currentRotation;
        float f2 = this.finalRotation;
        if (f != f2) {
            if (j != 0) {
                int i = (int) (((long) this.currentAnimationTime) + dt);
                this.currentAnimationTime = i;
                if (i >= 200) {
                    this.currentRotation = f2;
                } else if (f < f2) {
                    this.currentRotation = this.interpolator.getInterpolation(((float) i) / 200.0f) * this.finalRotation;
                } else {
                    this.currentRotation = 1.0f - this.interpolator.getInterpolation(((float) i) / 200.0f);
                }
            }
            invalidateSelf();
        }
        float f3 = this.typeAnimationProgress;
        if (f3 < 1.0f) {
            float f4 = f3 + (((float) dt) / 200.0f);
            this.typeAnimationProgress = f4;
            if (f4 > 1.0f) {
                this.typeAnimationProgress = 1.0f;
            }
            invalidateSelf();
        }
        this.lastFrameTime = newTime;
        canvas.save();
        canvas2.translate((float) ((getIntrinsicWidth() / 2) - AndroidUtilities.dp(9.0f)), (float) (getIntrinsicHeight() / 2));
        int i2 = this.iconColor;
        if (i2 == 0) {
            i2 = Theme.getColor("actionBarDefaultIcon");
        }
        int color1 = i2;
        int i3 = this.backColor;
        if (i3 == 0) {
            i3 = Theme.getColor("actionBarDefault");
        }
        int backColor14 = i3;
        int i4 = this.type;
        int i5 = TYPE_DEFAULT;
        if (i4 == i5) {
            if (this.previousType != i5) {
                diffUp = ((float) AndroidUtilities.dp(9.0f)) * (1.0f - this.typeAnimationProgress);
                diffMiddle = ((float) AndroidUtilities.dp(7.0f)) * (1.0f - this.typeAnimationProgress);
            } else {
                diffUp = 0.0f;
                diffMiddle = 0.0f;
            }
        } else if (this.previousType == i5) {
            diffUp = ((float) AndroidUtilities.dp(9.0f)) * this.typeAnimationProgress * (1.0f - this.currentRotation);
            diffMiddle = ((float) AndroidUtilities.dp(7.0f)) * this.typeAnimationProgress * (1.0f - this.currentRotation);
        } else {
            diffUp = ((float) AndroidUtilities.dp(9.0f)) * (1.0f - this.currentRotation);
            diffMiddle = ((float) AndroidUtilities.dp(7.0f)) * (1.0f - this.currentRotation);
        }
        if (this.rotateToBack) {
            canvas2.rotate(this.currentRotation * ((float) (this.reverseAngle ? -180 : 180)), (float) AndroidUtilities.dp(9.0f), 0.0f);
            this.paint.setColor(color1);
            this.paint.setAlpha(this.alpha);
            int backColor15 = backColor14;
            canvas.drawLine(0.0f, 0.0f, (((float) AndroidUtilities.dp(18.0f)) - (((float) AndroidUtilities.dp(3.0f)) * this.currentRotation)) - diffMiddle, 0.0f, this.paint);
            float endYDiff2 = (((float) AndroidUtilities.dp(5.0f)) * (1.0f - Math.abs(this.currentRotation))) - (((float) AndroidUtilities.dp(0.5f)) * Math.abs(this.currentRotation));
            float endXDiff2 = ((float) AndroidUtilities.dp(18.0f)) - (((float) AndroidUtilities.dp(2.5f)) * Math.abs(this.currentRotation));
            endYDiff = endYDiff2;
            startYDiff = ((float) AndroidUtilities.dp(5.0f)) + (((float) AndroidUtilities.dp(2.0f)) * Math.abs(this.currentRotation));
            startXDiff = ((float) AndroidUtilities.dp(7.5f)) * Math.abs(this.currentRotation);
            backColor1 = backColor15;
            endXDiff = endXDiff2;
        } else {
            canvas2.rotate(this.currentRotation * ((float) (this.reverseAngle ? -225 : 135)), (float) AndroidUtilities.dp(9.0f), 0.0f);
            if (this.miniIcon) {
                this.paint.setColor(color1);
                this.paint.setAlpha(this.alpha);
                int backColor16 = backColor14;
                canvas.drawLine((((float) AndroidUtilities.dp(1.0f)) * this.currentRotation) + (AndroidUtilities.dpf2(2.0f) * (1.0f - Math.abs(this.currentRotation))), 0.0f, ((AndroidUtilities.dpf2(16.0f) * (1.0f - this.currentRotation)) + (((float) AndroidUtilities.dp(17.0f)) * this.currentRotation)) - diffMiddle, 0.0f, this.paint);
                float endYDiff3 = (AndroidUtilities.dpf2(5.0f) * (1.0f - Math.abs(this.currentRotation))) - (AndroidUtilities.dpf2(0.5f) * Math.abs(this.currentRotation));
                endXDiff = (AndroidUtilities.dpf2(16.0f) * (1.0f - Math.abs(this.currentRotation))) + (AndroidUtilities.dpf2(9.0f) * Math.abs(this.currentRotation));
                startYDiff = AndroidUtilities.dpf2(5.0f) + (AndroidUtilities.dpf2(3.0f) * Math.abs(this.currentRotation));
                startXDiff = AndroidUtilities.dpf2(2.0f) + (AndroidUtilities.dpf2(7.0f) * Math.abs(this.currentRotation));
                backColor1 = backColor16;
                endYDiff = endYDiff3;
            } else {
                int backColor17 = backColor14;
                int color2 = Theme.getColor("actionBarActionModeDefaultIcon");
                int backColor2 = Theme.getColor("actionBarActionModeDefault");
                int backColor18 = AndroidUtilities.getOffsetColor(backColor17, backColor2, this.currentRotation, 1.0f);
                this.paint.setColor(AndroidUtilities.getOffsetColor(color1, color2, this.currentRotation, 1.0f));
                this.paint.setAlpha(this.alpha);
                int i6 = backColor2;
                int i7 = color2;
                canvas.drawLine(this.currentRotation * ((float) AndroidUtilities.dp(1.0f)), 0.0f, (((float) AndroidUtilities.dp(18.0f)) - (((float) AndroidUtilities.dp(1.0f)) * this.currentRotation)) - diffMiddle, 0.0f, this.paint);
                float endYDiff4 = (((float) AndroidUtilities.dp(5.0f)) * (1.0f - Math.abs(this.currentRotation))) - (((float) AndroidUtilities.dp(0.5f)) * Math.abs(this.currentRotation));
                endXDiff = ((float) AndroidUtilities.dp(18.0f)) - (((float) AndroidUtilities.dp(9.0f)) * Math.abs(this.currentRotation));
                startYDiff = ((float) AndroidUtilities.dp(5.0f)) + (((float) AndroidUtilities.dp(3.0f)) * Math.abs(this.currentRotation));
                startXDiff = ((float) AndroidUtilities.dp(9.0f)) * Math.abs(this.currentRotation);
                backColor1 = backColor18;
                endYDiff = endYDiff4;
            }
        }
        if (this.miniIcon) {
            Canvas canvas3 = canvas;
            float f5 = startXDiff;
            float f6 = endXDiff;
            backColor12 = backColor1;
            canvas3.drawLine(f5, -startYDiff, f6, -endYDiff, this.paint);
            canvas3.drawLine(f5, startYDiff, f6, endYDiff, this.paint);
        } else {
            backColor12 = backColor1;
            Canvas canvas4 = canvas;
            float f7 = startXDiff;
            canvas4.drawLine(f7, -startYDiff, endXDiff - diffUp, -endYDiff, this.paint);
            canvas4.drawLine(f7, startYDiff, endXDiff, endYDiff, this.paint);
        }
        int i8 = this.type;
        int i9 = TYPE_DEFAULT;
        if ((i8 == i9 || this.currentRotation == 1.0f) && (this.previousType == i9 || this.typeAnimationProgress == 1.0f)) {
        } else {
            float cx2 = (float) AndroidUtilities.dp(17.0f);
            float cy2 = (float) (-AndroidUtilities.dp(4.5f));
            float rad2 = AndroidUtilities.density * 5.5f;
            float f8 = this.currentRotation;
            canvas2.scale(1.0f - f8, 1.0f - f8, cx2, cy2);
            if (this.type == TYPE_DEFAULT) {
                rad = rad2 * (1.0f - this.typeAnimationProgress);
            } else {
                rad = rad2;
            }
            int backColor19 = backColor12;
            this.backPaint.setColor(backColor19);
            this.backPaint.setAlpha(this.alpha);
            canvas2.drawCircle(cx2, cy2, rad, this.paint);
            int i10 = this.type;
            int i11 = TYPE_UDPATE_AVAILABLE;
            if (i10 == i11 || this.previousType == i11) {
                this.backPaint.setStrokeWidth(AndroidUtilities.density * 1.66f);
                if (this.previousType == TYPE_UDPATE_AVAILABLE) {
                    backColor13 = backColor19;
                    this.backPaint.setAlpha((int) (((float) this.alpha) * (1.0f - this.typeAnimationProgress)));
                } else {
                    backColor13 = backColor19;
                    this.backPaint.setAlpha(this.alpha);
                }
                int i12 = backColor13;
                float f9 = rad;
                cy = cy2;
                cx = cx2;
                canvas.drawLine(cx2, cy2 - ((float) AndroidUtilities.dp(2.0f)), cx2, cy2, this.backPaint);
                canvas2.drawPoint(cx, cy + ((float) AndroidUtilities.dp(2.5f)), this.backPaint);
            } else {
                int i13 = backColor19;
                float var_ = rad;
                cy = cy2;
                cx = cx2;
            }
            int i14 = this.type;
            int i15 = TYPE_UDPATE_DOWNLOADING;
            if (i14 == i15 || this.previousType == i15) {
                this.backPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
                if (this.previousType == TYPE_UDPATE_DOWNLOADING) {
                    this.backPaint.setAlpha((int) (((float) this.alpha) * (1.0f - this.typeAnimationProgress)));
                } else {
                    this.backPaint.setAlpha(this.alpha);
                }
                float arcRad = Math.max(4.0f, this.animatedDownloadProgress * 360.0f);
                this.rect.set(cx - ((float) AndroidUtilities.dp(3.0f)), cy - ((float) AndroidUtilities.dp(3.0f)), ((float) AndroidUtilities.dp(3.0f)) + cx, cy + ((float) AndroidUtilities.dp(3.0f)));
                canvas.drawArc(this.rect, this.downloadRadOffset, arcRad, false, this.backPaint);
                float var_ = this.downloadRadOffset + (((float) (360 * dt)) / 2500.0f);
                this.downloadRadOffset = var_;
                this.downloadRadOffset = MediaActionDrawable.getCircleValue(var_);
                float var_ = this.downloadProgress;
                float var_ = this.downloadProgressAnimationStart;
                float progressDiff = var_ - var_;
                if (progressDiff > 0.0f) {
                    float var_ = this.downloadProgressTime + ((float) dt);
                    this.downloadProgressTime = var_;
                    if (var_ >= 200.0f) {
                        this.animatedDownloadProgress = var_;
                        this.downloadProgressAnimationStart = var_;
                        this.downloadProgressTime = 0.0f;
                    } else {
                        this.animatedDownloadProgress = var_ + (this.interpolator.getInterpolation(var_ / 200.0f) * progressDiff);
                    }
                }
                invalidateSelf();
            }
        }
        canvas.restore();
    }

    public void setUpdateDownloadProgress(float value, boolean animated) {
        if (!animated) {
            this.animatedDownloadProgress = value;
            this.downloadProgressAnimationStart = value;
        } else {
            if (this.animatedDownloadProgress > value) {
                this.animatedDownloadProgress = value;
            }
            this.downloadProgressAnimationStart = this.animatedDownloadProgress;
        }
        this.downloadProgress = value;
        this.downloadProgressTime = 0.0f;
        invalidateSelf();
    }

    public void setAlpha(int alpha2) {
        if (this.alpha != alpha2) {
            this.alpha = alpha2;
            this.paint.setAlpha(alpha2);
            this.backPaint.setAlpha(alpha2);
            invalidateSelf();
        }
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public int getOpacity() {
        return -2;
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(24.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(24.0f);
    }

    public void setIconColor(int iconColor2) {
        this.iconColor = iconColor2;
    }

    public void setBackColor(int backColor2) {
        this.backColor = backColor2;
    }

    public void setRoundCap() {
        this.paint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setMiniIcon(boolean miniIcon2) {
        this.miniIcon = miniIcon2;
    }
}
