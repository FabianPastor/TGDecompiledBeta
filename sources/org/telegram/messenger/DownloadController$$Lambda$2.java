package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class DownloadController$$Lambda$2 implements RequestDelegate {
    static final RequestDelegate $instance = new DownloadController$$Lambda$2();

    private DownloadController$$Lambda$2() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        DownloadController.lambda$savePresetToServer$3$DownloadController(tLObject, tL_error);
    }
}
