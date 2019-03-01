package org.telegram.messenger;

import java.nio.ByteBuffer;
import org.telegram.messenger.MediaController.AnonymousClass2;

final /* synthetic */ class MediaController$2$$Lambda$2 implements Runnable {
    private final AnonymousClass2 arg$1;
    private final ByteBuffer arg$2;

    MediaController$2$$Lambda$2(AnonymousClass2 anonymousClass2, ByteBuffer byteBuffer) {
        this.arg$1 = anonymousClass2;
        this.arg$2 = byteBuffer;
    }

    public void run() {
        this.arg$1.lambda$null$0$MediaController$2(this.arg$2);
    }
}
