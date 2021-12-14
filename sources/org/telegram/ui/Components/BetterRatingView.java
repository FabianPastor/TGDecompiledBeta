package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class BetterRatingView extends View {
    private Bitmap filledStar = BitmapFactory.decodeResource(getResources(), NUM).extractAlpha();
    private Bitmap hollowStar = BitmapFactory.decodeResource(getResources(), NUM).extractAlpha();
    private OnRatingChangeListener listener;
    private int numStars = 5;
    private Paint paint = new Paint();
    private int selectedRating = 0;

    public interface OnRatingChangeListener {
        void onRatingChanged(int i);
    }

    public BetterRatingView(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension((this.numStars * AndroidUtilities.dp(32.0f)) + ((this.numStars - 1) * AndroidUtilities.dp(16.0f)), AndroidUtilities.dp(32.0f));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i = 0;
        while (i < this.numStars) {
            this.paint.setColor(Theme.getColor(i < this.selectedRating ? "dialogTextBlue" : "dialogTextHint"));
            canvas.drawBitmap(i < this.selectedRating ? this.filledStar : this.hollowStar, (float) (AndroidUtilities.dp(48.0f) * i), 0.0f, this.paint);
            i++;
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int i;
        float dp = (float) AndroidUtilities.dp(-8.0f);
        int i2 = 0;
        while (i2 < this.numStars) {
            if (motionEvent.getX() <= dp || motionEvent.getX() >= ((float) AndroidUtilities.dp(48.0f)) + dp || this.selectedRating == (i = i2 + 1)) {
                dp += (float) AndroidUtilities.dp(48.0f);
                i2++;
            } else {
                this.selectedRating = i;
                OnRatingChangeListener onRatingChangeListener = this.listener;
                if (onRatingChangeListener != null) {
                    onRatingChangeListener.onRatingChanged(i);
                }
                invalidate();
                return true;
            }
        }
        return true;
    }

    public int getRating() {
        return this.selectedRating;
    }

    public void setOnRatingChangeListener(OnRatingChangeListener onRatingChangeListener) {
        this.listener = onRatingChangeListener;
    }
}
