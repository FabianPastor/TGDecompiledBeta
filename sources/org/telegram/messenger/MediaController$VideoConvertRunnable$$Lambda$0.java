package org.telegram.messenger;

final /* synthetic */ class MediaController$VideoConvertRunnable$$Lambda$0 implements Runnable {
    private final MessageObject arg$1;

    MediaController$VideoConvertRunnable$$Lambda$0(MessageObject messageObject) {
        this.arg$1 = messageObject;
    }

    public void run() {
        VideoConvertRunnable.lambda$runConversion$0$MediaController$VideoConvertRunnable(this.arg$1);
    }
}
