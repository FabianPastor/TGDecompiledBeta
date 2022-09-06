package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda341 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ TLRPC$User f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda341(MessagesController messagesController, boolean z, TLRPC$User tLRPC$User, long j) {
        this.f$0 = messagesController;
        this.f$1 = z;
        this.f$2 = tLRPC$User;
        this.f$3 = j;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$deleteParticipantFromChat$249(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
