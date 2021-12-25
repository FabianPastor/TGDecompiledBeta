package org.telegram.ui;

import android.view.View;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda38 implements View.OnClickListener {
    public final /* synthetic */ ActionBarPopupWindow.ActionBarPopupWindowLayout f$0;
    public final /* synthetic */ int[] f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda38(ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout, int[] iArr) {
        this.f$0 = actionBarPopupWindowLayout;
        this.f$1 = iArr;
    }

    public final void onClick(View view) {
        this.f$0.getSwipeBack().openForeground(this.f$1[0]);
    }
}
