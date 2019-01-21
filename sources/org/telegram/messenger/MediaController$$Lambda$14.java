package org.telegram.messenger;

final /* synthetic */ class MediaController$$Lambda$14 implements Runnable {
    private final MediaController arg$1;
    private final int arg$2;

    MediaController$$Lambda$14(MediaController mediaController, int i) {
        this.arg$1 = mediaController;
        this.arg$2 = i;
    }

    public void run() {
        this.arg$1.lambda$stopRecording$21$MediaController(this.arg$2);
    }
}
