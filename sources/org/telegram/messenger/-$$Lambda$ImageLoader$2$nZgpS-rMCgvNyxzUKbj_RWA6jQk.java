package org.telegram.messenger;

import org.telegram.messenger.ImageLoader.AnonymousClass2;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ImageLoader$2$nZgpS-rMCgvNyxzUKbj_RWA6jQk implements Runnable {
    private final /* synthetic */ AnonymousClass2 f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ boolean f$3;

    public /* synthetic */ -$$Lambda$ImageLoader$2$nZgpS-rMCgvNyxzUKbj_RWA6jQk(AnonymousClass2 anonymousClass2, int i, String str, boolean z) {
        this.f$0 = anonymousClass2;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.lambda$fileDidFailedUpload$4$ImageLoader$2(this.f$1, this.f$2, this.f$3);
    }
}
