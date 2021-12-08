package org.telegram.ui.Components;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class ChatAttachAlertLocationLayout$$ExternalSyntheticLambda17 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ ChatAttachAlertLocationLayout f$0;
    public final /* synthetic */ TLRPC.TL_messageMediaGeo f$1;

    public /* synthetic */ ChatAttachAlertLocationLayout$$ExternalSyntheticLambda17(ChatAttachAlertLocationLayout chatAttachAlertLocationLayout, TLRPC.TL_messageMediaGeo tL_messageMediaGeo) {
        this.f$0 = chatAttachAlertLocationLayout;
        this.f$1 = tL_messageMediaGeo;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.m2153xe9dbcf6d(this.f$1, z, i);
    }
}
