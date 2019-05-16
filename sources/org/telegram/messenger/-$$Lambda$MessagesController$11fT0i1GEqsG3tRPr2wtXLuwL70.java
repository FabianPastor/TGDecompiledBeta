package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserFull;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$11fT0i1GEqsG3tRPr2wtXLuwL70 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ User f$1;
    private final /* synthetic */ UserFull f$2;
    private final /* synthetic */ int f$3;

    public /* synthetic */ -$$Lambda$MessagesController$11fT0i1GEqsG3tRPr2wtXLuwL70(MessagesController messagesController, User user, UserFull userFull, int i) {
        this.f$0 = messagesController;
        this.f$1 = user;
        this.f$2 = userFull;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$null$18$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}
