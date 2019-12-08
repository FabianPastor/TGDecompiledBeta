package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.photos_Photos;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$y-eXdDdnoHYRbr9uIJJn0XF7oIQ implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ photos_Photos f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ long f$4;
    private final /* synthetic */ int f$5;

    public /* synthetic */ -$$Lambda$MessagesStorage$y-eXdDdnoHYRbr9uIJJn0XF7oIQ(MessagesStorage messagesStorage, photos_Photos photos_photos, int i, int i2, long j, int i3) {
        this.f$0 = messagesStorage;
        this.f$1 = photos_photos;
        this.f$2 = i;
        this.f$3 = i2;
        this.f$4 = j;
        this.f$5 = i3;
    }

    public final void run() {
        this.f$0.lambda$null$41$MessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
