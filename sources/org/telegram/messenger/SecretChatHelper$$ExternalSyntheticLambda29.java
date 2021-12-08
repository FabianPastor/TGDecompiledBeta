package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$DecryptedMessage;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda29 implements RequestDelegate {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ TLRPC$DecryptedMessage f$1;
    public final /* synthetic */ TLRPC$EncryptedChat f$2;
    public final /* synthetic */ TLRPC$Message f$3;
    public final /* synthetic */ MessageObject f$4;
    public final /* synthetic */ String f$5;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda29(SecretChatHelper secretChatHelper, TLRPC$DecryptedMessage tLRPC$DecryptedMessage, TLRPC$EncryptedChat tLRPC$EncryptedChat, TLRPC$Message tLRPC$Message, MessageObject messageObject, String str) {
        this.f$0 = secretChatHelper;
        this.f$1 = tLRPC$DecryptedMessage;
        this.f$2 = tLRPC$EncryptedChat;
        this.f$3 = tLRPC$Message;
        this.f$4 = messageObject;
        this.f$5 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$performSendEncryptedRequest$7(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tLRPC$TL_error);
    }
}
