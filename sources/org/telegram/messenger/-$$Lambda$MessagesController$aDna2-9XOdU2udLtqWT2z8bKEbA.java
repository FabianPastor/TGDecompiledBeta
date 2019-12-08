package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$aDna2-9XOdU2udLtqWT2z8bKEbA implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$aDna2-9XOdU2udLtqWT2z8bKEbA INSTANCE = new -$$Lambda$MessagesController$aDna2-9XOdU2udLtqWT2z8bKEbA();

    private /* synthetic */ -$$Lambda$MessagesController$aDna2-9XOdU2udLtqWT2z8bKEbA() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$reportSpam$29(tLObject, tL_error);
    }
}
