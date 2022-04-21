package org.telegram.ui.Components;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ChatNotificationsPopupWrapper;

public final /* synthetic */ class ChatNotificationsPopupWrapper$$ExternalSyntheticLambda1 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ ChatNotificationsPopupWrapper.Callback f$1;

    public /* synthetic */ ChatNotificationsPopupWrapper$$ExternalSyntheticLambda1(int i, ChatNotificationsPopupWrapper.Callback callback) {
        this.f$0 = i;
        this.f$1 = callback;
    }

    public final void didSelectDate(boolean z, int i) {
        AndroidUtilities.runOnUIThread(new ChatNotificationsPopupWrapper$$ExternalSyntheticLambda8(i, this.f$0, this.f$1), 16);
    }
}
