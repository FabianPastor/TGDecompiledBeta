package org.telegram.messenger;

import org.telegram.messenger.MediaController.CLASSNAME;

final /* synthetic */ class MediaController$2$$Lambda$0 implements Runnable {
    private final CLASSNAME arg$1;
    private final int arg$2;

    MediaController$2$$Lambda$0(CLASSNAME CLASSNAME, int i) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = i;
    }

    public void run() {
        this.arg$1.lambda$onCallStateChanged$0$MediaController$2(this.arg$2);
    }
}
