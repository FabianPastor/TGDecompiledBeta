package org.telegram.messenger;

final /* synthetic */ class MessagesController$$Lambda$61 implements Runnable {
    private final MessagesController arg$1;
    private final boolean arg$2;

    MessagesController$$Lambda$61(MessagesController messagesController, boolean z) {
        this.arg$1 = messagesController;
        this.arg$2 = z;
    }

    public void run() {
        this.arg$1.lambda$checkProxyInfo$85$MessagesController(this.arg$2);
    }
}
