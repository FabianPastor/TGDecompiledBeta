package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Dialog;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$r7YA4KOVPgAq6rKrM4Mvep6cSOo implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ EncryptedChat f$1;
    private final /* synthetic */ User f$2;
    private final /* synthetic */ Dialog f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$r7YA4KOVPgAq6rKrM4Mvep6cSOo(MessagesStorage messagesStorage, EncryptedChat encryptedChat, User user, Dialog dialog) {
        this.f$0 = messagesStorage;
        this.f$1 = encryptedChat;
        this.f$2 = user;
        this.f$3 = dialog;
    }

    public final void run() {
        this.f$0.lambda$putEncryptedChat$109$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}