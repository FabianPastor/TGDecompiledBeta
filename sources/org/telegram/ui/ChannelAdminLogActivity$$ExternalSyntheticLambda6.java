package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_channels_adminLogResults;

public final /* synthetic */ class ChannelAdminLogActivity$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ ChannelAdminLogActivity f$0;
    public final /* synthetic */ TLRPC$TL_channels_adminLogResults f$1;

    public /* synthetic */ ChannelAdminLogActivity$$ExternalSyntheticLambda6(ChannelAdminLogActivity channelAdminLogActivity, TLRPC$TL_channels_adminLogResults tLRPC$TL_channels_adminLogResults) {
        this.f$0 = channelAdminLogActivity;
        this.f$1 = tLRPC$TL_channels_adminLogResults;
    }

    public final void run() {
        this.f$0.lambda$loadMessages$0(this.f$1);
    }
}
