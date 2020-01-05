package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$SEw5IDt77PQJsopndUCrAMIHxLc implements RequestDelegate {
    private final /* synthetic */ SendMessagesHelper f$0;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$SEw5IDt77PQJsopndUCrAMIHxLc(SendMessagesHelper sendMessagesHelper) {
        this.f$0 = sendMessagesHelper;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$sendReaction$22$SendMessagesHelper(tLObject, tL_error);
    }
}
