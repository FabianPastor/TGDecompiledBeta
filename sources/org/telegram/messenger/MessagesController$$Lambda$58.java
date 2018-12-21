package org.telegram.messenger;

final /* synthetic */ class MessagesController$$Lambda$58 implements Runnable {
    private final MessagesController arg$1;
    private final boolean arg$2;

    MessagesController$$Lambda$58(MessagesController messagesController, boolean z) {
        this.arg$1 = messagesController;
        this.arg$2 = z;
    }

    public void run() {
        this.arg$1.lambda$checkProxyInfo$78$MessagesController(this.arg$2);
    }
}
