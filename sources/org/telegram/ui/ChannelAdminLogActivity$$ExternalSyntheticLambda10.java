package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChannelAdminLogActivity$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ ChannelAdminLogActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ ChannelAdminLogActivity$$ExternalSyntheticLambda10(ChannelAdminLogActivity channelAdminLogActivity, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = channelAdminLogActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m1614lambda$loadAdmins$10$orgtelegramuiChannelAdminLogActivity(this.f$1, this.f$2);
    }
}
