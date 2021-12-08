package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda26 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ LocationActivity f$0;
    public final /* synthetic */ TLRPC.TL_messageMediaGeo f$1;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda26(LocationActivity locationActivity, TLRPC.TL_messageMediaGeo tL_messageMediaGeo) {
        this.f$0 = locationActivity;
        this.f$1 = tL_messageMediaGeo;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.m3149lambda$createView$12$orgtelegramuiLocationActivity(this.f$1, z, i);
    }
}
