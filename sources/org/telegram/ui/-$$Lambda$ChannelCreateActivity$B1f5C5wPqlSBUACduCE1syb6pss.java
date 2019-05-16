package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChannelCreateActivity$B1f5C5wPqlSBUACduCE1syb6pss implements RequestDelegate {
    private final /* synthetic */ ChannelCreateActivity f$0;

    public /* synthetic */ -$$Lambda$ChannelCreateActivity$B1f5C5wPqlSBUACduCE1syb6pss(ChannelCreateActivity channelCreateActivity) {
        this.f$0 = channelCreateActivity;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$generateLink$10$ChannelCreateActivity(tLObject, tL_error);
    }
}
