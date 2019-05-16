package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SecretChatHelper$13Zq8UfkfLhsSEm3Hl6dWS1zGEc implements RequestDelegate {
    private final /* synthetic */ SecretChatHelper f$0;
    private final /* synthetic */ EncryptedChat f$1;

    public /* synthetic */ -$$Lambda$SecretChatHelper$13Zq8UfkfLhsSEm3Hl6dWS1zGEc(SecretChatHelper secretChatHelper, EncryptedChat encryptedChat) {
        this.f$0 = secretChatHelper;
        this.f$1 = encryptedChat;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$acceptSecretChat$22$SecretChatHelper(this.f$1, tLObject, tL_error);
    }
}
