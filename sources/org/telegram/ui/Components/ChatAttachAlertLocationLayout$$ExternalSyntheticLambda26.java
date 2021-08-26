package org.telegram.ui.Components;

import org.telegram.tgnet.TLRPC$TL_messageMediaVenue;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class ChatAttachAlertLocationLayout$$ExternalSyntheticLambda26 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ ChatAttachAlertLocationLayout f$0;
    public final /* synthetic */ TLRPC$TL_messageMediaVenue f$1;

    public /* synthetic */ ChatAttachAlertLocationLayout$$ExternalSyntheticLambda26(ChatAttachAlertLocationLayout chatAttachAlertLocationLayout, TLRPC$TL_messageMediaVenue tLRPC$TL_messageMediaVenue) {
        this.f$0 = chatAttachAlertLocationLayout;
        this.f$1 = tLRPC$TL_messageMediaVenue;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$new$14(this.f$1, z, i);
    }
}
