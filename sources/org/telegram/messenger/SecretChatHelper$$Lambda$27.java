package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.DecryptedMessage;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class SecretChatHelper$$Lambda$27 implements RequestDelegate {
    private final SecretChatHelper arg$1;
    private final DecryptedMessage arg$2;
    private final EncryptedChat arg$3;
    private final Message arg$4;
    private final MessageObject arg$5;
    private final String arg$6;

    SecretChatHelper$$Lambda$27(SecretChatHelper secretChatHelper, DecryptedMessage decryptedMessage, EncryptedChat encryptedChat, Message message, MessageObject messageObject, String str) {
        this.arg$1 = secretChatHelper;
        this.arg$2 = decryptedMessage;
        this.arg$3 = encryptedChat;
        this.arg$4 = message;
        this.arg$5 = messageObject;
        this.arg$6 = str;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$6$SecretChatHelper(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, tLObject, tL_error);
    }
}
