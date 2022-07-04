package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class UserConfig$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ UserConfig f$0;
    public final /* synthetic */ TLRPC$User f$1;

    public /* synthetic */ UserConfig$$ExternalSyntheticLambda0(UserConfig userConfig, TLRPC$User tLRPC$User) {
        this.f$0 = userConfig;
        this.f$1 = tLRPC$User;
    }

    public final void run() {
        this.f$0.lambda$checkPremium$1(this.f$1);
    }
}
