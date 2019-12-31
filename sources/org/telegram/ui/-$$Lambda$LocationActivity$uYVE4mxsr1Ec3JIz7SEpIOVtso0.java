package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationActivity$uYVE4mxsr1Ec3JIz7SEpIOVtso0 implements ScheduleDatePickerDelegate {
    private final /* synthetic */ LocationActivity f$0;
    private final /* synthetic */ TL_messageMediaVenue f$1;

    public /* synthetic */ -$$Lambda$LocationActivity$uYVE4mxsr1Ec3JIz7SEpIOVtso0(LocationActivity locationActivity, TL_messageMediaVenue tL_messageMediaVenue) {
        this.f$0 = locationActivity;
        this.f$1 = tL_messageMediaVenue;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$null$14$LocationActivity(this.f$1, z, i);
    }
}
