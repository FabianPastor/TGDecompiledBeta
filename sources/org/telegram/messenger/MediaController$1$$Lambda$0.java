package org.telegram.messenger;

import java.nio.ByteBuffer;
import org.telegram.messenger.MediaController.C02481;

final /* synthetic */ class MediaController$1$$Lambda$0 implements Runnable {
    private final C02481 arg$1;
    private final ByteBuffer arg$2;
    private final boolean arg$3;

    MediaController$1$$Lambda$0(C02481 c02481, ByteBuffer byteBuffer, boolean z) {
        this.arg$1 = c02481;
        this.arg$2 = byteBuffer;
        this.arg$3 = z;
    }

    public void run() {
        this.arg$1.lambda$run$1$MediaController$1(this.arg$2, this.arg$3);
    }
}
