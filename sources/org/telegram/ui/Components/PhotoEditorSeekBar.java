package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;

public class PhotoEditorSeekBar extends View {
    private PhotoEditorSeekBarDelegate delegate;
    private Paint innerPaint = new Paint();
    private int maxValue;
    private int minValue;
    private Paint outerPaint = new Paint(1);
    private boolean pressed = false;
    private float progress = 0.0f;
    private int thumbDX = 0;
    private int thumbSize = AndroidUtilities.dp(16.0f);

    public interface PhotoEditorSeekBarDelegate {
        void onProgressChanged(int i, int i2);
    }

    public PhotoEditorSeekBar(Context context) {
        super(context);
        this.innerPaint.setColor(-11711155);
        this.outerPaint.setColor(-1);
    }

    public void setDelegate(PhotoEditorSeekBarDelegate photoEditorSeekBarDelegate) {
        this.delegate = photoEditorSeekBarDelegate;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent == null) {
            return false;
        }
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        float measuredWidth = (float) ((int) (((float) (getMeasuredWidth() - this.thumbSize)) * this.progress));
        float f = 0.0f;
        if (motionEvent.getAction() == 0) {
            motionEvent = (float) ((getMeasuredHeight() - this.thumbSize) / 2);
            if (measuredWidth - motionEvent <= x && x <= (((float) this.thumbSize) + measuredWidth) + motionEvent && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
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
                        f = motionEvent > ((float) (getMeasuredWidth() - this.thumbSize)) ? (float) (getMeasuredWidth() - this.thumbSize) : motionEvent;
                    }
                    this.progress = f / ((float) (getMeasuredWidth() - this.thumbSize));
                    if (this.delegate != null) {
                        this.delegate.onProgressChanged(((Integer) getTag()).intValue(), getProgress());
                    }
                    invalidate();
                    return true;
                }
            }
        }
        if (this.pressed != null) {
            this.pressed = false;
            invalidate();
            return true;
        }
        return false;
    }

    public void setProgress(int i) {
        setProgress(i, true);
    }

    public void setProgress(int i, boolean z) {
        if (i < this.minValue) {
            i = this.minValue;
        } else if (i > this.maxValue) {
            i = this.maxValue;
        }
        this.progress = ((float) (i - this.minValue)) / ((float) (this.maxValue - this.minValue));
        invalidate();
        if (z && this.delegate != 0) {
            this.delegate.onProgressChanged(((Integer) getTag()).intValue(), getProgress());
        }
    }

    public int getProgress() {
        return (int) (((float) this.minValue) + (this.progress * ((float) (this.maxValue - this.minValue))));
    }

    public void setMinMax(int i, int i2) {
        this.minValue = i;
        this.maxValue = i2;
    }

    protected void onDraw(Canvas canvas) {
        int measuredHeight = (getMeasuredHeight() - this.thumbSize) / 2;
        int measuredWidth = (int) (((float) (getMeasuredWidth() - this.thumbSize)) * this.progress);
        canvas.drawRect((float) (this.thumbSize / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f)), (float) (getMeasuredWidth() - (this.thumbSize / 2)), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f)), this.innerPaint);
        if (this.minValue == 0) {
            canvas.drawRect((float) (r0.thumbSize / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f)), (float) measuredWidth, (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f)), r0.outerPaint);
        } else if (r0.progress > 0.5f) {
            canvas.drawRect((float) ((getMeasuredWidth() / 2) - AndroidUtilities.dp(1.0f)), (float) ((getMeasuredHeight() - r0.thumbSize) / 2), (float) (getMeasuredWidth() / 2), (float) ((getMeasuredHeight() + r0.thumbSize) / 2), r0.outerPaint);
            canvas.drawRect((float) (getMeasuredWidth() / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f)), (float) measuredWidth, (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f)), r0.outerPaint);
        } else {
            canvas.drawRect((float) (getMeasuredWidth() / 2), (float) ((getMeasuredHeight() - r0.thumbSize) / 2), (float) ((getMeasuredWidth() / 2) + AndroidUtilities.dp(1.0f)), (float) ((getMeasuredHeight() + r0.thumbSize) / 2), r0.outerPaint);
            canvas.drawRect((float) measuredWidth, (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f)), (float) (getMeasuredWidth() / 2), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f)), r0.outerPaint);
        }
        canvas.drawCircle((float) (measuredWidth + (r0.thumbSize / 2)), (float) (measuredHeight + (r0.thumbSize / 2)), (float) (r0.thumbSize / 2), r0.outerPaint);
    }
}
