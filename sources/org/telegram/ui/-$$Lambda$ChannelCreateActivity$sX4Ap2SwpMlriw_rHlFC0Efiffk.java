package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChannelCreateActivity$sX4Ap2SwpMlriw_rHlFC0Efiffk implements RequestDelegate {
    private final /* synthetic */ ChannelCreateActivity f$0;

    public /* synthetic */ -$$Lambda$ChannelCreateActivity$sX4Ap2SwpMlriw_rHlFC0Efiffk(ChannelCreateActivity channelCreateActivity) {
        this.f$0 = channelCreateActivity;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadAdminedChannels$17$ChannelCreateActivity(tLObject, tL_error);
    }
}
