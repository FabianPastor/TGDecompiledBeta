package org.telegram.messenger;

import org.telegram.messenger.ChatObject;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatObject$Call$$ExternalSyntheticLambda11 implements RequestDelegate {
    public final /* synthetic */ ChatObject.Call f$0;

    public /* synthetic */ ChatObject$Call$$ExternalSyntheticLambda11(ChatObject.Call call) {
        this.f$0 = call;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$setTitle$3(tLObject, tLRPC$TL_error);
    }
}