package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.ProximitySheet;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda29 implements ProximitySheet.onRadiusPickerChange {
    public final /* synthetic */ LocationActivity f$0;
    public final /* synthetic */ TLRPC.User f$1;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda29(LocationActivity locationActivity, TLRPC.User user) {
        this.f$0 = locationActivity;
        this.f$1 = user;
    }

    public final boolean run(boolean z, int i) {
        return this.f$0.m3176lambda$openProximityAlert$23$orgtelegramuiLocationActivity(this.f$1, z, i);
    }
}
