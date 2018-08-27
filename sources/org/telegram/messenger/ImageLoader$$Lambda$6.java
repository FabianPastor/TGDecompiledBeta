package org.telegram.messenger;

import java.io.File;

final /* synthetic */ class ImageLoader$$Lambda$6 implements Runnable {
    private final ImageLoader arg$1;
    private final String arg$2;
    private final int arg$3;
    private final File arg$4;

    ImageLoader$$Lambda$6(ImageLoader imageLoader, String str, int i, File file) {
        this.arg$1 = imageLoader;
        this.arg$2 = str;
        this.arg$3 = i;
        this.arg$4 = file;
    }

    public void run() {
        this.arg$1.lambda$fileDidLoaded$7$ImageLoader(this.arg$2, this.arg$3, this.arg$4);
    }
}
