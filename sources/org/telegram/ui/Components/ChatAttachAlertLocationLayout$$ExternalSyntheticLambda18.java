package org.telegram.ui.Components;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class ChatAttachAlertLocationLayout$$ExternalSyntheticLambda18 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ ChatAttachAlertLocationLayout f$0;
    public final /* synthetic */ TLRPC.TL_messageMediaVenue f$1;

    public /* synthetic */ ChatAttachAlertLocationLayout$$ExternalSyntheticLambda18(ChatAttachAlertLocationLayout chatAttachAlertLocationLayout, TLRPC.TL_messageMediaVenue tL_messageMediaVenue) {
        this.f$0 = chatAttachAlertLocationLayout;
        this.f$1 = tL_messageMediaVenue;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.m2149xe2809d5d(this.f$1, z, i);
    }
}
