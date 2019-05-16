package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.messages_SentEncryptedMessage;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SecretChatHelper$kXyPCeVLRJzYiyabGiQYTza0QCE implements Runnable {
    private final /* synthetic */ SecretChatHelper f$0;
    private final /* synthetic */ Message f$1;
    private final /* synthetic */ messages_SentEncryptedMessage f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ String f$4;

    public /* synthetic */ -$$Lambda$SecretChatHelper$kXyPCeVLRJzYiyabGiQYTza0QCE(SecretChatHelper secretChatHelper, Message message, messages_SentEncryptedMessage messages_sentencryptedmessage, int i, String str) {
        this.f$0 = secretChatHelper;
        this.f$1 = message;
        this.f$2 = messages_sentencryptedmessage;
        this.f$3 = i;
        this.f$4 = str;
    }

    public final void run() {
        this.f$0.lambda$null$4$SecretChatHelper(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
