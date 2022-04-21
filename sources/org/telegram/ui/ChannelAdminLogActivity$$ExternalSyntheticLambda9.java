package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChannelAdminLogActivity$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ ChannelAdminLogActivity f$0;
    public final /* synthetic */ TLRPC.TL_channels_adminLogResults f$1;

    public /* synthetic */ ChannelAdminLogActivity$$ExternalSyntheticLambda9(ChannelAdminLogActivity channelAdminLogActivity, TLRPC.TL_channels_adminLogResults tL_channels_adminLogResults) {
        this.f$0 = channelAdminLogActivity;
        this.f$1 = tL_channels_adminLogResults;
    }

    public final void run() {
        this.f$0.m1589lambda$loadMessages$0$orgtelegramuiChannelAdminLogActivity(this.f$1);
    }
}
