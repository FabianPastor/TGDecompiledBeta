package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;

public class VideoSeekBarView extends View {
    private SeekBarDelegate delegate;
    private Paint paint = new Paint();
    private Paint paint2 = new Paint(1);
    private boolean pressed = false;
    private float progress = 0.0f;
    private int thumbDX = 0;
    private int thumbHeight = AndroidUtilities.dp(12.0f);
    private int thumbWidth = AndroidUtilities.dp(12.0f);

    public interface SeekBarDelegate {
        void onSeekBarDrag(float f);
    }

    public VideoSeekBarView(Context context) {
        super(context);
        this.paint.setColor(-10724260);
        this.paint2.setColor(-1);
    }

    public void setDelegate(SeekBarDelegate seekBarDelegate) {
        this.delegate = seekBarDelegate;
    }

    public boolean onTouchEvent(MotionEvent event) {
        SeekBarDelegate seekBarDelegate;
        if (event == null) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        float thumbX = (float) ((int) (((float) (getMeasuredWidth() - this.thumbWidth)) * this.progress));
        if (event.getAction() == 0) {
            int measuredHeight = getMeasuredHeight();
            int i = this.thumbWidth;
            int additionWidth = (measuredHeight - i) / 2;
            if (thumbX - ((float) additionWidth) <= x && x <= ((float) i) + thumbX + ((float) additionWidth) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                this.pressed = true;
                this.thumbDX = (int) (x - thumbX);
                getParent().requestDisallowInterceptTouchEvent(true);
                invalidate();
                return true;
            }
        } else if (event.getAction() == 1 || event.getAction() == 3) {
            if (this.pressed) {
                if (event.getAction() == 1 && (seekBarDelegate = this.delegate) != null) {
                    seekBarDelegate.onSeekBarDrag(thumbX / ((float) (getMeasuredWidth() - this.thumbWidth)));
                }
                this.pressed = false;
                invalidate();
                return true;
            }
        } else if (event.getAction() == 2 && this.pressed) {
            float thumbX2 = (float) ((int) (x - ((float) this.thumbDX)));
            if (thumbX2 < 0.0f) {
                thumbX2 = 0.0f;
            } else if (thumbX2 > ((float) (getMeasuredWidth() - this.thumbWidth))) {
                thumbX2 = (float) (getMeasuredWidth() - this.thumbWidth);
            }
            this.progress = thumbX2 / ((float) (getMeasuredWidth() - this.thumbWidth));
            invalidate();
            return true;
        }
        return false;
    }

    public void setProgress(float progress2) {
        if (progress2 < 0.0f) {
            progress2 = 0.0f;
        } else if (progress2 > 1.0f) {
            progress2 = 1.0f;
        }
        this.progress = progress2;
        invalidate();
    }

    public float getProgress() {
        return this.progress;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int measuredWidth = getMeasuredWidth();
        int i = this.thumbWidth;
        canvas.drawRect((float) (i / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f)), (float) (getMeasuredWidth() - (this.thumbWidth / 2)), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f)), this.paint);
        int i2 = this.thumbWidth;
        canvas.drawCircle((float) ((i2 / 2) + ((int) (((float) (measuredWidth - i)) * this.progress))), (float) ((this.thumbHeight / 2) + ((getMeasuredHeight() - this.thumbHeight) / 2)), (float) (i2 / 2), this.paint2);
    }
}
