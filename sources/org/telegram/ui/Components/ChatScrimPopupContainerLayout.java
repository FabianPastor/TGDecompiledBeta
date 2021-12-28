package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;

public class ChatScrimPopupContainerLayout extends LinearLayout {
    public ActionBarPopupWindow.ActionBarPopupWindowLayout popupWindowLayout;
    public View reactionsLayout;

    public ChatScrimPopupContainerLayout(Context context) {
        super(context);
        setOrientation(1);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout;
        if (this.reactionsLayout == null || (actionBarPopupWindowLayout = this.popupWindowLayout) == null || actionBarPopupWindowLayout.getSwipeBack() == null || this.reactionsLayout.getLayoutParams().width == -2) {
            super.onMeasure(i, i2);
            return;
        }
        super.onMeasure(i, i2);
        ((LinearLayout.LayoutParams) this.reactionsLayout.getLayoutParams()).rightMargin = this.popupWindowLayout.getSwipeBack().getMeasuredWidth() - this.popupWindowLayout.getSwipeBack().getChildAt(0).getMeasuredWidth();
        super.onMeasure(i, i2);
    }
}
