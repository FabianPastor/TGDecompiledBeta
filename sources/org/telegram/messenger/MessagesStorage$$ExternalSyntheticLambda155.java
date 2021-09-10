package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Document;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda155 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$Document f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ String f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda155(MessagesStorage messagesStorage, TLRPC$Document tLRPC$Document, String str, String str2) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$Document;
        this.f$2 = str;
        this.f$3 = str2;
    }

    public final void run() {
        this.f$0.lambda$addRecentLocalFile$52(this.f$1, this.f$2, this.f$3);
    }
}
