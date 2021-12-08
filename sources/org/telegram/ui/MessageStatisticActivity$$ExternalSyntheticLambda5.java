package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessageStatisticActivity$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ MessageStatisticActivity f$0;

    public /* synthetic */ MessageStatisticActivity$$ExternalSyntheticLambda5(MessageStatisticActivity messageStatisticActivity) {
        this.f$0 = messageStatisticActivity;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3304lambda$loadChats$3$orgtelegramuiMessageStatisticActivity(tLObject, tL_error);
    }
}
