package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda21 implements RequestDelegate {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ TLRPC.DecryptedMessage f$1;
    public final /* synthetic */ TLRPC.EncryptedChat f$2;
    public final /* synthetic */ TLRPC.Message f$3;
    public final /* synthetic */ MessageObject f$4;
    public final /* synthetic */ String f$5;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda21(SecretChatHelper secretChatHelper, TLRPC.DecryptedMessage decryptedMessage, TLRPC.EncryptedChat encryptedChat, TLRPC.Message message, MessageObject messageObject, String str) {
        this.f$0 = secretChatHelper;
        this.f$1 = decryptedMessage;
        this.f$2 = encryptedChat;
        this.f$3 = message;
        this.f$4 = messageObject;
        this.f$5 = str;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1137xvar_ed719(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tL_error);
    }
}
