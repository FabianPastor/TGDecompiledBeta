package org.telegram.ui.Components;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ChatNotificationsPopupWrapper;

public final /* synthetic */ class ChatNotificationsPopupWrapper$$ExternalSyntheticLambda10 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ ChatNotificationsPopupWrapper.Callback f$1;

    public /* synthetic */ ChatNotificationsPopupWrapper$$ExternalSyntheticLambda10(int i, ChatNotificationsPopupWrapper.Callback callback) {
        this.f$0 = i;
        this.f$1 = callback;
    }

    public final void didSelectDate(boolean z, int i) {
        AndroidUtilities.runOnUIThread(new ChatNotificationsPopupWrapper$$ExternalSyntheticLambda7(this.f$0, i, this.f$1), 16);
    }
}
