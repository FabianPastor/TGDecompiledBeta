package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ TLRPC.EncryptedChat f$1;
    public final /* synthetic */ TLRPC.DecryptedMessage f$2;
    public final /* synthetic */ TLRPC.Message f$3;
    public final /* synthetic */ TLRPC.InputEncryptedFile f$4;
    public final /* synthetic */ MessageObject f$5;
    public final /* synthetic */ String f$6;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda8(SecretChatHelper secretChatHelper, TLRPC.EncryptedChat encryptedChat, TLRPC.DecryptedMessage decryptedMessage, TLRPC.Message message, TLRPC.InputEncryptedFile inputEncryptedFile, MessageObject messageObject, String str) {
        this.f$0 = secretChatHelper;
        this.f$1 = encryptedChat;
        this.f$2 = decryptedMessage;
        this.f$3 = message;
        this.f$4 = inputEncryptedFile;
        this.f$5 = messageObject;
        this.f$6 = str;
    }

    public final void run() {
        this.f$0.m1080x85cvar_a(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
