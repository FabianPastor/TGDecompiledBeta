package org.telegram.messenger;

final /* synthetic */ class ImageLoader$$Lambda$7 implements Runnable {
    private final ImageLoader arg$1;
    private final String arg$2;

    ImageLoader$$Lambda$7(ImageLoader imageLoader, String str) {
        this.arg$1 = imageLoader;
        this.arg$2 = str;
    }

    public void run() {
        this.arg$1.lambda$fileDidFailedLoad$8$ImageLoader(this.arg$2);
    }
}
