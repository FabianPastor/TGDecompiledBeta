package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda219 implements RequestDelegate {
    public final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda219(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$createMenu$154(tLObject, tLRPC$TL_error);
    }
}
