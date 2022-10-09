package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes.dex */
public final /* synthetic */ class DownloadController$$ExternalSyntheticLambda13 implements RequestDelegate {
    public static final /* synthetic */ DownloadController$$ExternalSyntheticLambda13 INSTANCE = new DownloadController$$ExternalSyntheticLambda13();

    private /* synthetic */ DownloadController$$ExternalSyntheticLambda13() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        DownloadController.lambda$savePresetToServer$3(tLObject, tLRPC$TL_error);
    }
}
