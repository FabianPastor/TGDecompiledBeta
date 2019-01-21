package org.telegram.messenger;

final /* synthetic */ class MediaController$$Lambda$12 implements Runnable {
    private final MediaController arg$1;
    private final String arg$2;
    private final String arg$3;

    MediaController$$Lambda$12(MediaController mediaController, String str, String str2) {
        this.arg$1 = mediaController;
        this.arg$2 = str;
        this.arg$3 = str2;
    }

    public void run() {
        this.arg$1.lambda$generateWaveform$17$MediaController(this.arg$2, this.arg$3);
    }
}
