package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$0yMZVwrc7W9ZqZ8puLkQWwGfdhA implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ boolean f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$0yMZVwrc7W9ZqZ8puLkQWwGfdhA(MessagesStorage messagesStorage, int i, int i2, boolean z) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.lambda$markMessagesAsDeleted$135$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}