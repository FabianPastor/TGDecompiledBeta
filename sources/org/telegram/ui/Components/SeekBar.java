package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;

public class SeekBar {
    private static Paint paint;
    private static int thumbWidth;
    private int backgroundColor;
    private int backgroundSelectedColor;
    private float bufferedProgress;
    private int cacheColor;
    private int circleColor;
    private float currentRadius;
    private SeekBarDelegate delegate;
    private int draggingThumbX = 0;
    private int height;
    private long lastUpdateTime;
    private int lineHeight = AndroidUtilities.dp(2.0f);
    private View parentView;
    private boolean pressed = false;
    private int progressColor;
    private RectF rect = new RectF();
    private boolean selected;
    private int thumbDX = 0;
    private int thumbX = 0;
    private int width;

    public interface SeekBarDelegate {

        public final /* synthetic */ class -CC {
            public static void $default$onSeekBarContinuousDrag(SeekBarDelegate seekBarDelegate, float f) {
            }
        }

        void onSeekBarContinuousDrag(float f);

        void onSeekBarDrag(float f);
    }

    public SeekBar(View view) {
        if (paint == null) {
            paint = new Paint(1);
        }
        this.parentView = view;
        thumbWidth = AndroidUtilities.dp(24.0f);
        this.currentRadius = (float) AndroidUtilities.dp(6.0f);
    }

    public void setDelegate(SeekBarDelegate seekBarDelegate) {
        this.delegate = seekBarDelegate;
    }

    public boolean onTouch(int i, float f, float f2) {
        int i2;
        SeekBarDelegate seekBarDelegate;
        if (i == 0) {
            i = this.height;
            int i3 = thumbWidth;
            int i4 = (i - i3) / 2;
            if (f >= ((float) (-i4)) && f <= ((float) (this.width + i4)) && f2 >= 0.0f && f2 <= ((float) i)) {
                i = this.thumbX;
                if (((float) (i - i4)) > f || f > ((float) ((i + i3) + i4))) {
                    i = (int) f;
                    i2 = thumbWidth;
                    this.thumbX = i - (i2 / 2);
                    i = this.thumbX;
                    if (i < 0) {
                        this.thumbX = 0;
                    } else {
                        int i5 = this.width;
                        if (i > i5 - i2) {
                            this.thumbX = i2 - i5;
                        }
                    }
                }
                this.pressed = true;
                i = this.thumbX;
                this.draggingThumbX = i;
                this.thumbDX = (int) (f - ((float) i));
                return true;
            }
        } else if (i == 1 || i == 3) {
            if (this.pressed) {
                this.thumbX = this.draggingThumbX;
                if (i == 1) {
                    seekBarDelegate = this.delegate;
                    if (seekBarDelegate != null) {
                        seekBarDelegate.onSeekBarDrag(((float) this.thumbX) / ((float) (this.width - thumbWidth)));
                    }
                }
                this.pressed = false;
                return true;
            }
        } else if (i == 2 && this.pressed) {
            this.draggingThumbX = (int) (f - ((float) this.thumbDX));
            i = this.draggingThumbX;
            if (i < 0) {
                this.draggingThumbX = 0;
            } else {
                int i6 = this.width;
                i2 = thumbWidth;
                if (i > i6 - i2) {
                    this.draggingThumbX = i6 - i2;
                }
            }
            seekBarDelegate = this.delegate;
            if (seekBarDelegate != null) {
                seekBarDelegate.onSeekBarContinuousDrag(((float) this.draggingThumbX) / ((float) (this.width - thumbWidth)));
            }
            return true;
        }
        return false;
    }

    public void setColors(int i, int i2, int i3, int i4, int i5) {
        this.backgroundColor = i;
        this.cacheColor = i2;
        this.circleColor = i4;
        this.progressColor = i3;
        this.backgroundSelectedColor = i5;
    }

    public void setProgress(float f) {
        this.thumbX = (int) Math.ceil((double) (((float) (this.width - thumbWidth)) * f));
        int i = this.thumbX;
        if (i < 0) {
            this.thumbX = 0;
            return;
        }
        int i2 = this.width;
        int i3 = thumbWidth;
        if (i > i2 - i3) {
            this.thumbX = i2 - i3;
        }
    }

    public void setBufferedProgress(float f) {
        this.bufferedProgress = f;
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

    public void setSelected(boolean z) {
        this.selected = z;
    }

    public void setSize(int i, int i2) {
        this.width = i;
        this.height = i2;
    }

    public int getWidth() {
        return this.width - thumbWidth;
    }

    public void setLineHeight(int i) {
        this.lineHeight = i;
    }

    public void draw(Canvas canvas) {
        RectF rectF = this.rect;
        int i = thumbWidth;
        float f = (float) (i / 2);
        int i2 = this.height;
        int i3 = i2 / 2;
        int i4 = this.lineHeight;
        rectF.set(f, (float) (i3 - (i4 / 2)), (float) (this.width - (i / 2)), (float) ((i2 / 2) + (i4 / 2)));
        paint.setColor(this.selected ? this.backgroundSelectedColor : this.backgroundColor);
        rectF = this.rect;
        i = thumbWidth;
        canvas.drawRoundRect(rectF, (float) (i / 2), (float) (i / 2), paint);
        if (this.bufferedProgress > 0.0f) {
            paint.setColor(this.selected ? this.backgroundSelectedColor : this.cacheColor);
            rectF = this.rect;
            i = thumbWidth;
            f = (float) (i / 2);
            i2 = this.height;
            i3 = i2 / 2;
            i4 = this.lineHeight;
            rectF.set(f, (float) (i3 - (i4 / 2)), ((float) (i / 2)) + (this.bufferedProgress * ((float) (this.width - i))), (float) ((i2 / 2) + (i4 / 2)));
            rectF = this.rect;
            i = thumbWidth;
            canvas.drawRoundRect(rectF, (float) (i / 2), (float) (i / 2), paint);
        }
        rectF = this.rect;
        i = thumbWidth;
        rectF.set((float) (i / 2), (float) ((this.height / 2) - (this.lineHeight / 2)), (float) ((i / 2) + (this.pressed ? this.draggingThumbX : this.thumbX)), (float) ((this.height / 2) + (this.lineHeight / 2)));
        paint.setColor(this.progressColor);
        rectF = this.rect;
        i = thumbWidth;
        canvas.drawRoundRect(rectF, (float) (i / 2), (float) (i / 2), paint);
        paint.setColor(this.circleColor);
        float dp = (float) AndroidUtilities.dp(this.pressed ? 8.0f : 6.0f);
        if (this.currentRadius != dp) {
            long elapsedRealtime = SystemClock.elapsedRealtime() - this.lastUpdateTime;
            if (elapsedRealtime > 18) {
                elapsedRealtime = 16;
            }
            float f2 = this.currentRadius;
            if (f2 < dp) {
                this.currentRadius = f2 + (((float) AndroidUtilities.dp(1.0f)) * (((float) elapsedRealtime) / 60.0f));
                if (this.currentRadius > dp) {
                    this.currentRadius = dp;
                }
            } else {
                this.currentRadius = f2 - (((float) AndroidUtilities.dp(1.0f)) * (((float) elapsedRealtime) / 60.0f));
                if (this.currentRadius < dp) {
                    this.currentRadius = dp;
                }
            }
            View view = this.parentView;
            if (view != null) {
                view.invalidate();
            }
        }
        canvas.drawCircle((float) ((this.pressed ? this.draggingThumbX : this.thumbX) + (thumbWidth / 2)), (float) (this.height / 2), this.currentRadius, paint);
    }
}
