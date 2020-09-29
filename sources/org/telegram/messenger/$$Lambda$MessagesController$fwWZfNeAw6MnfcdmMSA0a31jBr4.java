package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$fwWZfNeAw6MnfcdmMSA0a31jBr4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$fwWZfNeAw6MnfcdmMSA0a31jBr4 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$fwWZfNeAw6MnfcdmMSA0a31jBr4 INSTANCE = new $$Lambda$MessagesController$fwWZfNeAw6MnfcdmMSA0a31jBr4();

    private /* synthetic */ $$Lambda$MessagesController$fwWZfNeAw6MnfcdmMSA0a31jBr4() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMessageContentAsRead$171(tLObject, tLRPC$TL_error);
    }
}
