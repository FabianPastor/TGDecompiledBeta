package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class DownloadController$$ExternalSyntheticLambda11 implements RequestDelegate {
    public static final /* synthetic */ DownloadController$$ExternalSyntheticLambda11 INSTANCE = new DownloadController$$ExternalSyntheticLambda11();

    private /* synthetic */ DownloadController$$ExternalSyntheticLambda11() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        DownloadController.lambda$savePresetToServer$3(tLObject, tLRPC$TL_error);
    }
}
