package org.telegram.messenger;

final /* synthetic */ class MessagesStorage$$Lambda$38 implements Runnable {
    private final MessagesStorage arg$1;
    private final int arg$2;

    MessagesStorage$$Lambda$38(MessagesStorage messagesStorage, int i) {
        this.arg$1 = messagesStorage;
        this.arg$2 = i;
    }

    public void run() {
        this.arg$1.lambda$loadChannelAdmins$58$MessagesStorage(this.arg$2);
    }
}
