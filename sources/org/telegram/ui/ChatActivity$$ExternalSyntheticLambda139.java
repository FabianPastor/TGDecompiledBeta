package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda139 implements RequestDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda139(ChatActivity chatActivity, TLObject tLObject) {
        this.f$0 = chatActivity;
        this.f$1 = tLObject;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2941lambda$createView$19$orgtelegramuiChatActivity(this.f$1, tLObject, tL_error);
    }
}
