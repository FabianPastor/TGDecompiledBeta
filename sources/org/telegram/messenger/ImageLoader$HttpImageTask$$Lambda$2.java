package org.telegram.messenger;

final /* synthetic */ class ImageLoader$HttpImageTask$$Lambda$2 implements Runnable {
    private final HttpImageTask arg$1;
    private final Boolean arg$2;

    ImageLoader$HttpImageTask$$Lambda$2(HttpImageTask httpImageTask, Boolean bool) {
        this.arg$1 = httpImageTask;
        this.arg$2 = bool;
    }

    public void run() {
        this.arg$1.lambda$onPostExecute$4$ImageLoader$HttpImageTask(this.arg$2);
    }
}
