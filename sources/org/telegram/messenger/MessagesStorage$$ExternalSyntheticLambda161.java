package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda161 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$EncryptedChat f$1;
    public final /* synthetic */ TLRPC$User f$2;
    public final /* synthetic */ TLRPC$Dialog f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda161(MessagesStorage messagesStorage, TLRPC$EncryptedChat tLRPC$EncryptedChat, TLRPC$User tLRPC$User, TLRPC$Dialog tLRPC$Dialog) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$EncryptedChat;
        this.f$2 = tLRPC$User;
        this.f$3 = tLRPC$Dialog;
    }

    public final void run() {
        this.f$0.lambda$putEncryptedChat$136(this.f$1, this.f$2, this.f$3);
    }
}
