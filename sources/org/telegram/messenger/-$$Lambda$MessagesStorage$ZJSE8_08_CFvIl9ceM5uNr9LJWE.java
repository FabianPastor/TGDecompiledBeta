package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.UserFull;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$ZJSE8_08_CFvIl9ceM5uNr9LJWE implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ UserFull f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$ZJSE8_08_CFvIl9ceM5uNr9LJWE(MessagesStorage messagesStorage, boolean z, UserFull userFull) {
        this.f$0 = messagesStorage;
        this.f$1 = z;
        this.f$2 = userFull;
    }

    public final void run() {
        this.f$0.lambda$updateUserInfo$72$MessagesStorage(this.f$1, this.f$2);
    }
}
