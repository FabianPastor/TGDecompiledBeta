package org.telegram.messenger;

import org.telegram.messenger.MediaController.CLASSNAME;

final /* synthetic */ class MediaController$3$$Lambda$0 implements Runnable {
    private final CLASSNAME arg$1;
    private final MessageObject arg$2;

    MediaController$3$$Lambda$0(CLASSNAME CLASSNAME, MessageObject messageObject) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = messageObject;
    }

    public void run() {
        this.arg$1.lambda$run$0$MediaController$3(this.arg$2);
    }
}
