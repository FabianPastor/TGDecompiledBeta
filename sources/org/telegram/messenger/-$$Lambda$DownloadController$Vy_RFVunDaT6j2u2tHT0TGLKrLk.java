package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DownloadController$Vy_RFVunDaT6j2u2tHT0TGLKrLk implements RequestDelegate {
    private final /* synthetic */ DownloadController f$0;

    public /* synthetic */ -$$Lambda$DownloadController$Vy_RFVunDaT6j2u2tHT0TGLKrLk(DownloadController downloadController) {
        this.f$0 = downloadController;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadAutoDownloadConfig$2$DownloadController(tLObject, tL_error);
    }
}
