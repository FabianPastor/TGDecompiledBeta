package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatEditActivity$$ExternalSyntheticLambda26 implements Runnable {
    public final /* synthetic */ ChatEditActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ ChatEditActivity$$ExternalSyntheticLambda26(ChatEditActivity chatEditActivity, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        this.f$0 = chatEditActivity;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$loadLinksCount$0(this.f$1, this.f$2);
    }
}
