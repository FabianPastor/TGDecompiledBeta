package org.telegram.messenger;

import org.telegram.messenger.MediaController.AnonymousClass3;

final /* synthetic */ class MediaController$3$$Lambda$0 implements Runnable {
    private final AnonymousClass3 arg$1;
    private final MessageObject arg$2;

    MediaController$3$$Lambda$0(AnonymousClass3 anonymousClass3, MessageObject messageObject) {
        this.arg$1 = anonymousClass3;
        this.arg$2 = messageObject;
    }

    public void run() {
        this.arg$1.lambda$run$0$MediaController$3(this.arg$2);
    }
}
