package org.telegram.messenger;

final /* synthetic */ class ImageLoader$$Lambda$8 implements Runnable {
    private final ImageLoader arg$1;
    private final HttpFileTask arg$2;
    private final int arg$3;

    ImageLoader$$Lambda$8(ImageLoader imageLoader, HttpFileTask httpFileTask, int i) {
        this.arg$1 = imageLoader;
        this.arg$2 = httpFileTask;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$runHttpFileLoadTasks$10$ImageLoader(this.arg$2, this.arg$3);
    }
}
