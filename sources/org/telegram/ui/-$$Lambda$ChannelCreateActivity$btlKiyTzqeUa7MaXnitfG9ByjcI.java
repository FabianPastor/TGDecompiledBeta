package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChannelCreateActivity$btlKiyTzqeUa7MaXnitfG9ByjcI implements RequestDelegate {
    private final /* synthetic */ ChannelCreateActivity f$0;
    private final /* synthetic */ String f$1;

    public /* synthetic */ -$$Lambda$ChannelCreateActivity$btlKiyTzqeUa7MaXnitfG9ByjcI(ChannelCreateActivity channelCreateActivity, String str) {
        this.f$0 = channelCreateActivity;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$19$ChannelCreateActivity(this.f$1, tLObject, tL_error);
    }
}
