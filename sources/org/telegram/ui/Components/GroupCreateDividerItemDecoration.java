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
import org.telegram.ui.Cells.GroupCreateSectionCell;

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
        int i = 0;
        while (i < childCount) {
            View childAt = recyclerView.getChildAt(i);
            View childAt2 = i < childCount + -1 ? recyclerView.getChildAt(i + 1) : null;
            if (!(recyclerView.getChildAdapterPosition(childAt) < this.skipRows || (childAt instanceof GroupCreateSectionCell) || (childAt2 instanceof GroupCreateSectionCell))) {
                float bottom = (float) childAt.getBottom();
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(72.0f), bottom, (float) (width - (LocaleController.isRTL ? AndroidUtilities.dp(72.0f) : 0)), bottom, Theme.dividerPaint);
            }
            i++;
        }
    }

    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
        super.getItemOffsets(rect, view, recyclerView, state);
        rect.top = 1;
    }
}
