package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class DownloadController$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ DownloadController f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ DownloadController$$ExternalSyntheticLambda9(DownloadController downloadController, TLObject tLObject) {
        this.f$0 = downloadController;
        this.f$1 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$loadAutoDownloadConfig$1(this.f$1);
    }
}
