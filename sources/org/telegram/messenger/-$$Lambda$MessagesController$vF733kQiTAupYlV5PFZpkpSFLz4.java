package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserFull;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$vvar_kQiTAupYlV5PFZpkpSFLz4 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ User f$1;
    private final /* synthetic */ UserFull f$2;
    private final /* synthetic */ MessageObject f$3;

    public /* synthetic */ -$$Lambda$MessagesController$vvar_kQiTAupYlV5PFZpkpSFLz4(MessagesController messagesController, User user, UserFull userFull, MessageObject messageObject) {
        this.f$0 = messagesController;
        this.f$1 = user;
        this.f$2 = userFull;
        this.f$3 = messageObject;
    }

    public final void run() {
        this.f$0.lambda$processUserInfo$76$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}
