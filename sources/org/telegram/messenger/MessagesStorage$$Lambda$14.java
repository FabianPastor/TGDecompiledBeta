package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;

final /* synthetic */ class MessagesStorage$$Lambda$14 implements Runnable {
    private final MessagesStorage arg$1;
    private final Document arg$2;
    private final String arg$3;
    private final String arg$4;

    MessagesStorage$$Lambda$14(MessagesStorage messagesStorage, Document document, String str, String str2) {
        this.arg$1 = messagesStorage;
        this.arg$2 = document;
        this.arg$3 = str;
        this.arg$4 = str2;
    }

    public void run() {
        this.arg$1.lambda$addRecentLocalFile$24$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}
