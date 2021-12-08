package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda27 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ LocationActivity f$0;
    public final /* synthetic */ TLRPC.TL_messageMediaVenue f$1;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda27(LocationActivity locationActivity, TLRPC.TL_messageMediaVenue tL_messageMediaVenue) {
        this.f$0 = locationActivity;
        this.f$1 = tL_messageMediaVenue;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.m3156lambda$createView$19$orgtelegramuiLocationActivity(this.f$1, z, i);
    }
}
