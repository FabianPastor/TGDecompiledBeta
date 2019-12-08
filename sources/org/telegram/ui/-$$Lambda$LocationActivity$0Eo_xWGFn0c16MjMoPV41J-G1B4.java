package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationActivity$0Eo_xWGFn0CLASSNAMEMjMoPV41J-G1B4 implements ScheduleDatePickerDelegate {
    private final /* synthetic */ LocationActivity f$0;
    private final /* synthetic */ TL_messageMediaVenue f$1;

    public /* synthetic */ -$$Lambda$LocationActivity$0Eo_xWGFn0CLASSNAMEMjMoPV41J-G1B4(LocationActivity locationActivity, TL_messageMediaVenue tL_messageMediaVenue) {
        this.f$0 = locationActivity;
        this.f$1 = tL_messageMediaVenue;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$null$11$LocationActivity(this.f$1, z, i);
    }
}
