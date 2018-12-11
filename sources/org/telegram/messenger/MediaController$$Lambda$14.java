package org.telegram.messenger;

import java.io.File;
import org.telegram.tgnet.TLRPC.TL_document;

final /* synthetic */ class MediaController$$Lambda$14 implements Runnable {
    private final MediaController arg$1;
    private final TL_document arg$2;
    private final File arg$3;
    private final int arg$4;

    MediaController$$Lambda$14(MediaController mediaController, TL_document tL_document, File file, int i) {
        this.arg$1 = mediaController;
        this.arg$2 = tL_document;
        this.arg$3 = file;
        this.arg$4 = i;
    }

    public void run() {
        this.arg$1.lambda$stopRecordingInternal$20$MediaController(this.arg$2, this.arg$3, this.arg$4);
    }
}
