package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda169 implements RequestDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda169(ChatActivity chatActivity, TLObject tLObject) {
        this.f$0 = chatActivity;
        this.f$1 = tLObject;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$createView$17(this.f$1, tLObject, tLRPC$TL_error);
    }
}
