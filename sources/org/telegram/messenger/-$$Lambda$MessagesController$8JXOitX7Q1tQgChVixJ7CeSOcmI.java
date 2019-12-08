package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$8JXOitX7Q1tQgChVixJ7CeSOcmI implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$8JXOitX7Q1tQgChVixJ7CeSOcmI INSTANCE = new -$$Lambda$MessagesController$8JXOitX7Q1tQgChVixJ7CeSOcmI();

    private /* synthetic */ -$$Lambda$MessagesController$8JXOitX7Q1tQgChVixJ7CeSOcmI() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$unblockUser$57(tLObject, tL_error);
    }
}
