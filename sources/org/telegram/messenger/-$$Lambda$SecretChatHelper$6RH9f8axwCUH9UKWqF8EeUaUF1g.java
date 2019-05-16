package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SecretChatHelper$6RH9f8axwCUH9UKWqF8EeUaUF1g implements Runnable {
    private final /* synthetic */ SecretChatHelper f$0;
    private final /* synthetic */ Message f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ String f$3;

    public /* synthetic */ -$$Lambda$SecretChatHelper$6RH9f8axwCUH9UKWqF8EeUaUF1g(SecretChatHelper secretChatHelper, Message message, int i, String str) {
        this.f$0 = secretChatHelper;
        this.f$1 = message;
        this.f$2 = i;
        this.f$3 = str;
    }

    public final void run() {
        this.f$0.lambda$null$3$SecretChatHelper(this.f$1, this.f$2, this.f$3);
    }
}
