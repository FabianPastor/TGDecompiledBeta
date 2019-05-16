package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import androidx.viewpager.widget.ViewPager;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class BottomPagesView extends View {
    private float animatedProgress;
    private String colorKey;
    private int currentPage;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private int pagesCount;
    private Paint paint = new Paint(1);
    private float progress;
    private RectF rect = new RectF();
    private int scrollPosition;
    private String selectedColorKey;
    private ViewPager viewPager;

    public BottomPagesView(Context context, ViewPager viewPager, int i) {
        super(context);
        this.viewPager = viewPager;
        this.pagesCount = i;
    }

    public void setPageOffset(int i, float f) {
        this.progress = f;
        this.scrollPosition = i;
        invalidate();
    }

    public void setCurrentPage(int i) {
        this.currentPage = i;
        invalidate();
    }

    public void setColor(String str, String str2) {
        this.colorKey = str;
        this.selectedColorKey = str2;
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        int i;
        AndroidUtilities.dp(5.0f);
        String str = this.colorKey;
        if (str != null) {
            this.paint.setColor((Theme.getColor(str) & 16777215) | -NUM);
        } else {
            this.paint.setColor(-4473925);
        }
        this.currentPage = this.viewPager.getCurrentItem();
        for (i = 0; i < this.pagesCount; i++) {
            if (i != this.currentPage) {
                int dp = AndroidUtilities.dp(11.0f) * i;
                this.rect.set((float) dp, 0.0f, (float) (dp + AndroidUtilities.dp(5.0f)), (float) AndroidUtilities.dp(5.0f));
                canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.5f), (float) AndroidUtilities.dp(2.5f), this.paint);
            }
        }
        str = this.selectedColorKey;
        if (str != null) {
            this.paint.setColor(Theme.getColor(str));
        } else {
            this.paint.setColor(-13851168);
        }
        i = this.currentPage * AndroidUtilities.dp(11.0f);
        if (this.progress == 0.0f) {
            this.rect.set((float) i, 0.0f, (float) (i + AndroidUtilities.dp(5.0f)), (float) AndroidUtilities.dp(5.0f));
        } else if (this.scrollPosition >= this.currentPage) {
            this.rect.set((float) i, 0.0f, ((float) (i + AndroidUtilities.dp(5.0f))) + (((float) AndroidUtilities.dp(11.0f)) * this.progress), (float) AndroidUtilities.dp(5.0f));
        } else {
            this.rect.set(((float) i) - (((float) AndroidUtilities.dp(11.0f)) * (1.0f - this.progress)), 0.0f, (float) (i + AndroidUtilities.dp(5.0f)), (float) AndroidUtilities.dp(5.0f));
        }
        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.5f), (float) AndroidUtilities.dp(2.5f), this.paint);
    }
}
