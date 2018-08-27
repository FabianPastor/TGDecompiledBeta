package org.telegram.messenger;

final /* synthetic */ class MessagesStorage$$Lambda$50 implements Runnable {
    private final MessagesStorage arg$1;
    private final String arg$2;
    private final String arg$3;

    MessagesStorage$$Lambda$50(MessagesStorage messagesStorage, String str, String str2) {
        this.arg$1 = messagesStorage;
        this.arg$2 = str;
        this.arg$3 = str2;
    }

    public void run() {
        this.arg$1.lambda$applyPhoneBookUpdates$72$MessagesStorage(this.arg$2, this.arg$3);
    }
}
