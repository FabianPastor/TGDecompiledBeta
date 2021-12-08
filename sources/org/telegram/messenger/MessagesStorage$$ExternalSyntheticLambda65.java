package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda65 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC.EncryptedChat f$1;
    public final /* synthetic */ TLRPC.User f$2;
    public final /* synthetic */ TLRPC.Dialog f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda65(MessagesStorage messagesStorage, TLRPC.EncryptedChat encryptedChat, TLRPC.User user, TLRPC.Dialog dialog) {
        this.f$0 = messagesStorage;
        this.f$1 = encryptedChat;
        this.f$2 = user;
        this.f$3 = dialog;
    }

    public final void run() {
        this.f$0.m1024x2acb6e3e(this.f$1, this.f$2, this.f$3);
    }
}
