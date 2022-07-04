package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatLinkActivity$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ ChatLinkActivity f$0;

    public /* synthetic */ ChatLinkActivity$$ExternalSyntheticLambda5(ChatLinkActivity chatLinkActivity) {
        this.f$0 = chatLinkActivity;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3245lambda$loadChats$17$orgtelegramuiChatLinkActivity(tLObject, tL_error);
    }
}
