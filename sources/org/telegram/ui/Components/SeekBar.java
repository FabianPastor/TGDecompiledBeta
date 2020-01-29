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
    private int draggingThumbX = 0;
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

        /* renamed from: org.telegram.ui.Components.SeekBar$SeekBarDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onSeekBarContinuousDrag(SeekBarDelegate seekBarDelegate, float f) {
            }
        }

        void onSeekBarContinuousDrag(float f);

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
        SeekBarDelegate seekBarDelegate;
        if (i == 0) {
            int i2 = this.height;
            int i3 = thumbWidth;
            int i4 = (i2 - i3) / 2;
            int i5 = this.thumbX;
            if (((float) (i5 - i4)) <= f && f <= ((float) (i3 + i5 + i4)) && f2 >= 0.0f && f2 <= ((float) i2)) {
                this.pressed = true;
                this.draggingThumbX = i5;
                this.thumbDX = (int) (f - ((float) i5));
                return true;
            }
        } else if (i == 1 || i == 3) {
            if (this.pressed) {
                this.thumbX = this.draggingThumbX;
                if (i == 1 && (seekBarDelegate = this.delegate) != null) {
                    seekBarDelegate.onSeekBarDrag(((float) this.thumbX) / ((float) (this.width - thumbWidth)));
                }
                this.pressed = false;
                return true;
            }
        } else if (i == 2 && this.pressed) {
            this.draggingThumbX = (int) (f - ((float) this.thumbDX));
            int i6 = this.draggingThumbX;
            if (i6 < 0) {
                this.draggingThumbX = 0;
            } else {
                int i7 = this.width;
                int i8 = thumbWidth;
                if (i6 > i7 - i8) {
                    this.draggingThumbX = i7 - i8;
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
        int i2 = this.height;
        int i3 = this.lineHeight;
        rectF.set((float) (i / 2), (float) ((i2 / 2) - (i3 / 2)), (float) (this.width - (i / 2)), (float) ((i2 / 2) + (i3 / 2)));
        paint.setColor(this.selected ? this.backgroundSelectedColor : this.backgroundColor);
        RectF rectF2 = this.rect;
        int i4 = thumbWidth;
        canvas.drawRoundRect(rectF2, (float) (i4 / 2), (float) (i4 / 2), paint);
        if (this.bufferedProgress > 0.0f) {
            paint.setColor(this.selected ? this.backgroundSelectedColor : this.cacheColor);
            RectF rectF3 = this.rect;
            int i5 = thumbWidth;
            int i6 = this.height;
            int i7 = this.lineHeight;
            rectF3.set((float) (i5 / 2), (float) ((i6 / 2) - (i7 / 2)), ((float) (i5 / 2)) + (this.bufferedProgress * ((float) (this.width - i5))), (float) ((i6 / 2) + (i7 / 2)));
            RectF rectF4 = this.rect;
            int i8 = thumbWidth;
            canvas.drawRoundRect(rectF4, (float) (i8 / 2), (float) (i8 / 2), paint);
        }
        RectF rectF5 = this.rect;
        int i9 = thumbWidth;
        int i10 = this.height;
        int i11 = this.lineHeight;
        rectF5.set((float) (i9 / 2), (float) ((i10 / 2) - (i11 / 2)), (float) ((i9 / 2) + this.thumbX), (float) ((i10 / 2) + (i11 / 2)));
        paint.setColor(this.progressColor);
        RectF rectF6 = this.rect;
        int i12 = thumbWidth;
        canvas.drawRoundRect(rectF6, (float) (i12 / 2), (float) (i12 / 2), paint);
        paint.setColor(this.circleColor);
        canvas.drawCircle((float) ((this.pressed ? this.draggingThumbX : this.thumbX) + (thumbWidth / 2)), (float) (this.height / 2), (float) AndroidUtilities.dp(this.pressed ? 8.0f : 6.0f), paint);
    }
}
