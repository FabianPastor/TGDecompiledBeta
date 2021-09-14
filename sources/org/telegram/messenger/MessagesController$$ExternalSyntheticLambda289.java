package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda289 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$Dialog f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda289(MessagesController messagesController, TLRPC$Dialog tLRPC$Dialog, long j) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$Dialog;
        this.f$2 = j;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$checkLastDialogMessage$176(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
