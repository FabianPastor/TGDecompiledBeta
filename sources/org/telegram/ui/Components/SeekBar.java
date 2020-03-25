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

        /* renamed from: org.telegram.ui.Components.SeekBar$SeekBarDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
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
        SeekBarDelegate seekBarDelegate;
        if (i == 0) {
            int i2 = this.height;
            int i3 = thumbWidth;
            int i4 = (i2 - i3) / 2;
            if (f >= ((float) (-i4)) && f <= ((float) (this.width + i4)) && f2 >= 0.0f && f2 <= ((float) i2)) {
                int i5 = this.thumbX;
                if (((float) (i5 - i4)) > f || f > ((float) (i5 + i3 + i4))) {
                    int i6 = thumbWidth;
                    int i7 = ((int) f) - (i6 / 2);
                    this.thumbX = i7;
                    if (i7 < 0) {
                        this.thumbX = 0;
                    } else {
                        int i8 = this.width;
                        if (i7 > i8 - i6) {
                            this.thumbX = i6 - i8;
                        }
                    }
                }
                this.pressed = true;
                int i9 = this.thumbX;
                this.draggingThumbX = i9;
                this.thumbDX = (int) (f - ((float) i9));
                return true;
            }
        } else if (i == 1 || i == 3) {
            if (this.pressed) {
                int i10 = this.draggingThumbX;
                this.thumbX = i10;
                if (i == 1 && (seekBarDelegate = this.delegate) != null) {
                    seekBarDelegate.onSeekBarDrag(((float) i10) / ((float) (this.width - thumbWidth)));
                }
                this.pressed = false;
                return true;
            }
        } else if (i == 2 && this.pressed) {
            int i11 = (int) (f - ((float) this.thumbDX));
            this.draggingThumbX = i11;
            if (i11 < 0) {
                this.draggingThumbX = 0;
            } else {
                int i12 = this.width;
                int i13 = thumbWidth;
                if (i11 > i12 - i13) {
                    this.draggingThumbX = i12 - i13;
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
        int ceil = (int) Math.ceil((double) (((float) (this.width - thumbWidth)) * f));
        this.thumbX = ceil;
        if (ceil < 0) {
            this.thumbX = 0;
            return;
        }
        int i = this.width;
        int i2 = thumbWidth;
        if (ceil > i - i2) {
            this.thumbX = i - i2;
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
        rectF5.set((float) (i9 / 2), (float) ((this.height / 2) - (this.lineHeight / 2)), (float) ((i9 / 2) + (this.pressed ? this.draggingThumbX : this.thumbX)), (float) ((this.height / 2) + (this.lineHeight / 2)));
        paint.setColor(this.progressColor);
        RectF rectF6 = this.rect;
        int i10 = thumbWidth;
        canvas.drawRoundRect(rectF6, (float) (i10 / 2), (float) (i10 / 2), paint);
        paint.setColor(this.circleColor);
        float dp = (float) AndroidUtilities.dp(this.pressed ? 8.0f : 6.0f);
        if (this.currentRadius != dp) {
            long elapsedRealtime = SystemClock.elapsedRealtime() - this.lastUpdateTime;
            if (elapsedRealtime > 18) {
                elapsedRealtime = 16;
            }
            float f = this.currentRadius;
            if (f < dp) {
                float dp2 = f + (((float) AndroidUtilities.dp(1.0f)) * (((float) elapsedRealtime) / 60.0f));
                this.currentRadius = dp2;
                if (dp2 > dp) {
                    this.currentRadius = dp;
                }
            } else {
                float dp3 = f - (((float) AndroidUtilities.dp(1.0f)) * (((float) elapsedRealtime) / 60.0f));
                this.currentRadius = dp3;
                if (dp3 < dp) {
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
