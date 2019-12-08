package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.photos_Photos;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$dmBaoEBVUXAfkurWt4XN1ZU7znw implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ photos_Photos f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$dmBaoEBVUXAfkurWt4XN1ZU7znw(MessagesStorage messagesStorage, int i, photos_Photos photos_photos) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = photos_photos;
    }

    public final void run() {
        this.f$0.lambda$putDialogPhotos$50$MessagesStorage(this.f$1, this.f$2);
    }
}
