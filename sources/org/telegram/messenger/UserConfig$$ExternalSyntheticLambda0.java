package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class UserConfig$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ UserConfig f$0;
    public final /* synthetic */ TLRPC.User f$1;

    public /* synthetic */ UserConfig$$ExternalSyntheticLambda0(UserConfig userConfig, TLRPC.User user) {
        this.f$0 = userConfig;
        this.f$1 = user;
    }

    public final void run() {
        this.f$0.m506lambda$checkPremium$1$orgtelegrammessengerUserConfig(this.f$1);
    }
}
