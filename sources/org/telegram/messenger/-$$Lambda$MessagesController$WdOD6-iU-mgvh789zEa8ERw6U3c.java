package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$WdOD6-iU-mgvh789zEa8ERw6U3c implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ Integer f$1;

    public /* synthetic */ -$$Lambda$MessagesController$WdOD6-iU-mgvh789zEa8ERw6U3c(MessagesController messagesController, Integer num) {
        this.f$0 = messagesController;
        this.f$1 = num;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadChannelParticipants$78$MessagesController(this.f$1, tLObject, tL_error);
    }
}