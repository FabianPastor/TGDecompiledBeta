package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class SecretChatHelper$$Lambda$12 implements RequestDelegate {
    private final SecretChatHelper arg$1;
    private final EncryptedChat arg$2;

    SecretChatHelper$$Lambda$12(SecretChatHelper secretChatHelper, EncryptedChat encryptedChat) {
        this.arg$1 = secretChatHelper;
        this.arg$2 = encryptedChat;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$acceptSecretChat$22$SecretChatHelper(this.arg$2, tLObject, tL_error);
    }
}
