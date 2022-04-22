package org.telegram.messenger;

import org.telegram.messenger.ImageLoader;

public final /* synthetic */ class ImageLoader$5$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ ImageLoader.AnonymousClass5 f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ ImageLoader$5$$ExternalSyntheticLambda5(ImageLoader.AnonymousClass5 r1, int i, String str, boolean z) {
        this.f$0 = r1;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.lambda$fileDidFailedUpload$4(this.f$1, this.f$2, this.f$3);
    }
}
