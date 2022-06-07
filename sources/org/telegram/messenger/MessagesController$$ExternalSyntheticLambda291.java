package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda291 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda291(MessagesController messagesController, long j, int i, long j2) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = j2;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$getChannelDifference$273(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
