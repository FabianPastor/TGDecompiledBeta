package org.telegram.messenger;

import org.telegram.messenger.ImageLoader.C13482;

final /* synthetic */ class ImageLoader$2$$Lambda$2 implements Runnable {
    private final C13482 arg$1;
    private final int arg$2;
    private final String arg$3;
    private final boolean arg$4;

    ImageLoader$2$$Lambda$2(C13482 c13482, int i, String str, boolean z) {
        this.arg$1 = c13482;
        this.arg$2 = i;
        this.arg$3 = str;
        this.arg$4 = z;
    }

    public void run() {
        this.arg$1.lambda$fileDidFailedUpload$4$ImageLoader$2(this.arg$2, this.arg$3, this.arg$4);
    }
}
