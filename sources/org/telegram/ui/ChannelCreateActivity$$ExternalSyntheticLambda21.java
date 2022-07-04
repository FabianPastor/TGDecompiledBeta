package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChannelCreateActivity$$ExternalSyntheticLambda21 implements RequestDelegate {
    public final /* synthetic */ ChannelCreateActivity f$0;

    public /* synthetic */ ChannelCreateActivity$$ExternalSyntheticLambda21(ChannelCreateActivity channelCreateActivity) {
        this.f$0 = channelCreateActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadAdminedChannels$16(tLObject, tLRPC$TL_error);
    }
}
