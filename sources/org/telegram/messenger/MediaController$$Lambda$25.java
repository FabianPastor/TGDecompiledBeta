package org.telegram.messenger;

final /* synthetic */ class MediaController$$Lambda$25 implements Runnable {
    private final MediaController arg$1;
    private final String arg$2;
    private final byte[] arg$3;

    MediaController$$Lambda$25(MediaController mediaController, String str, byte[] bArr) {
        this.arg$1 = mediaController;
        this.arg$2 = str;
        this.arg$3 = bArr;
    }

    public void run() {
        this.arg$1.lambda$null$16$MediaController(this.arg$2, this.arg$3);
    }
}
