package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessageSeenView$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ MessageSeenView f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MessageSeenView$$ExternalSyntheticLambda1(MessageSeenView messageSeenView, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i) {
        this.f$0 = messageSeenView;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$new$2(this.f$1, this.f$2, this.f$3);
    }
}
