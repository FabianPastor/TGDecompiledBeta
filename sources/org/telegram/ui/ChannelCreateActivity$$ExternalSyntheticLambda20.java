package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChannelCreateActivity$$ExternalSyntheticLambda20 implements RequestDelegate {
    public final /* synthetic */ ChannelCreateActivity f$0;

    public /* synthetic */ ChannelCreateActivity$$ExternalSyntheticLambda20(ChannelCreateActivity channelCreateActivity) {
        this.f$0 = channelCreateActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$generateLink$13(tLObject, tLRPC$TL_error);
    }
}
