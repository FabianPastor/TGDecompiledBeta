package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.ui.ActionBar.Theme;

public class GroupCreateDividerItemDecoration extends ItemDecoration {
    private Paint paint = new Paint();
    private boolean searching;

    public GroupCreateDividerItemDecoration() {
        this.paint.setColor(Theme.GROUP_CREATE_DIVIDER_COLOR);
    }

    public void setSearching(boolean value) {
        this.searching = value;
    }

    public void onDraw(Canvas canvas, RecyclerView parent, State state) {
        int width = parent.getWidth();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            View child = parent.getChildAt(i);
            if (parent.getChildAdapterPosition(child) != 0) {
                int top = child.getBottom();
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(72.0f), (float) top, (float) (width - (LocaleController.isRTL ? AndroidUtilities.dp(72.0f) : 0)), (float) top, this.paint);
            }
        }
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
            return;
        }
        if (this.searching || position != 1) {
            outRect.top = 1;
        }
    }
}
