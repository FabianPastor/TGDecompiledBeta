package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda131 implements RequestDelegate {
    public final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda131(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3065lambda$processSelectedOption$200$orgtelegramuiChatActivity(tLObject, tL_error);
    }
}
