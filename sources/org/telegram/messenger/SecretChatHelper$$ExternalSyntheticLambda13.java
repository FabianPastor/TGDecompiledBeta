package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda13 implements Runnable {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ TLRPC.Message f$1;
    public final /* synthetic */ TLRPC.messages_SentEncryptedMessage f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ String f$4;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda13(SecretChatHelper secretChatHelper, TLRPC.Message message, TLRPC.messages_SentEncryptedMessage messages_sentencryptedmessage, int i, String str) {
        this.f$0 = secretChatHelper;
        this.f$1 = message;
        this.f$2 = messages_sentencryptedmessage;
        this.f$3 = i;
        this.f$4 = str;
    }

    public final void run() {
        this.f$0.m1077xdfee8117(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
