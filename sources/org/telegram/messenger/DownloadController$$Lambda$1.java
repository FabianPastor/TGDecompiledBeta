package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class DownloadController$$Lambda$1 implements RequestDelegate {
    private final DownloadController arg$1;

    DownloadController$$Lambda$1(DownloadController downloadController) {
        this.arg$1 = downloadController;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadAutoDownloadConfig$2$DownloadController(tLObject, tL_error);
    }
}
