package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$UU6fcvD2-kOMU8I5scKYIYHk99o implements RequestDelegate {
    private final /* synthetic */ SendMessagesHelper f$0;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$UU6fcvD2-kOMU8I5scKYIYHk99o(SendMessagesHelper sendMessagesHelper) {
        this.f$0 = sendMessagesHelper;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$sendReaction$18$SendMessagesHelper(tLObject, tL_error);
    }
}
