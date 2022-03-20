package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class DownloadController$$ExternalSyntheticLambda10 implements RequestDelegate {
    public final /* synthetic */ DownloadController f$0;

    public /* synthetic */ DownloadController$$ExternalSyntheticLambda10(DownloadController downloadController) {
        this.f$0 = downloadController;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadAutoDownloadConfig$2(tLObject, tLRPC$TL_error);
    }
}
