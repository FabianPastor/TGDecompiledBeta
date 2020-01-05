package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$PYdreL1lZbKyAVmwgtRlBdpecdo implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ Document f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ String f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$PYdreL1lZbKyAVmwgtRlBdpecdo(MessagesStorage messagesStorage, Document document, String str, String str2) {
        this.f$0 = messagesStorage;
        this.f$1 = document;
        this.f$2 = str;
        this.f$3 = str2;
    }

    public final void run() {
        this.f$0.lambda$addRecentLocalFile$34$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}
