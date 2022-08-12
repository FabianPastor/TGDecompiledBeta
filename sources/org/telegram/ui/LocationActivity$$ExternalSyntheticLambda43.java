package org.telegram.ui;

import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.Components.ProximitySheet;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda43 implements ProximitySheet.onRadiusPickerChange {
    public final /* synthetic */ LocationActivity f$0;
    public final /* synthetic */ TLRPC$User f$1;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda43(LocationActivity locationActivity, TLRPC$User tLRPC$User) {
        this.f$0 = locationActivity;
        this.f$1 = tLRPC$User;
    }

    public final boolean run(boolean z, int i) {
        return this.f$0.lambda$openProximityAlert$27(this.f$1, z, i);
    }
}
