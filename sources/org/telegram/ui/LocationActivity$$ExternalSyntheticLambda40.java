package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_messageMediaGeo;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda40 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ LocationActivity f$0;
    public final /* synthetic */ TLRPC$TL_messageMediaGeo f$1;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda40(LocationActivity locationActivity, TLRPC$TL_messageMediaGeo tLRPC$TL_messageMediaGeo) {
        this.f$0 = locationActivity;
        this.f$1 = tLRPC$TL_messageMediaGeo;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$createView$12(this.f$1, z, i);
    }
}
