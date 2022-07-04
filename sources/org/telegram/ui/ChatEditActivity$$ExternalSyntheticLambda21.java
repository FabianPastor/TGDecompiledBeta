package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatEditActivity$$ExternalSyntheticLambda21 implements RequestDelegate {
    public final /* synthetic */ ChatEditActivity f$0;

    public /* synthetic */ ChatEditActivity$$ExternalSyntheticLambda21(ChatEditActivity chatEditActivity) {
        this.f$0 = chatEditActivity;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3206lambda$loadLinksCount$1$orgtelegramuiChatEditActivity(tLObject, tL_error);
    }
}
