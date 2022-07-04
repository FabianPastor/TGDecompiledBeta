package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda32 implements DialogInterface.OnClickListener {
    public final /* synthetic */ LocationActivity f$0;
    public final /* synthetic */ TLRPC.User f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda32(LocationActivity locationActivity, TLRPC.User user, int i) {
        this.f$0 = locationActivity;
        this.f$1 = user;
        this.f$2 = i;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3758lambda$openProximityAlert$22$orgtelegramuiLocationActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}
