package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessageSeenView$$ExternalSyntheticLambda2 implements RequestDelegate {
    public final /* synthetic */ MessageSeenView f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ MessageSeenView$$ExternalSyntheticLambda2(MessageSeenView messageSeenView, int i) {
        this.f$0 = messageSeenView;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$new$1(this.f$1, tLObject, tLRPC$TL_error);
    }
}
