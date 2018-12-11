package org.telegram.messenger;

import java.nio.ByteBuffer;
import org.telegram.messenger.MediaController.CLASSNAME;

final /* synthetic */ class MediaController$1$$Lambda$2 implements Runnable {
    private final CLASSNAME arg$1;
    private final ByteBuffer arg$2;

    MediaController$1$$Lambda$2(CLASSNAME CLASSNAME, ByteBuffer byteBuffer) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = byteBuffer;
    }

    public void run() {
        this.arg$1.lambda$null$0$MediaController$1(this.arg$2);
    }
}
