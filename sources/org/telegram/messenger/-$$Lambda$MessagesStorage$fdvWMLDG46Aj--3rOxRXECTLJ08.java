package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$fdvWMLDG46Aj--3rOxRXECTLJ08 implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$fdvWMLDG46Aj--3rOxRXECTLJ08(MessagesStorage messagesStorage, int i, int i2) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = i2;
    }

    public final void run() {
        this.f$0.lambda$updateChatOnlineCount$76$MessagesStorage(this.f$1, this.f$2);
    }
}