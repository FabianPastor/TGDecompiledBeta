package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.FileLocation;

final /* synthetic */ class ImageLoader$$Lambda$2 implements Runnable {
    private final ImageLoader arg$1;
    private final String arg$2;
    private final String arg$3;
    private final FileLocation arg$4;

    ImageLoader$$Lambda$2(ImageLoader imageLoader, String str, String str2, FileLocation fileLocation) {
        this.arg$1 = imageLoader;
        this.arg$2 = str;
        this.arg$3 = str2;
        this.arg$4 = fileLocation;
    }

    public void run() {
        this.arg$1.lambda$replaceImageInCache$3$ImageLoader(this.arg$2, this.arg$3, this.arg$4);
    }
}
