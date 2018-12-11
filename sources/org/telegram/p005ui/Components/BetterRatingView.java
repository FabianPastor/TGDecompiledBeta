package org.telegram.p005ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.CLASSNAMER;
import org.telegram.p005ui.ActionBar.Theme;

/* renamed from: org.telegram.ui.Components.BetterRatingView */
public class BetterRatingView extends View {
    private Bitmap filledStar = BitmapFactory.decodeResource(getResources(), CLASSNAMER.drawable.ic_rating_star_filled).extractAlpha();
    private Bitmap hollowStar = BitmapFactory.decodeResource(getResources(), CLASSNAMER.drawable.ic_rating_star).extractAlpha();
    private OnRatingChangeListener listener;
    private int numStars = 5;
    private Paint paint = new Paint();
    private int selectedRating = 0;

    /* renamed from: org.telegram.ui.Components.BetterRatingView$OnRatingChangeListener */
    public interface OnRatingChangeListener {
        void onRatingChanged(int i);
    }

    public BetterRatingView(Context context) {
        super(context);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((this.numStars * AndroidUtilities.m9dp(32.0f)) + ((this.numStars - 1) * AndroidUtilities.m9dp(16.0f)), AndroidUtilities.m9dp(32.0f));
    }

    protected void onDraw(Canvas canvas) {
        int i = 0;
        while (i < this.numStars) {
            this.paint.setColor(Theme.getColor(i < this.selectedRating ? Theme.key_calls_ratingStarSelected : Theme.key_calls_ratingStar));
            canvas.drawBitmap(i < this.selectedRating ? this.filledStar : this.hollowStar, (float) (AndroidUtilities.m9dp(48.0f) * i), 0.0f, this.paint);
            i++;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        float offset = (float) AndroidUtilities.m9dp(-8.0f);
        int i = 0;
        while (i < this.numStars) {
            if (event.getX() <= offset || event.getX() >= ((float) AndroidUtilities.m9dp(48.0f)) + offset || this.selectedRating == i + 1) {
                offset += (float) AndroidUtilities.m9dp(48.0f);
                i++;
            } else {
                this.selectedRating = i + 1;
                if (this.listener != null) {
                    this.listener.onRatingChanged(this.selectedRating);
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

    public void setOnRatingChangeListener(OnRatingChangeListener l) {
        this.listener = l;
    }
}
