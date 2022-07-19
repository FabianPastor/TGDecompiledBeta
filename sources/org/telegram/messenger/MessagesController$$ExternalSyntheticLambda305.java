package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda305 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC$Chat f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda305(MessagesController messagesController, long j, TLRPC$Chat tLRPC$Chat) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = tLRPC$Chat;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadUnknownChannel$263(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
