package org.telegram.messenger;

final /* synthetic */ class ImageLoader$HttpImageTask$$Lambda$0 implements Runnable {
    private final HttpImageTask arg$1;
    private final float arg$2;

    ImageLoader$HttpImageTask$$Lambda$0(HttpImageTask httpImageTask, float f) {
        this.arg$1 = httpImageTask;
        this.arg$2 = f;
    }

    public void run() {
        this.arg$1.lambda$reportProgress$1$ImageLoader$HttpImageTask(this.arg$2);
    }
}
