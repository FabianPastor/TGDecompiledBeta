package org.telegram.messenger;

final /* synthetic */ class ImageLoader$$Lambda$5 implements Runnable {
    private final ImageLoader arg$1;
    private final String arg$2;

    ImageLoader$$Lambda$5(ImageLoader imageLoader, String str) {
        this.arg$1 = imageLoader;
        this.arg$2 = str;
    }

    public void run() {
        this.arg$1.lambda$httpFileLoadError$6$ImageLoader(this.arg$2);
    }
}
