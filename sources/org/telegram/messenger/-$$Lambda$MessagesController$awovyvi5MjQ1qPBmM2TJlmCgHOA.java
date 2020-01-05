package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$awovyvi5MjQ1qPBmM2TJlmCgHOA implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ long f$2;

    public /* synthetic */ -$$Lambda$MessagesController$awovyvi5MjQ1qPBmM2TJlmCgHOA(MessagesController messagesController, long j, long j2) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = j2;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadUnknownDialog$134$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
