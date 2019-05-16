package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.DecryptedMessage;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SecretChatHelper$go4ClJO8kMeuzFvRQpPn8-EmO40 implements Runnable {
    private final /* synthetic */ SecretChatHelper f$0;
    private final /* synthetic */ EncryptedChat f$1;
    private final /* synthetic */ DecryptedMessage f$2;
    private final /* synthetic */ Message f$3;
    private final /* synthetic */ InputEncryptedFile f$4;
    private final /* synthetic */ MessageObject f$5;
    private final /* synthetic */ String f$6;

    public /* synthetic */ -$$Lambda$SecretChatHelper$go4ClJO8kMeuzFvRQpPn8-EmO40(SecretChatHelper secretChatHelper, EncryptedChat encryptedChat, DecryptedMessage decryptedMessage, Message message, InputEncryptedFile inputEncryptedFile, MessageObject messageObject, String str) {
        this.f$0 = secretChatHelper;
        this.f$1 = encryptedChat;
        this.f$2 = decryptedMessage;
        this.f$3 = message;
        this.f$4 = inputEncryptedFile;
        this.f$5 = messageObject;
        this.f$6 = str;
    }

    public final void run() {
        this.f$0.lambda$performSendEncryptedRequest$7$SecretChatHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
