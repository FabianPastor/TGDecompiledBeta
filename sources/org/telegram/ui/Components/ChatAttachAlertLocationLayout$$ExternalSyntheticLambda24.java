package org.telegram.ui.Components;

import org.telegram.tgnet.TLRPC$TL_messageMediaGeo;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class ChatAttachAlertLocationLayout$$ExternalSyntheticLambda24 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ ChatAttachAlertLocationLayout f$0;
    public final /* synthetic */ TLRPC$TL_messageMediaGeo f$1;

    public /* synthetic */ ChatAttachAlertLocationLayout$$ExternalSyntheticLambda24(ChatAttachAlertLocationLayout chatAttachAlertLocationLayout, TLRPC$TL_messageMediaGeo tLRPC$TL_messageMediaGeo) {
        this.f$0 = chatAttachAlertLocationLayout;
        this.f$1 = tLRPC$TL_messageMediaGeo;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$new$5(this.f$1, z, i);
    }
}
