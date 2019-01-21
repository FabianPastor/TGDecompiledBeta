package org.telegram.messenger;

import java.nio.ByteBuffer;
import org.telegram.messenger.MediaController.AnonymousClass1;

final /* synthetic */ class MediaController$1$$Lambda$2 implements Runnable {
    private final AnonymousClass1 arg$1;
    private final ByteBuffer arg$2;

    MediaController$1$$Lambda$2(AnonymousClass1 anonymousClass1, ByteBuffer byteBuffer) {
        this.arg$1 = anonymousClass1;
        this.arg$2 = byteBuffer;
    }

    public void run() {
        this.arg$1.lambda$null$0$MediaController$1(this.arg$2);
    }
}
