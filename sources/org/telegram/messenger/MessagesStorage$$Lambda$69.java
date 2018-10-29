package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class MessagesStorage$$Lambda$69 implements Runnable {
    private final MessagesStorage arg$1;
    private final EncryptedChat arg$2;
    private final User arg$3;
    private final TL_dialog arg$4;

    MessagesStorage$$Lambda$69(MessagesStorage messagesStorage, EncryptedChat encryptedChat, User user, TL_dialog tL_dialog) {
        this.arg$1 = messagesStorage;
        this.arg$2 = encryptedChat;
        this.arg$3 = user;
        this.arg$4 = tL_dialog;
    }

    public void run() {
        this.arg$1.lambda$putEncryptedChat$93$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}
