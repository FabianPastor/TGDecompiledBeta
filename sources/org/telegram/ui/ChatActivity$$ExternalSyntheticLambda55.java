package org.telegram.ui;

import android.view.View;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda55 implements View.OnClickListener {
    public final /* synthetic */ ActionBarPopupWindow.ActionBarPopupWindowLayout f$0;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda55(ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout) {
        this.f$0 = actionBarPopupWindowLayout;
    }

    public final void onClick(View view) {
        this.f$0.getSwipeBack().closeForeground();
    }
}
