package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.photos_Photos;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$BmQ4KzU527mjcBaLf7ITSJtYsl4 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ photos_Photos f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ int f$5;

    public /* synthetic */ -$$Lambda$MessagesController$BmQ4KzU527mjcBaLf7ITSJtYsl4(MessagesController messagesController, photos_Photos photos_photos, boolean z, int i, int i2, int i3) {
        this.f$0 = messagesController;
        this.f$1 = photos_photos;
        this.f$2 = z;
        this.f$3 = i;
        this.f$4 = i2;
        this.f$5 = i3;
    }

    public final void run() {
        this.f$0.lambda$processLoadedUserPhotos$69$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
