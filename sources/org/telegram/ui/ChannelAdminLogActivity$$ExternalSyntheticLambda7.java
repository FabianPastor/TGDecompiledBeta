package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChannelAdminLogActivity$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ ChannelAdminLogActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ ChannelAdminLogActivity$$ExternalSyntheticLambda7(ChannelAdminLogActivity channelAdminLogActivity, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        this.f$0 = channelAdminLogActivity;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$loadAdmins$10(this.f$1, this.f$2);
    }
}
