package org.telegram.messenger;

final /* synthetic */ class MediaController$$Lambda$11 implements Runnable {
    private final MessageObject arg$1;

    MediaController$$Lambda$11(MessageObject messageObject) {
        this.arg$1 = messageObject;
    }

    public void run() {
        NotificationCenter.getInstance(this.arg$1.currentAccount).postNotificationName(NotificationCenter.fileDidLoad, FileLoader.getAttachFileName(this.arg$1.getDocument()));
    }
}
