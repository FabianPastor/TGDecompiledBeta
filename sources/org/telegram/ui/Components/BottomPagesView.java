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

    public BottomPagesView(Context context, ViewPager pager, int count) {
        super(context);
        this.viewPager = pager;
        this.pagesCount = count;
    }

    public void setPageOffset(int position, float offset) {
        this.progress = offset;
        this.scrollPosition = position;
        invalidate();
    }

    public void setCurrentPage(int page) {
        this.currentPage = page;
        invalidate();
    }

    public void setColor(String key, String selectedKey) {
        this.colorKey = key;
        this.selectedColorKey = selectedKey;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float dp = (float) AndroidUtilities.dp(5.0f);
        String str = this.colorKey;
        if (str != null) {
            this.paint.setColor((Theme.getColor(str) & 16777215) | -NUM);
        } else {
            this.paint.setColor(-4473925);
        }
        this.currentPage = this.viewPager.getCurrentItem();
        for (int a = 0; a < this.pagesCount; a++) {
            if (a != this.currentPage) {
                int x = AndroidUtilities.dp(11.0f) * a;
                this.rect.set((float) x, 0.0f, (float) (AndroidUtilities.dp(5.0f) + x), (float) AndroidUtilities.dp(5.0f));
                canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.5f), (float) AndroidUtilities.dp(2.5f), this.paint);
            }
        }
        String str2 = this.selectedColorKey;
        if (str2 != null) {
            this.paint.setColor(Theme.getColor(str2));
        } else {
            this.paint.setColor(-13851168);
        }
        int x2 = this.currentPage * AndroidUtilities.dp(11.0f);
        if (this.progress == 0.0f) {
            this.rect.set((float) x2, 0.0f, (float) (AndroidUtilities.dp(5.0f) + x2), (float) AndroidUtilities.dp(5.0f));
        } else if (this.scrollPosition >= this.currentPage) {
            this.rect.set((float) x2, 0.0f, ((float) (AndroidUtilities.dp(5.0f) + x2)) + (((float) AndroidUtilities.dp(11.0f)) * this.progress), (float) AndroidUtilities.dp(5.0f));
        } else {
            this.rect.set(((float) x2) - (((float) AndroidUtilities.dp(11.0f)) * (1.0f - this.progress)), 0.0f, (float) (AndroidUtilities.dp(5.0f) + x2), (float) AndroidUtilities.dp(5.0f));
        }
        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.5f), (float) AndroidUtilities.dp(2.5f), this.paint);
    }
}
