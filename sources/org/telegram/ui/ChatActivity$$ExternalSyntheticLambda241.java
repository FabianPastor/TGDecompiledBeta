package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.Components.ReactedUsersListView;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda241 implements ReactedUsersListView.OnHeightChangedListener {
    public final /* synthetic */ ActionBarPopupWindow.ActionBarPopupWindowLayout f$0;
    public final /* synthetic */ int[] f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda241(ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout, int[] iArr) {
        this.f$0 = actionBarPopupWindowLayout;
        this.f$1 = iArr;
    }

    public final void onHeightChanged(ReactedUsersListView reactedUsersListView, int i) {
        this.f$0.getSwipeBack().setNewForegroundHeight(this.f$1[0], AndroidUtilities.dp(52.0f) + i, true);
    }
}
