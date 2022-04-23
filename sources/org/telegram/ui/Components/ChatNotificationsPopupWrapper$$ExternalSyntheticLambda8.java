package org.telegram.ui.Components;

import org.telegram.ui.Components.ChatNotificationsPopupWrapper;

public final /* synthetic */ class ChatNotificationsPopupWrapper$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ ChatNotificationsPopupWrapper.Callback f$0;

    public /* synthetic */ ChatNotificationsPopupWrapper$$ExternalSyntheticLambda8(ChatNotificationsPopupWrapper.Callback callback) {
        this.f$0 = callback;
    }

    public final void run() {
        this.f$0.toggleMute();
    }
}
