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
        void onSeekBarContinuousDrag(float f);

        void onSeekBarDrag(float f);

        /* renamed from: org.telegram.ui.Components.SeekBar$SeekBarDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onSeekBarContinuousDrag(SeekBarDelegate _this, float progress) {
            }
        }
    }

    public SeekBar(View parent) {
        if (paint == null) {
            paint = new Paint(1);
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
                            this.thumbX = i3 - i2;
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

    public void setColors(int background, int cache, int progress, int circle, int selected2) {
        this.backgroundColor = background;
        this.cacheColor = cache;
        this.circleColor = circle;
        this.progressColor = progress;
        this.backgroundSelectedColor = selected2;
    }

    public void setProgress(float progress) {
        int ceil = (int) Math.ceil((double) (((float) (this.width - thumbWidth)) * progress));
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

    public void setBufferedProgress(float value) {
        this.bufferedProgress = value;
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

    public void setLineHeight(int value) {
        this.lineHeight = value;
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
        float f = (float) (i9 / 2);
        int i10 = this.height;
        int i11 = this.lineHeight;
        rectF5.set(f, (float) ((i10 / 2) - (i11 / 2)), (float) ((i9 / 2) + (this.pressed ? this.draggingThumbX : this.thumbX)), (float) ((i10 / 2) + (i11 / 2)));
        paint.setColor(this.progressColor);
        RectF rectF6 = this.rect;
        int i12 = thumbWidth;
        canvas.drawRoundRect(rectF6, (float) (i12 / 2), (float) (i12 / 2), paint);
        paint.setColor(this.circleColor);
        int newRad = AndroidUtilities.dp(this.pressed ? 8.0f : 6.0f);
        if (this.currentRadius != ((float) newRad)) {
            long dt = SystemClock.elapsedRealtime() - this.lastUpdateTime;
            if (dt > 18) {
                dt = 16;
            }
            float f2 = this.currentRadius;
            if (f2 < ((float) newRad)) {
                float dp = f2 + (((float) AndroidUtilities.dp(1.0f)) * (((float) dt) / 60.0f));
                this.currentRadius = dp;
                if (dp > ((float) newRad)) {
                    this.currentRadius = (float) newRad;
                }
            } else {
                float dp2 = f2 - (((float) AndroidUtilities.dp(1.0f)) * (((float) dt) / 60.0f));
                this.currentRadius = dp2;
                if (dp2 < ((float) newRad)) {
                    this.currentRadius = (float) newRad;
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
