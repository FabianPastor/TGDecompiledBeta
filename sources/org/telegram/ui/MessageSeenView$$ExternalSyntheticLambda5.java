package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessageSeenView$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ MessageSeenView f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC$Chat f$3;

    public /* synthetic */ MessageSeenView$$ExternalSyntheticLambda5(MessageSeenView messageSeenView, long j, int i, TLRPC$Chat tLRPC$Chat) {
        this.f$0 = messageSeenView;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = tLRPC$Chat;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$new$5(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
