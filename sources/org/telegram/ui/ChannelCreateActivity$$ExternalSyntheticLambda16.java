package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChannelCreateActivity$$ExternalSyntheticLambda16 implements RequestDelegate {
    public final /* synthetic */ ChannelCreateActivity f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ ChannelCreateActivity$$ExternalSyntheticLambda16(ChannelCreateActivity channelCreateActivity, String str) {
        this.f$0 = channelCreateActivity;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2874lambda$checkUserName$22$orgtelegramuiChannelCreateActivity(this.f$1, tLObject, tL_error);
    }
}
