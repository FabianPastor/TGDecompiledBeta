package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class MessagesStorage$$Lambda$67 implements Runnable {
    private final MessagesStorage arg$1;
    private final String arg$2;
    private final TLObject arg$3;
    private final int arg$4;
    private final String arg$5;

    MessagesStorage$$Lambda$67(MessagesStorage messagesStorage, String str, TLObject tLObject, int i, String str2) {
        this.arg$1 = messagesStorage;
        this.arg$2 = str;
        this.arg$3 = tLObject;
        this.arg$4 = i;
        this.arg$5 = str2;
    }

    public void run() {
        this.arg$1.lambda$putSentFile$94$MessagesStorage(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
