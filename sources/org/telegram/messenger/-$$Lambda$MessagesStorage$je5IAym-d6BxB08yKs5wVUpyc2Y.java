package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$je5IAym-d6BxB08yKs5wVUpyc2Y implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ User f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ int f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$je5IAym-d6BxB08yKs5wVUpyc2Y(MessagesStorage messagesStorage, User user, boolean z, int i) {
        this.f$0 = messagesStorage;
        this.f$1 = user;
        this.f$2 = z;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$loadUserInfo$71$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}