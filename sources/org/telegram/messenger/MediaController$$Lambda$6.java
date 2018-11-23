package org.telegram.messenger;

final /* synthetic */ class MediaController$$Lambda$6 implements Runnable {
    private final MediaController arg$1;
    private final MessageObject arg$2;

    MediaController$$Lambda$6(MediaController mediaController, MessageObject messageObject) {
        this.arg$1 = mediaController;
        this.arg$2 = messageObject;
    }

    public void run() {
        this.arg$1.lambda$startAudioAgain$6$MediaController(this.arg$2);
    }
}
