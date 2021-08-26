package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda304 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda304 INSTANCE = new MessagesController$$ExternalSyntheticLambda304();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda304() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$deleteParticipantFromChat$227(tLObject, tLRPC$TL_error);
    }
}
