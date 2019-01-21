package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class ImageLoader$$Lambda$2 implements Runnable {
    private final ImageLoader arg$1;
    private final String arg$2;
    private final String arg$3;
    private final TLObject arg$4;

    ImageLoader$$Lambda$2(ImageLoader imageLoader, String str, String str2, TLObject tLObject) {
        this.arg$1 = imageLoader;
        this.arg$2 = str;
        this.arg$3 = str2;
        this.arg$4 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$replaceImageInCache$3$ImageLoader(this.arg$2, this.arg$3, this.arg$4);
    }
}
