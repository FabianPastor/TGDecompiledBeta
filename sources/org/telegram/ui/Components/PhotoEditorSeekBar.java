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

    public void setDelegate(PhotoEditorSeekBarDelegate delegate2) {
        this.delegate = delegate2;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        float thumbX = (float) ((int) (((float) (getMeasuredWidth() - this.thumbSize)) * this.progress));
        if (event.getAction() == 0) {
            int measuredHeight = getMeasuredHeight();
            int i = this.thumbSize;
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
                this.pressed = false;
                invalidate();
                return true;
            }
        } else if (event.getAction() == 2 && this.pressed) {
            float thumbX2 = (float) ((int) (x - ((float) this.thumbDX)));
            if (thumbX2 < 0.0f) {
                thumbX2 = 0.0f;
            } else if (thumbX2 > ((float) (getMeasuredWidth() - this.thumbSize))) {
                thumbX2 = (float) (getMeasuredWidth() - this.thumbSize);
            }
            this.progress = thumbX2 / ((float) (getMeasuredWidth() - this.thumbSize));
            PhotoEditorSeekBarDelegate photoEditorSeekBarDelegate = this.delegate;
            if (photoEditorSeekBarDelegate != null) {
                photoEditorSeekBarDelegate.onProgressChanged(((Integer) getTag()).intValue(), getProgress());
            }
            invalidate();
            return true;
        }
        return false;
    }

    public void setProgress(int progress2) {
        setProgress(progress2, true);
    }

    public void setProgress(int progress2, boolean notify) {
        PhotoEditorSeekBarDelegate photoEditorSeekBarDelegate;
        int i = this.minValue;
        if (progress2 < i) {
            progress2 = this.minValue;
        } else if (progress2 > this.maxValue) {
            progress2 = this.maxValue;
        }
        this.progress = ((float) (progress2 - i)) / ((float) (this.maxValue - i));
        invalidate();
        if (notify && (photoEditorSeekBarDelegate = this.delegate) != null) {
            photoEditorSeekBarDelegate.onProgressChanged(((Integer) getTag()).intValue(), getProgress());
        }
    }

    public int getProgress() {
        int i = this.minValue;
        return (int) (((float) i) + (this.progress * ((float) (this.maxValue - i))));
    }

    public void setMinMax(int min, int max) {
        this.minValue = min;
        this.maxValue = max;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int y = (getMeasuredHeight() - this.thumbSize) / 2;
        int measuredWidth = getMeasuredWidth();
        int i = this.thumbSize;
        int thumbX = (int) (((float) (measuredWidth - i)) * this.progress);
        canvas.drawRect((float) (i / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f)), (float) (getMeasuredWidth() - (this.thumbSize / 2)), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f)), this.innerPaint);
        if (this.minValue == 0) {
            canvas.drawRect((float) (this.thumbSize / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f)), (float) thumbX, (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f)), this.outerPaint);
        } else if (this.progress > 0.5f) {
            canvas.drawRect((float) ((getMeasuredWidth() / 2) - AndroidUtilities.dp(1.0f)), (float) ((getMeasuredHeight() - this.thumbSize) / 2), (float) (getMeasuredWidth() / 2), (float) ((getMeasuredHeight() + this.thumbSize) / 2), this.outerPaint);
            canvas.drawRect((float) (getMeasuredWidth() / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f)), (float) thumbX, (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f)), this.outerPaint);
        } else {
            canvas.drawRect((float) (getMeasuredWidth() / 2), (float) ((getMeasuredHeight() - this.thumbSize) / 2), (float) ((getMeasuredWidth() / 2) + AndroidUtilities.dp(1.0f)), (float) ((getMeasuredHeight() + this.thumbSize) / 2), this.outerPaint);
            canvas.drawRect((float) thumbX, (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f)), (float) (getMeasuredWidth() / 2), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f)), this.outerPaint);
        }
        int i2 = this.thumbSize;
        canvas.drawCircle((float) ((i2 / 2) + thumbX), (float) ((i2 / 2) + y), (float) (i2 / 2), this.outerPaint);
    }
}
