package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChannelCreateActivity$$ExternalSyntheticLambda21 implements RequestDelegate {
    public final /* synthetic */ ChannelCreateActivity f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ ChannelCreateActivity$$ExternalSyntheticLambda21(ChannelCreateActivity channelCreateActivity, String str) {
        this.f$0 = channelCreateActivity;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$checkUserName$20(this.f$1, tLObject, tLRPC$TL_error);
    }
}
