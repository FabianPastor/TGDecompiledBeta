package org.telegram.ui.Components;

import android.content.Context;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;

public class ChatScrimPopupContainerLayout extends LinearLayout {
    public ActionBarPopupWindow.ActionBarPopupWindowLayout popupWindowLayout;
    public ReactionsContainerLayout reactionsLayout;

    public ChatScrimPopupContainerLayout(Context context) {
        super(context);
        setOrientation(1);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.reactionsLayout == null || this.popupWindowLayout == null) {
            super.onMeasure(i, i2);
            return;
        }
        super.onMeasure(i, i2);
        int totalWidth = this.reactionsLayout.getTotalWidth();
        int i3 = 0;
        int measuredWidth = (this.popupWindowLayout.getSwipeBack() != null ? this.popupWindowLayout.getSwipeBack() : this.popupWindowLayout).getChildAt(0).getMeasuredWidth() + AndroidUtilities.dp(16.0f) + AndroidUtilities.dp(16.0f) + AndroidUtilities.dp(36.0f);
        if (totalWidth > measuredWidth) {
            int dp = ((measuredWidth - AndroidUtilities.dp(16.0f)) / AndroidUtilities.dp(36.0f)) + 1;
            int dp2 = ((AndroidUtilities.dp(36.0f) * dp) + AndroidUtilities.dp(16.0f)) - AndroidUtilities.dp(8.0f);
            if (dp2 <= totalWidth && dp != this.reactionsLayout.getItemsCount()) {
                totalWidth = dp2;
            }
            this.reactionsLayout.getLayoutParams().width = totalWidth;
        } else {
            this.reactionsLayout.getLayoutParams().width = -2;
        }
        if (this.popupWindowLayout.getSwipeBack() != null) {
            i3 = this.popupWindowLayout.getSwipeBack().getMeasuredWidth() - this.popupWindowLayout.getSwipeBack().getChildAt(0).getMeasuredWidth();
        }
        ((LinearLayout.LayoutParams) this.reactionsLayout.getLayoutParams()).rightMargin = i3;
        super.onMeasure(i, i2);
    }
}
