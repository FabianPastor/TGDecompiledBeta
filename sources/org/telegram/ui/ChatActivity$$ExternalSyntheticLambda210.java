package org.telegram.ui;

import org.telegram.ui.Components.PopupSwipeBackLayout;
import org.telegram.ui.Components.ReactionsContainerLayout;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda210 implements PopupSwipeBackLayout.OnSwipeBackProgressListener {
    public final /* synthetic */ ReactionsContainerLayout f$0;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda210(ReactionsContainerLayout reactionsContainerLayout) {
        this.f$0 = reactionsContainerLayout;
    }

    public final void onSwipeBackProgress(PopupSwipeBackLayout popupSwipeBackLayout, float f, float f2) {
        ChatActivity.lambda$createMenu$154(this.f$0, popupSwipeBackLayout, f, f2);
    }
}
