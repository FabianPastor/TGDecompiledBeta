package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda262 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda262(MessagesController messagesController, long j, long j2) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = j2;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadUnknownDialog$157(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}