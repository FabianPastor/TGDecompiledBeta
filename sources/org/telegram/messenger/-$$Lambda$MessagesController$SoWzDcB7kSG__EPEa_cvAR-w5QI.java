package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$SoWzDcB7kSG__EPEa_cvAR-w5QI implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ long f$1;

    public /* synthetic */ -$$Lambda$MessagesController$SoWzDcB7kSG__EPEa_cvAR-w5QI(MessagesController messagesController, long j) {
        this.f$0 = messagesController;
        this.f$1 = j;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$markMessageAsRead$159$MessagesController(this.f$1, tLObject, tL_error);
    }
}
