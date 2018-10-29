package org.telegram.messenger;

final /* synthetic */ class ImageLoader$HttpFileTask$$Lambda$1 implements Runnable {
    private final HttpFileTask arg$1;
    private final float arg$2;

    ImageLoader$HttpFileTask$$Lambda$1(HttpFileTask httpFileTask, float f) {
        this.arg$1 = httpFileTask;
        this.arg$2 = f;
    }

    public void run() {
        this.arg$1.lambda$null$0$ImageLoader$HttpFileTask(this.arg$2);
    }
}
