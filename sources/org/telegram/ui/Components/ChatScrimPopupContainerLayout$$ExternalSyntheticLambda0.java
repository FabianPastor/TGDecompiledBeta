package org.telegram.ui.Components;

import org.telegram.ui.ActionBar.ActionBarPopupWindow;

public final /* synthetic */ class ChatScrimPopupContainerLayout$$ExternalSyntheticLambda0 implements ActionBarPopupWindow.onSizeChangedListener {
    public final /* synthetic */ ChatScrimPopupContainerLayout f$0;
    public final /* synthetic */ ActionBarPopupWindow.ActionBarPopupWindowLayout f$1;

    public /* synthetic */ ChatScrimPopupContainerLayout$$ExternalSyntheticLambda0(ChatScrimPopupContainerLayout chatScrimPopupContainerLayout, ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout) {
        this.f$0 = chatScrimPopupContainerLayout;
        this.f$1 = actionBarPopupWindowLayout;
    }

    public final void onSizeChanged() {
        this.f$0.lambda$setPopupWindowLayout$0(this.f$1);
    }
}
