package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChannelAdminLogActivity$$ExternalSyntheticLambda12 implements RequestDelegate {
    public final /* synthetic */ ChannelAdminLogActivity f$0;

    public /* synthetic */ ChannelAdminLogActivity$$ExternalSyntheticLambda12(ChannelAdminLogActivity channelAdminLogActivity) {
        this.f$0 = channelAdminLogActivity;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2863lambda$loadAdmins$11$orgtelegramuiChannelAdminLogActivity(tLObject, tL_error);
    }
}
