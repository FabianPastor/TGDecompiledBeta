package org.telegram.messenger;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda122 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda122(MediaDataController mediaDataController, boolean z, long j) {
        this.f$0 = mediaDataController;
        this.f$1 = z;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$markFeaturedStickersByIdAsRead$53(this.f$1, this.f$2);
    }
}
