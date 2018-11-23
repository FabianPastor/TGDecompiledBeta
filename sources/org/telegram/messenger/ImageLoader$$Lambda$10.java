package org.telegram.messenger;

final /* synthetic */ class ImageLoader$$Lambda$10 implements Runnable {
    private final ImageLoader arg$1;
    private final HttpFileTask arg$2;

    ImageLoader$$Lambda$10(ImageLoader imageLoader, HttpFileTask httpFileTask) {
        this.arg$1 = imageLoader;
        this.arg$2 = httpFileTask;
    }

    public void run() {
        this.arg$1.lambda$null$10$ImageLoader(this.arg$2);
    }
}
