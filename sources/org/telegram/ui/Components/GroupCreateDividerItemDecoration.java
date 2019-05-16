package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.RecyclerView.State;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class GroupCreateDividerItemDecoration extends ItemDecoration {
    private boolean searching;
    private boolean single;
    private int skipRows;

    public void setSearching(boolean z) {
        this.searching = z;
    }

    public void setSingle(boolean z) {
        this.single = z;
    }

    public void setSkipRows(int i) {
        this.skipRows = i;
    }

    public void onDraw(Canvas canvas, RecyclerView recyclerView, State state) {
        int width = recyclerView.getWidth();
        int childCount = recyclerView.getChildCount() - (this.single ^ 1);
        for (int i = 0; i < childCount; i++) {
            View childAt = recyclerView.getChildAt(i);
            if (recyclerView.getChildAdapterPosition(childAt) >= this.skipRows) {
                float bottom = (float) childAt.getBottom();
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(72.0f), bottom, (float) (width - (LocaleController.isRTL ? AndroidUtilities.dp(72.0f) : 0)), bottom, Theme.dividerPaint);
            }
        }
    }

    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
        super.getItemOffsets(rect, view, recyclerView, state);
        rect.top = 1;
    }
}
