package org.telegram.ui.Components;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC$TL_messageMediaPoll;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class ChatAttachAlertPollLayout$$ExternalSyntheticLambda1 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ ChatAttachAlertPollLayout f$0;
    public final /* synthetic */ TLRPC$TL_messageMediaPoll f$1;
    public final /* synthetic */ HashMap f$2;

    public /* synthetic */ ChatAttachAlertPollLayout$$ExternalSyntheticLambda1(ChatAttachAlertPollLayout chatAttachAlertPollLayout, TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll, HashMap hashMap) {
        this.f$0 = chatAttachAlertPollLayout;
        this.f$1 = tLRPC$TL_messageMediaPoll;
        this.f$2 = hashMap;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$onMenuItemClick$1(this.f$1, this.f$2, z, i);
    }
}
