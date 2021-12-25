package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatEditActivity$$ExternalSyntheticLambda28 implements RequestDelegate {
    public final /* synthetic */ ChatEditActivity f$0;

    public /* synthetic */ ChatEditActivity$$ExternalSyntheticLambda28(ChatEditActivity chatEditActivity) {
        this.f$0 = chatEditActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadLinksCount$1(tLObject, tLRPC$TL_error);
    }
}
