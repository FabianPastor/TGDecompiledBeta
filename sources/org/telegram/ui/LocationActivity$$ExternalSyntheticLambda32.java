package org.telegram.ui;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda32 implements MessagesStorage.IntCallback {
    public final /* synthetic */ LocationActivity f$0;
    public final /* synthetic */ TLRPC$User f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda32(LocationActivity locationActivity, TLRPC$User tLRPC$User, int i) {
        this.f$0 = locationActivity;
        this.f$1 = tLRPC$User;
        this.f$2 = i;
    }

    public final void run(int i) {
        this.f$0.lambda$openShareLiveLocation$30(this.f$1, this.f$2, i);
    }
}
