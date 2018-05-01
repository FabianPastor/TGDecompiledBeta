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

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent == null) {
            return false;
        }
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        float measuredWidth = (float) ((int) (((float) (getMeasuredWidth() - this.thumbWidth)) * this.progress));
        float f = 0.0f;
        if (motionEvent.getAction() == 0) {
            motionEvent = (float) ((getMeasuredHeight() - this.thumbWidth) / 2);
            if (measuredWidth - motionEvent <= x && x <= (((float) this.thumbWidth) + measuredWidth) + motionEvent && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                this.pressed = true;
                this.thumbDX = (int) (x - measuredWidth);
                getParent().requestDisallowInterceptTouchEvent(true);
                invalidate();
                return true;
            }
        }
        if (motionEvent.getAction() != 1) {
            if (motionEvent.getAction() != 3) {
                if (motionEvent.getAction() == 2 && this.pressed != null) {
                    motionEvent = (float) ((int) (x - ((float) this.thumbDX)));
                    if (motionEvent >= null) {
                        f = motionEvent > ((float) (getMeasuredWidth() - this.thumbWidth)) ? (float) (getMeasuredWidth() - this.thumbWidth) : motionEvent;
                    }
                    this.progress = f / ((float) (getMeasuredWidth() - this.thumbWidth));
                    invalidate();
                    return true;
                }
            }
        }
        if (this.pressed) {
            if (motionEvent.getAction() == 1 && this.delegate != null) {
                this.delegate.onSeekBarDrag(measuredWidth / ((float) (getMeasuredWidth() - this.thumbWidth)));
            }
            this.pressed = false;
            invalidate();
            return true;
        }
        return false;
    }

    public void setProgress(float f) {
        if (f < 0.0f) {
            f = 0.0f;
        } else if (f > 1.0f) {
            f = 1.0f;
        }
        this.progress = f;
        invalidate();
    }

    public float getProgress() {
        return this.progress;
    }

    protected void onDraw(Canvas canvas) {
        int measuredHeight = (getMeasuredHeight() - this.thumbHeight) / 2;
        int measuredWidth = (int) (((float) (getMeasuredWidth() - this.thumbWidth)) * this.progress);
        canvas.drawRect((float) (this.thumbWidth / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f)), (float) (getMeasuredWidth() - (this.thumbWidth / 2)), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f)), this.paint);
        canvas.drawCircle((float) (measuredWidth + (this.thumbWidth / 2)), (float) (measuredHeight + (this.thumbHeight / 2)), (float) (this.thumbWidth / 2), this.paint2);
    }
}
