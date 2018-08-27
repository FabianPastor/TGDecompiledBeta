package org.telegram.messenger;

final /* synthetic */ class ImageLoader$$Lambda$1 implements Runnable {
    private final ImageLoader arg$1;
    private final int arg$2;
    private final ImageReceiver arg$3;

    ImageLoader$$Lambda$1(ImageLoader imageLoader, int i, ImageReceiver imageReceiver) {
        this.arg$1 = imageLoader;
        this.arg$2 = i;
        this.arg$3 = imageReceiver;
    }

    public void run() {
        this.arg$1.lambda$cancelLoadingForImageReceiver$2$ImageLoader(this.arg$2, this.arg$3);
    }
}
