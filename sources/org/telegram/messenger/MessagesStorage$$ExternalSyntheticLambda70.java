package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda70 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC.Document f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ String f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda70(MessagesStorage messagesStorage, TLRPC.Document document, String str, String str2) {
        this.f$0 = messagesStorage;
        this.f$1 = document;
        this.f$2 = str;
        this.f$3 = str2;
    }

    public final void run() {
        this.f$0.m2127xafb5f8b9(this.f$1, this.f$2, this.f$3);
    }
}
