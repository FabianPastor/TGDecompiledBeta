package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChannelCreateActivity$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ ChannelCreateActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ ChannelCreateActivity$$ExternalSyntheticLambda7(ChannelCreateActivity channelCreateActivity, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = channelCreateActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m1609lambda$generateLink$10$orgtelegramuiChannelCreateActivity(this.f$1, this.f$2);
    }
}
