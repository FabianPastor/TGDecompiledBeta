package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GroupCreateSectionCell;

public class GroupCreateDividerItemDecoration extends RecyclerView.ItemDecoration {
    private boolean searching;
    private boolean single;
    private int skipRows;

    public void setSearching(boolean value) {
        this.searching = value;
    }

    public void setSingle(boolean value) {
        this.single = value;
    }

    public void setSkipRows(int value) {
        this.skipRows = value;
    }

    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        RecyclerView recyclerView = parent;
        int width = parent.getWidth();
        int childCount = parent.getChildCount() - (this.single ^ true ? 1 : 0);
        int i = 0;
        while (i < childCount) {
            View child = recyclerView.getChildAt(i);
            View nextChild = i < childCount + -1 ? recyclerView.getChildAt(i + 1) : null;
            if (recyclerView.getChildAdapterPosition(child) >= this.skipRows && !(child instanceof GroupCreateSectionCell) && !(nextChild instanceof GroupCreateSectionCell)) {
                int top = child.getBottom();
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(72.0f), (float) top, (float) (width - (LocaleController.isRTL ? AndroidUtilities.dp(72.0f) : 0)), (float) top, Theme.dividerPaint);
            }
            i++;
        }
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = 1;
    }
}
