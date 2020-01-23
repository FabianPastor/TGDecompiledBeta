package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserFull;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$larH52SMeyMkTT15kLMMX9Lbkmc implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ User f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ boolean f$4;
    private final /* synthetic */ UserFull f$5;
    private final /* synthetic */ MessageObject f$6;

    public /* synthetic */ -$$Lambda$MessagesController$larH52SMeyMkTT15kLMMX9Lbkmc(MessagesController messagesController, boolean z, User user, int i, boolean z2, UserFull userFull, MessageObject messageObject) {
        this.f$0 = messagesController;
        this.f$1 = z;
        this.f$2 = user;
        this.f$3 = i;
        this.f$4 = z2;
        this.f$5 = userFull;
        this.f$6 = messageObject;
    }

    public final void run() {
        this.f$0.lambda$processUserInfo$91$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
