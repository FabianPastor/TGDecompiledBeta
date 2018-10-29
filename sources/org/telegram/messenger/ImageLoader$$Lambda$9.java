package org.telegram.messenger;

final /* synthetic */ class ImageLoader$$Lambda$9 implements Runnable {
    private final ImageLoader arg$1;
    private final HttpFileTask arg$2;

    ImageLoader$$Lambda$9(ImageLoader imageLoader, HttpFileTask httpFileTask) {
        this.arg$1 = imageLoader;
        this.arg$2 = httpFileTask;
    }

    public void run() {
        this.arg$1.lambda$null$9$ImageLoader(this.arg$2);
    }
}
