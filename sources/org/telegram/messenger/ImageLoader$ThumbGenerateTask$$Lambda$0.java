package org.telegram.messenger;

final /* synthetic */ class ImageLoader$ThumbGenerateTask$$Lambda$0 implements Runnable {
    private final ThumbGenerateTask arg$1;
    private final String arg$2;

    ImageLoader$ThumbGenerateTask$$Lambda$0(ThumbGenerateTask thumbGenerateTask, String str) {
        this.arg$1 = thumbGenerateTask;
        this.arg$2 = str;
    }

    public void run() {
        this.arg$1.lambda$removeTask$0$ImageLoader$ThumbGenerateTask(this.arg$2);
    }
}
