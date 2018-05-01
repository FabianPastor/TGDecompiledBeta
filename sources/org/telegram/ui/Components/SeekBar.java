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
            i = (this.height - thumbWidth) / 2;
            if (((float) (this.thumbX - i)) <= f && f <= ((float) ((this.thumbX + thumbWidth) + i)) && f2 >= 0 && f2 <= ((float) this.height)) {
                this.pressed = true;
                this.thumbDX = (int) (f - ((float) this.thumbX));
                return true;
            }
        }
        if (i != 1) {
            if (i != 3) {
                if (i == 2 && this.pressed != 0) {
                    this.thumbX = (int) (f - ((float) this.thumbDX));
                    if (this.thumbX < 0) {
                        this.thumbX = 0;
                    } else if (this.thumbX > this.width - thumbWidth) {
                        this.thumbX = this.width - thumbWidth;
                    }
                    return true;
                }
            }
        }
        if (this.pressed != null) {
            if (i == 1 && this.delegate != 0) {
                this.delegate.onSeekBarDrag(((float) this.thumbX) / ((float) (this.width - thumbWidth)));
            }
            this.pressed = false;
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
        if (this.thumbX < null) {
            this.thumbX = 0;
        } else if (this.thumbX > this.width - thumbWidth) {
            this.thumbX = this.width - thumbWidth;
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
        this.rect.set((float) (thumbWidth / 2), (float) ((this.height / 2) - (this.lineHeight / 2)), (float) (this.width - (thumbWidth / 2)), (float) ((this.height / 2) + (this.lineHeight / 2)));
        paint.setColor(this.selected ? this.backgroundSelectedColor : this.backgroundColor);
        canvas.drawRoundRect(this.rect, (float) (thumbWidth / 2), (float) (thumbWidth / 2), paint);
        if (this.bufferedProgress > 0.0f) {
            paint.setColor(this.selected ? this.backgroundSelectedColor : this.cacheColor);
            this.rect.set((float) (thumbWidth / 2), (float) ((this.height / 2) - (this.lineHeight / 2)), ((float) (thumbWidth / 2)) + (this.bufferedProgress * ((float) (this.width - thumbWidth))), (float) ((this.height / 2) + (this.lineHeight / 2)));
            canvas.drawRoundRect(this.rect, (float) (thumbWidth / 2), (float) (thumbWidth / 2), paint);
        }
        this.rect.set((float) (thumbWidth / 2), (float) ((this.height / 2) - (this.lineHeight / 2)), (float) ((thumbWidth / 2) + this.thumbX), (float) ((this.height / 2) + (this.lineHeight / 2)));
        paint.setColor(this.progressColor);
        canvas.drawRoundRect(this.rect, (float) (thumbWidth / 2), (float) (thumbWidth / 2), paint);
        paint.setColor(this.circleColor);
        canvas.drawCircle((float) (this.thumbX + (thumbWidth / 2)), (float) (this.height / 2), (float) AndroidUtilities.dp(this.pressed ? 8.0f : 6.0f), paint);
    }
}
