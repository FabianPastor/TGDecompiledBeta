package org.telegram.ui;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda17 implements MessagesStorage.IntCallback {
    public final /* synthetic */ LocationActivity f$0;
    public final /* synthetic */ TLRPC.User f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda17(LocationActivity locationActivity, TLRPC.User user, int i) {
        this.f$0 = locationActivity;
        this.f$1 = user;
        this.f$2 = i;
    }

    public final void run(int i) {
        this.f$0.m2439lambda$openShareLiveLocation$26$orgtelegramuiLocationActivity(this.f$1, this.f$2, i);
    }
}
