package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserFull;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$hx9xkt8bBM-1LwEylyyTU9CVME0 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ UserFull f$1;
    private final /* synthetic */ User f$2;
    private final /* synthetic */ int f$3;

    public /* synthetic */ -$$Lambda$MessagesController$hx9xkt8bBM-1LwEylyyTU9CVME0(MessagesController messagesController, UserFull userFull, User user, int i) {
        this.f$0 = messagesController;
        this.f$1 = userFull;
        this.f$2 = user;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$null$20$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}
