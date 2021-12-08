package org.telegram.messenger;

import org.telegram.messenger.ChatObject;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatObject$Call$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ ChatObject.Call f$0;

    public /* synthetic */ ChatObject$Call$$ExternalSyntheticLambda1(ChatObject.Call call) {
        this.f$0 = call;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m610lambda$setTitle$3$orgtelegrammessengerChatObject$Call(tLObject, tL_error);
    }
}
