package org.telegram.messenger;

import java.io.File;

final /* synthetic */ class MediaController$$Lambda$20 implements Runnable {
    private final MediaController arg$1;
    private final boolean arg$2;
    private final boolean arg$3;
    private final MessageObject arg$4;
    private final File arg$5;
    private final boolean arg$6;
    private final long arg$7;

    MediaController$$Lambda$20(MediaController mediaController, boolean z, boolean z2, MessageObject messageObject, File file, boolean z3, long j) {
        this.arg$1 = mediaController;
        this.arg$2 = z;
        this.arg$3 = z2;
        this.arg$4 = messageObject;
        this.arg$5 = file;
        this.arg$6 = z3;
        this.arg$7 = j;
    }

    public void run() {
        this.arg$1.lambda$didWriteData$30$MediaController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7);
    }
}
