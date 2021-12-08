package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda245 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda245 INSTANCE = new MessagesController$$ExternalSyntheticLambda245();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda245() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$deleteParticipantFromChat$230(tLObject, tL_error);
    }
}
