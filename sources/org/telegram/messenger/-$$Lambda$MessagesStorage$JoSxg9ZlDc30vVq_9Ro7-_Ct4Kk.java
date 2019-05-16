package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$JoSxg9ZlDCLASSNAMEvVq_9Ro7-_Ct4Kk implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$JoSxg9ZlDCLASSNAMEvVq_9Ro7-_Ct4Kk(MessagesStorage messagesStorage, int i, int i2) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = i2;
    }

    public final void run() {
        this.f$0.lambda$updateChatOnlineCount$78$MessagesStorage(this.f$1, this.f$2);
    }
}
