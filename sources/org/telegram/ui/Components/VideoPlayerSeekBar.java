package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;

public class VideoPlayerSeekBar {
    private static Paint paint;
    private static Paint strokePaint;
    private static int thumbWidth;
    private float animateFromBufferedProgress;
    private boolean animateResetBuffering;
    private float animateThumbProgress = 1.0f;
    private int backgroundColor;
    private int backgroundSelectedColor;
    private float bufferedAnimationValue = 1.0f;
    private float bufferedProgress;
    private int cacheColor;
    private int circleColor;
    private float currentRadius;
    private SeekBarDelegate delegate;
    private int draggingThumbX = 0;
    private int fromThumbX = 0;
    private int height;
    private int horizontalPadding;
    private long lastUpdateTime;
    private int lineHeight = AndroidUtilities.dp(4.0f);
    private View parentView;
    private boolean pressed = false;
    private int progressColor;
    private RectF rect = new RectF();
    private boolean selected;
    private int smallLineColor;
    private int smallLineHeight = AndroidUtilities.dp(2.0f);
    private int thumbDX = 0;
    private int thumbX = 0;
    private float transitionProgress;
    private int width;

    public interface SeekBarDelegate {
        void onSeekBarContinuousDrag(float f);

        void onSeekBarDrag(float f);

        /* renamed from: org.telegram.ui.Components.VideoPlayerSeekBar$SeekBarDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onSeekBarContinuousDrag(SeekBarDelegate _this, float progress) {
            }
        }
    }

    public VideoPlayerSeekBar(View parent) {
        if (paint == null) {
            paint = new Paint(1);
            Paint paint2 = new Paint(1);
            strokePaint = paint2;
            paint2.setStyle(Paint.Style.STROKE);
            strokePaint.setColor(-16777216);
            strokePaint.setStrokeWidth(1.0f);
        }
        this.parentView = parent;
        thumbWidth = AndroidUtilities.dp(24.0f);
        this.currentRadius = (float) AndroidUtilities.dp(6.0f);
    }

    public void setDelegate(SeekBarDelegate seekBarDelegate) {
        this.delegate = seekBarDelegate;
    }

    public boolean onTouch(int action, float x, float y) {
        SeekBarDelegate seekBarDelegate;
        if (action == 0) {
            if (this.transitionProgress > 0.0f) {
                return false;
            }
            int i = this.height;
            int i2 = thumbWidth;
            int additionWidth = (i - i2) / 2;
            if (x >= ((float) (-additionWidth))) {
                int i3 = this.width;
                if (x <= ((float) (i3 + additionWidth)) && y >= 0.0f && y <= ((float) i)) {
                    int i4 = this.thumbX;
                    if (((float) (i4 - additionWidth)) > x || x > ((float) (i4 + i2 + additionWidth))) {
                        int i5 = ((int) x) - (i2 / 2);
                        this.thumbX = i5;
                        if (i5 < 0) {
                            this.thumbX = 0;
                        } else if (i5 > i3 - i2) {
                            this.thumbX = i2 - i3;
                        }
                    }
                    this.pressed = true;
                    int i6 = this.thumbX;
                    this.draggingThumbX = i6;
                    this.thumbDX = (int) (x - ((float) i6));
                    return true;
                }
            }
        } else if (action == 1 || action == 3) {
            if (this.pressed) {
                int i7 = this.draggingThumbX;
                this.thumbX = i7;
                if (action == 1 && (seekBarDelegate = this.delegate) != null) {
                    seekBarDelegate.onSeekBarDrag(((float) i7) / ((float) (this.width - thumbWidth)));
                }
                this.pressed = false;
                return true;
            }
        } else if (action == 2 && this.pressed) {
            int i8 = (int) (x - ((float) this.thumbDX));
            this.draggingThumbX = i8;
            if (i8 < 0) {
                this.draggingThumbX = 0;
            } else {
                int i9 = this.width;
                int i10 = thumbWidth;
                if (i8 > i9 - i10) {
                    this.draggingThumbX = i9 - i10;
                }
            }
            SeekBarDelegate seekBarDelegate2 = this.delegate;
            if (seekBarDelegate2 != null) {
                seekBarDelegate2.onSeekBarContinuousDrag(((float) this.draggingThumbX) / ((float) (this.width - thumbWidth)));
            }
            return true;
        }
        return false;
    }

    public void setColors(int background, int cache, int progress, int circle, int selected2, int smallLineColor2) {
        this.backgroundColor = background;
        this.cacheColor = cache;
        this.circleColor = circle;
        this.progressColor = progress;
        this.backgroundSelectedColor = selected2;
        this.smallLineColor = smallLineColor2;
    }

    public void setProgress(float progress, boolean animated) {
        int newThumb = (int) Math.ceil((double) (((float) (this.width - thumbWidth)) * progress));
        if (animated) {
            if (Math.abs(newThumb - this.thumbX) > AndroidUtilities.dp(10.0f)) {
                float progressInterpolated = CubicBezierInterpolator.DEFAULT.getInterpolation(this.animateThumbProgress);
                this.fromThumbX = (int) ((((float) this.thumbX) * progressInterpolated) + (((float) this.fromThumbX) * (1.0f - progressInterpolated)));
                this.animateThumbProgress = 0.0f;
            } else if (this.animateThumbProgress == 1.0f) {
                this.animateThumbProgress = 0.0f;
                this.fromThumbX = this.thumbX;
            }
        }
        this.thumbX = newThumb;
        if (newThumb < 0) {
            this.thumbX = 0;
            return;
        }
        int i = this.width;
        int i2 = thumbWidth;
        if (newThumb > i - i2) {
            this.thumbX = i - i2;
        }
    }

    public void setProgress(float progress) {
        setProgress(progress, false);
    }

    public void setBufferedProgress(float value) {
        float f = this.bufferedProgress;
        if (value != f) {
            this.animateFromBufferedProgress = f;
            this.animateResetBuffering = value < f;
            this.bufferedProgress = value;
            this.bufferedAnimationValue = 0.0f;
        }
    }

    public float getProgress() {
        return ((float) this.thumbX) / ((float) (this.width - thumbWidth));
    }

    public int getThumbX() {
        return (this.pressed ? this.draggingThumbX : this.thumbX) + (thumbWidth / 2);
    }

    public boolean isDragging() {
        return this.pressed;
    }

    public void setSelected(boolean value) {
        this.selected = value;
    }

    public void setSize(int w, int h) {
        this.width = w;
        this.height = h;
    }

    public int getWidth() {
        return this.width - thumbWidth;
    }

    public float getTransitionProgress() {
        return this.transitionProgress;
    }

    public void setTransitionProgress(float transitionProgress2) {
        if (this.transitionProgress != transitionProgress2) {
            this.transitionProgress = transitionProgress2;
            this.parentView.invalidate();
        }
    }

    public int getHorizontalPadding() {
        return this.horizontalPadding;
    }

    public void setHorizontalPadding(int horizontalPadding2) {
        this.horizontalPadding = horizontalPadding2;
    }

    public void draw(Canvas canvas, View view) {
        float radius = AndroidUtilities.lerp(((float) thumbWidth) / 2.0f, ((float) this.smallLineHeight) / 2.0f, this.transitionProgress);
        this.rect.left = ((float) this.horizontalPadding) + AndroidUtilities.lerp(((float) thumbWidth) / 2.0f, 0.0f, this.transitionProgress);
        RectF rectF = this.rect;
        int i = this.height;
        rectF.top = AndroidUtilities.lerp(((float) (i - this.lineHeight)) / 2.0f, (float) ((i - AndroidUtilities.dp(3.0f)) - this.smallLineHeight), this.transitionProgress);
        RectF rectF2 = this.rect;
        int i2 = this.height;
        rectF2.bottom = AndroidUtilities.lerp(((float) (this.lineHeight + i2)) / 2.0f, (float) (i2 - AndroidUtilities.dp(3.0f)), this.transitionProgress);
        float currentThumbX = (float) this.thumbX;
        float f = this.animateThumbProgress;
        if (f != 1.0f) {
            float f2 = f + 0.07272727f;
            this.animateThumbProgress = f2;
            if (f2 >= 1.0f) {
                this.animateThumbProgress = 1.0f;
            } else {
                view.invalidate();
                float progressInterpolated = CubicBezierInterpolator.DEFAULT.getInterpolation(this.animateThumbProgress);
                currentThumbX = (((float) this.fromThumbX) * (1.0f - progressInterpolated)) + (((float) this.thumbX) * progressInterpolated);
            }
        }
        this.rect.right = ((float) this.horizontalPadding) + AndroidUtilities.lerp(((float) this.width) - (((float) thumbWidth) / 2.0f), ((float) this.parentView.getWidth()) - (((float) this.horizontalPadding) * 2.0f), this.transitionProgress);
        setPaintColor(this.selected ? this.backgroundSelectedColor : this.backgroundColor, 1.0f - this.transitionProgress);
        canvas.drawRoundRect(this.rect, radius, radius, paint);
        float f3 = this.bufferedAnimationValue;
        if (f3 != 1.0f) {
            float f4 = f3 + 0.16f;
            this.bufferedAnimationValue = f4;
            if (f4 > 1.0f) {
                this.bufferedAnimationValue = 1.0f;
            } else {
                this.parentView.invalidate();
            }
        }
        if (this.animateResetBuffering) {
            float f5 = this.animateFromBufferedProgress;
            if (f5 > 0.0f) {
                RectF rectF3 = this.rect;
                int i3 = thumbWidth;
                rectF3.right = ((float) this.horizontalPadding) + AndroidUtilities.lerp((((float) i3) / 2.0f) + (f5 * ((float) (this.width - i3))), ((float) this.parentView.getWidth()) - (((float) this.horizontalPadding) * 2.0f), this.transitionProgress);
                setPaintColor(this.selected ? this.backgroundSelectedColor : this.cacheColor, (1.0f - this.transitionProgress) * (1.0f - this.bufferedAnimationValue));
                canvas.drawRoundRect(this.rect, radius, radius, paint);
            }
            float f6 = this.bufferedProgress;
            if (f6 > 0.0f) {
                RectF rectF4 = this.rect;
                int i4 = thumbWidth;
                rectF4.right = ((float) this.horizontalPadding) + AndroidUtilities.lerp((((float) i4) / 2.0f) + (f6 * ((float) (this.width - i4))), ((float) this.parentView.getWidth()) - (((float) this.horizontalPadding) * 2.0f), this.transitionProgress);
                setPaintColor(this.selected ? this.backgroundSelectedColor : this.cacheColor, 1.0f - this.transitionProgress);
                canvas.drawRoundRect(this.rect, radius, radius, paint);
            }
        } else {
            float f7 = this.animateFromBufferedProgress;
            float f8 = this.bufferedAnimationValue;
            float currentBufferedProgress = (f7 * (1.0f - f8)) + (this.bufferedProgress * f8);
            if (currentBufferedProgress > 0.0f) {
                RectF rectF5 = this.rect;
                int i5 = thumbWidth;
                rectF5.right = ((float) this.horizontalPadding) + AndroidUtilities.lerp((((float) i5) / 2.0f) + (((float) (this.width - i5)) * currentBufferedProgress), ((float) this.parentView.getWidth()) - (((float) this.horizontalPadding) * 2.0f), this.transitionProgress);
                setPaintColor(this.selected ? this.backgroundSelectedColor : this.cacheColor, 1.0f - this.transitionProgress);
                canvas.drawRoundRect(this.rect, radius, radius, paint);
            }
        }
        this.rect.right = ((float) this.horizontalPadding) + AndroidUtilities.lerp((((float) thumbWidth) / 2.0f) + (this.pressed ? (float) this.draggingThumbX : currentThumbX), (((float) this.parentView.getWidth()) - (((float) this.horizontalPadding) * 2.0f)) * getProgress(), this.transitionProgress);
        if (this.transitionProgress > 0.0f && this.rect.width() > 0.0f) {
            strokePaint.setAlpha((int) (this.transitionProgress * 255.0f * 0.2f));
            canvas.drawRoundRect(this.rect, radius, radius, strokePaint);
        }
        setPaintColor(ColorUtils.blendARGB(this.progressColor, this.smallLineColor, this.transitionProgress), 1.0f);
        canvas.drawRoundRect(this.rect, radius, radius, paint);
        setPaintColor(ColorUtils.blendARGB(this.circleColor, getProgress() == 0.0f ? 0 : this.smallLineColor, this.transitionProgress), 1.0f - this.transitionProgress);
        int newRad = AndroidUtilities.dp(this.pressed ? 8.0f : 6.0f);
        if (this.currentRadius != ((float) newRad)) {
            long newUpdateTime = SystemClock.elapsedRealtime();
            long dt = newUpdateTime - this.lastUpdateTime;
            this.lastUpdateTime = newUpdateTime;
            if (dt > 18) {
                dt = 16;
            }
            float f9 = this.currentRadius;
            if (f9 < ((float) newRad)) {
                float dp = f9 + (((float) AndroidUtilities.dp(1.0f)) * (((float) dt) / 60.0f));
                this.currentRadius = dp;
                if (dp > ((float) newRad)) {
                    this.currentRadius = (float) newRad;
                }
            } else {
                float dp2 = f9 - (((float) AndroidUtilities.dp(1.0f)) * (((float) dt) / 60.0f));
                this.currentRadius = dp2;
                if (dp2 < ((float) newRad)) {
                    this.currentRadius = (float) newRad;
                }
            }
            View view2 = this.parentView;
            if (view2 != null) {
                view2.invalidate();
            }
        }
        canvas.drawCircle(this.rect.right, this.rect.centerY(), AndroidUtilities.lerp(this.currentRadius, 0.0f, this.transitionProgress), paint);
    }

    private void setPaintColor(int color, float alpha) {
        if (alpha < 1.0f) {
            color = ColorUtils.setAlphaComponent(color, (int) (((float) Color.alpha(color)) * alpha));
        }
        paint.setColor(color);
    }
}
