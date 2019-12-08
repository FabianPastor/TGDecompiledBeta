package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$SqK2B3MDGPnqui97ni4rzMahKnQ implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;

    public /* synthetic */ -$$Lambda$MessagesController$SqK2B3MDGPnqui97ni4rzMahKnQ(MessagesController messagesController) {
        this.f$0 = messagesController;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$pinMessage$79$MessagesController(tLObject, tL_error);
    }
}
