package org.telegram.messenger;

final /* synthetic */ class MediaController$$Lambda$11 implements Runnable {
    private final MediaController arg$1;
    private final int arg$2;
    private final long arg$3;
    private final MessageObject arg$4;

    MediaController$$Lambda$11(MediaController mediaController, int i, long j, MessageObject messageObject) {
        this.arg$1 = mediaController;
        this.arg$2 = i;
        this.arg$3 = j;
        this.arg$4 = messageObject;
    }

    public void run() {
        this.arg$1.lambda$startRecording$15$MediaController(this.arg$2, this.arg$3, this.arg$4);
    }
}
