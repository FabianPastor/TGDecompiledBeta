package org.telegram.messenger;

import org.telegram.messenger.MediaController.AnonymousClass4;

final /* synthetic */ class MediaController$4$$Lambda$0 implements Runnable {
    private final AnonymousClass4 arg$1;
    private final MessageObject arg$2;

    MediaController$4$$Lambda$0(AnonymousClass4 anonymousClass4, MessageObject messageObject) {
        this.arg$1 = anonymousClass4;
        this.arg$2 = messageObject;
    }

    public void run() {
        this.arg$1.lambda$run$0$MediaController$4(this.arg$2);
    }
}
