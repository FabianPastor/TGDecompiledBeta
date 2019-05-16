package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import org.telegram.messenger.AndroidUtilities;

public class SeekBar {
    private static Paint paint;
    private static int thumbWidth;
    private int backgroundColor;
    private int backgroundSelectedColor;
    private float bufferedProgress;
    private int cacheColor;
    private int circleColor;
    private SeekBarDelegate delegate;
    private int height;
    private int lineHeight = AndroidUtilities.dp(2.0f);
    private boolean pressed = false;
    private int progressColor;
    private RectF rect = new RectF();
    private boolean selected;
    private int thumbDX = 0;
    private int thumbX = 0;
    private int width;

    public interface SeekBarDelegate {
        void onSeekBarDrag(float f);
    }

    public SeekBar(Context context) {
        if (paint == null) {
            paint = new Paint(1);
            thumbWidth = AndroidUtilities.dp(24.0f);
        }
    }

    public void setDelegate(SeekBarDelegate seekBarDelegate) {
        this.delegate = seekBarDelegate;
    }

    public boolean onTouch(int i, float f, float f2) {
        if (i == 0) {
            i = this.height;
            int i2 = thumbWidth;
            int i3 = (i - i2) / 2;
            int i4 = this.thumbX;
            if (((float) (i4 - i3)) <= f && f <= ((float) ((i2 + i4) + i3)) && f2 >= 0.0f && f2 <= ((float) i)) {
                this.pressed = true;
                this.thumbDX = (int) (f - ((float) i4));
                return true;
            }
        } else if (i == 1 || i == 3) {
            if (this.pressed) {
                if (i == 1) {
                    SeekBarDelegate seekBarDelegate = this.delegate;
                    if (seekBarDelegate != null) {
                        seekBarDelegate.onSeekBarDrag(((float) this.thumbX) / ((float) (this.width - thumbWidth)));
                    }
                }
                this.pressed = false;
                return true;
            }
        } else if (i == 2 && this.pressed) {
            this.thumbX = (int) (f - ((float) this.thumbDX));
            i = this.thumbX;
            if (i < 0) {
                this.thumbX = 0;
            } else {
                int i5 = this.width;
                int i6 = thumbWidth;
                if (i > i5 - i6) {
                    this.thumbX = i5 - i6;
                }
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
        f = (float) (i / 2);
        i2 = this.height;
        i3 = i2 / 2;
        i4 = this.lineHeight;
        rectF.set(f, (float) (i3 - (i4 / 2)), (float) ((i / 2) + this.thumbX), (float) ((i2 / 2) + (i4 / 2)));
        paint.setColor(this.progressColor);
        rectF = this.rect;
        i = thumbWidth;
        canvas.drawRoundRect(rectF, (float) (i / 2), (float) (i / 2), paint);
        paint.setColor(this.circleColor);
        canvas.drawCircle((float) (this.thumbX + (thumbWidth / 2)), (float) (this.height / 2), (float) AndroidUtilities.dp(this.pressed ? 8.0f : 6.0f), paint);
    }
}
