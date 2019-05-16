package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.DecryptedMessage;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SecretChatHelper$NeIJyTvVk2g1G3EFM6ENqFtjkw0 implements RequestDelegate {
    private final /* synthetic */ SecretChatHelper f$0;
    private final /* synthetic */ DecryptedMessage f$1;
    private final /* synthetic */ EncryptedChat f$2;
    private final /* synthetic */ Message f$3;
    private final /* synthetic */ MessageObject f$4;
    private final /* synthetic */ String f$5;

    public /* synthetic */ -$$Lambda$SecretChatHelper$NeIJyTvVk2g1G3EFM6ENqFtjkw0(SecretChatHelper secretChatHelper, DecryptedMessage decryptedMessage, EncryptedChat encryptedChat, Message message, MessageObject messageObject, String str) {
        this.f$0 = secretChatHelper;
        this.f$1 = decryptedMessage;
        this.f$2 = encryptedChat;
        this.f$3 = message;
        this.f$4 = messageObject;
        this.f$5 = str;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$6$SecretChatHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tL_error);
    }
}
