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

    public boolean onTouch(int action, float x, float y) {
        if (action == 0) {
            int additionWidth = (this.height - thumbWidth) / 2;
            if (((float) (this.thumbX - additionWidth)) <= x && x <= ((float) ((this.thumbX + thumbWidth) + additionWidth)) && y >= 0.0f && y <= ((float) this.height)) {
                this.pressed = true;
                this.thumbDX = (int) (x - ((float) this.thumbX));
                return true;
            }
        } else {
            if (action != 1) {
                if (action != 3) {
                    if (action == 2 && this.pressed) {
                        this.thumbX = (int) (x - ((float) this.thumbDX));
                        if (this.thumbX < 0) {
                            this.thumbX = 0;
                        } else if (this.thumbX > this.width - thumbWidth) {
                            this.thumbX = this.width - thumbWidth;
                        }
                        return true;
                    }
                }
            }
            if (this.pressed) {
                if (action == 1 && this.delegate != null) {
                    this.delegate.onSeekBarDrag(((float) this.thumbX) / ((float) (this.width - thumbWidth)));
                }
                this.pressed = false;
                return true;
            }
        }
        return false;
    }

    public void setColors(int background, int cache, int progress, int circle, int selected) {
        this.backgroundColor = background;
        this.cacheColor = cache;
        this.circleColor = circle;
        this.progressColor = progress;
        this.backgroundSelectedColor = selected;
    }

    public void setProgress(float progress) {
        this.thumbX = (int) Math.ceil((double) (((float) (this.width - thumbWidth)) * progress));
        if (this.thumbX < 0) {
            this.thumbX = 0;
        } else if (this.thumbX > this.width - thumbWidth) {
            this.thumbX = this.width - thumbWidth;
        }
    }

    public void setBufferedProgress(float value) {
        this.bufferedProgress = value;
    }

    public float getProgress() {
        return ((float) this.thumbX) / ((float) (this.width - thumbWidth));
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

    public void setLineHeight(int value) {
        this.lineHeight = value;
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
