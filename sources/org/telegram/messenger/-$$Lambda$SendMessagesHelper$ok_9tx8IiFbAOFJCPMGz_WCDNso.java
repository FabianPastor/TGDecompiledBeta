package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$ok_9tx8IiFbAOFJCPMGz_WCDNso implements RequestDelegate {
    private final /* synthetic */ SendMessagesHelper f$0;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$ok_9tx8IiFbAOFJCPMGz_WCDNso(SendMessagesHelper sendMessagesHelper) {
        this.f$0 = sendMessagesHelper;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$sendReaction$20$SendMessagesHelper(tLObject, tL_error);
    }
}
