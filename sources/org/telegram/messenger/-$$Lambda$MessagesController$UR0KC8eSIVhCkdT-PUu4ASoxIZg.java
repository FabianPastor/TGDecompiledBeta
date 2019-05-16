package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$UR0KC8eSIVhCkdT-PUu4ASoxIZg implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ User f$1;

    public /* synthetic */ -$$Lambda$MessagesController$UR0KC8eSIVhCkdT-PUu4ASoxIZg(MessagesController messagesController, User user) {
        this.f$0 = messagesController;
        this.f$1 = user;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$blockUser$39$MessagesController(this.f$1, tLObject, tL_error);
    }
}
