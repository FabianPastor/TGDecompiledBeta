package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChannelCreateActivity$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ ChannelCreateActivity f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ TLRPC.TL_error f$2;
    public final /* synthetic */ TLObject f$3;

    public /* synthetic */ ChannelCreateActivity$$ExternalSyntheticLambda3(ChannelCreateActivity channelCreateActivity, String str, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = channelCreateActivity;
        this.f$1 = str;
        this.f$2 = tL_error;
        this.f$3 = tLObject;
    }

    public final void run() {
        this.f$0.m1625lambda$checkUserName$19$orgtelegramuiChannelCreateActivity(this.f$1, this.f$2, this.f$3);
    }
}
