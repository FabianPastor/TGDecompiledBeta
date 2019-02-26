package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class DownloadController$$Lambda$3 implements Runnable {
    private final DownloadController arg$1;
    private final TLObject arg$2;

    DownloadController$$Lambda$3(DownloadController downloadController, TLObject tLObject) {
        this.arg$1 = downloadController;
        this.arg$2 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$1$DownloadController(this.arg$2);
    }
}
