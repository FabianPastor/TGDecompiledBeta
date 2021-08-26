package org.telegram.messenger.voip;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda88 implements RequestDelegate {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ MessagesStorage f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda88(VoIPService voIPService, MessagesStorage messagesStorage) {
        this.f$0 = voIPService;
        this.f$1 = messagesStorage;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$acceptIncomingCall$69(this.f$1, tLObject, tLRPC$TL_error);
    }
}
