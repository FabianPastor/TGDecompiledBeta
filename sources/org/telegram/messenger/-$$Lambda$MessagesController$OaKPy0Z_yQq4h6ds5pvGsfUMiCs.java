package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$OaKPy0Z_yQq4h6ds5pvGsfUMiCs implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ long f$1;

    public /* synthetic */ -$$Lambda$MessagesController$OaKPy0Z_yQq4h6ds5pvGsfUMiCs(MessagesController messagesController, long j) {
        this.f$0 = messagesController;
        this.f$1 = j;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$147$MessagesController(this.f$1, tLObject, tL_error);
    }
}
