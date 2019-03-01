package org.telegram.messenger;

import java.nio.ByteBuffer;
import org.telegram.messenger.MediaController.AnonymousClass2;

final /* synthetic */ class MediaController$2$$Lambda$0 implements Runnable {
    private final AnonymousClass2 arg$1;
    private final ByteBuffer arg$2;
    private final boolean arg$3;

    MediaController$2$$Lambda$0(AnonymousClass2 anonymousClass2, ByteBuffer byteBuffer, boolean z) {
        this.arg$1 = anonymousClass2;
        this.arg$2 = byteBuffer;
        this.arg$3 = z;
    }

    public void run() {
        this.arg$1.lambda$run$1$MediaController$2(this.arg$2, this.arg$3);
    }
}
