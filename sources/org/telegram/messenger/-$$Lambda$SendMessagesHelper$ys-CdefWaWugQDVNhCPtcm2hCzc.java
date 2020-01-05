package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$ys-CdefWaWugQDVNhCPtcm2hCzc implements RequestDelegate {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ MessageObject f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ Runnable f$3;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$ys-CdefWaWugQDVNhCPtcm2hCzc(SendMessagesHelper sendMessagesHelper, MessageObject messageObject, String str, Runnable runnable) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = messageObject;
        this.f$2 = str;
        this.f$3 = runnable;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$sendVote$21$SendMessagesHelper(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
