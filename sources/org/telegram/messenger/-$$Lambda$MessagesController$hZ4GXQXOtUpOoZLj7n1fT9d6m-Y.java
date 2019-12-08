package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$hZ4GXQXOtUpOoZLj7n1fT9d6m-Y implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ long f$1;

    public /* synthetic */ -$$Lambda$MessagesController$hZ4GXQXOtUpOoZLj7n1fT9d6m-Y(MessagesController messagesController, long j) {
        this.f$0 = messagesController;
        this.f$1 = j;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$reorderPinnedDialogs$233$MessagesController(this.f$1, tLObject, tL_error);
    }
}
