package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.messages_SentEncryptedMessage;

final /* synthetic */ class SecretChatHelper$$Lambda$28 implements Runnable {
    private final SecretChatHelper arg$1;
    private final Message arg$2;
    private final messages_SentEncryptedMessage arg$3;
    private final int arg$4;
    private final String arg$5;

    SecretChatHelper$$Lambda$28(SecretChatHelper secretChatHelper, Message message, messages_SentEncryptedMessage messages_sentencryptedmessage, int i, String str) {
        this.arg$1 = secretChatHelper;
        this.arg$2 = message;
        this.arg$3 = messages_sentencryptedmessage;
        this.arg$4 = i;
        this.arg$5 = str;
    }

    public void run() {
        this.arg$1.lambda$null$4$SecretChatHelper(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
