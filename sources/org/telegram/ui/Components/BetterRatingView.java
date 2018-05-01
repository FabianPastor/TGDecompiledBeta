package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.ui.ActionBar.Theme;

public class BetterRatingView extends View {
    private Bitmap filledStar = BitmapFactory.decodeResource(getResources(), C0446R.drawable.ic_rating_star_filled).extractAlpha();
    private Bitmap hollowStar = BitmapFactory.decodeResource(getResources(), C0446R.drawable.ic_rating_star).extractAlpha();
    private OnRatingChangeListener listener;
    private int numStars = 5;
    private Paint paint = new Paint();
    private int selectedRating = null;

    public interface OnRatingChangeListener {
        void onRatingChanged(int i);
    }

    public BetterRatingView(Context context) {
        super(context);
    }

    protected void onMeasure(int i, int i2) {
        setMeasuredDimension((this.numStars * AndroidUtilities.dp(32.0f)) + ((this.numStars - 1) * AndroidUtilities.dp(16.0f)), AndroidUtilities.dp(32.0f));
    }

    protected void onDraw(Canvas canvas) {
        int i = 0;
        while (i < this.numStars) {
            this.paint.setColor(Theme.getColor(i < this.selectedRating ? Theme.key_calls_ratingStarSelected : Theme.key_calls_ratingStar));
            canvas.drawBitmap(i < this.selectedRating ? this.filledStar : this.hollowStar, (float) (AndroidUtilities.dp(48.0f) * i), 0.0f, this.paint);
            i++;
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        float dp = (float) AndroidUtilities.dp(-8.0f);
        for (int i = 0; i < this.numStars; i++) {
            if (motionEvent.getX() > dp && motionEvent.getX() < ((float) AndroidUtilities.dp(48.0f)) + dp) {
                int i2 = i + 1;
                if (this.selectedRating != i2) {
                    this.selectedRating = i2;
                    if (this.listener != null) {
                        this.listener.onRatingChanged(this.selectedRating);
                    }
                    invalidate();
                    return true;
                }
            }
            dp += (float) AndroidUtilities.dp(48.0f);
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
