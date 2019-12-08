package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$sl3tSQuaAaxIP0aEjQCXxgX3vqE implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$sl3tSQuaAaxIP0aEjQCXxgX3vqE INSTANCE = new -$$Lambda$MessagesController$sl3tSQuaAaxIP0aEjQCXxgX3vqE();

    private /* synthetic */ -$$Lambda$MessagesController$sl3tSQuaAaxIP0aEjQCXxgX3vqE() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$markMessageContentAsRead$144(tLObject, tL_error);
    }
}
