package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChannelAdminLogActivity$v_o39r5RzWtyA17rINStweDzJAI implements RequestDelegate {
    private final /* synthetic */ ChannelAdminLogActivity f$0;

    public /* synthetic */ -$$Lambda$ChannelAdminLogActivity$v_o39r5RzWtyA17rINStweDzJAI(ChannelAdminLogActivity channelAdminLogActivity) {
        this.f$0 = channelAdminLogActivity;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadAdmins$13$ChannelAdminLogActivity(tLObject, tL_error);
    }
}
