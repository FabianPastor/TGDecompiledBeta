package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChannelAdminLogActivity$$ExternalSyntheticLambda10 implements RequestDelegate {
    public final /* synthetic */ ChannelAdminLogActivity f$0;

    public /* synthetic */ ChannelAdminLogActivity$$ExternalSyntheticLambda10(ChannelAdminLogActivity channelAdminLogActivity) {
        this.f$0 = channelAdminLogActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadMessages$1(tLObject, tLRPC$TL_error);
    }
}
