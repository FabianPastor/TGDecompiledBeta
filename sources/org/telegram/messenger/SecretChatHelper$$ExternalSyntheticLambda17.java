package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$DecryptedMessage;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$InputEncryptedFile;
import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda17 implements Runnable {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ TLRPC$EncryptedChat f$1;
    public final /* synthetic */ TLRPC$DecryptedMessage f$2;
    public final /* synthetic */ TLRPC$Message f$3;
    public final /* synthetic */ TLRPC$InputEncryptedFile f$4;
    public final /* synthetic */ MessageObject f$5;
    public final /* synthetic */ String f$6;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda17(SecretChatHelper secretChatHelper, TLRPC$EncryptedChat tLRPC$EncryptedChat, TLRPC$DecryptedMessage tLRPC$DecryptedMessage, TLRPC$Message tLRPC$Message, TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile, MessageObject messageObject, String str) {
        this.f$0 = secretChatHelper;
        this.f$1 = tLRPC$EncryptedChat;
        this.f$2 = tLRPC$DecryptedMessage;
        this.f$3 = tLRPC$Message;
        this.f$4 = tLRPC$InputEncryptedFile;
        this.f$5 = messageObject;
        this.f$6 = str;
    }

    public final void run() {
        this.f$0.lambda$performSendEncryptedRequest$8(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
