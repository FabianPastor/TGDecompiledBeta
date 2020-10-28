package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$DownloadController$X8Qr0DXt6tSy5F0hlLtJCLASSNAMEVYJ0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$DownloadController$X8Qr0DXt6tSy5F0hlLtJCLASSNAMEVYJ0 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$DownloadController$X8Qr0DXt6tSy5F0hlLtJCLASSNAMEVYJ0 INSTANCE = new $$Lambda$DownloadController$X8Qr0DXt6tSy5F0hlLtJCLASSNAMEVYJ0();

    private /* synthetic */ $$Lambda$DownloadController$X8Qr0DXt6tSy5F0hlLtJCLASSNAMEVYJ0() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        DownloadController.lambda$savePresetToServer$3(tLObject, tLRPC$TL_error);
    }
}
