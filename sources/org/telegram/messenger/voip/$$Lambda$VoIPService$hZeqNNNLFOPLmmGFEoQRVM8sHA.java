package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$hZeqNNNLFOPLmmGFEo-QRVM8sHA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$hZeqNNNLFOPLmmGFEoQRVM8sHA implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$hZeqNNNLFOPLmmGFEoQRVM8sHA INSTANCE = new $$Lambda$VoIPService$hZeqNNNLFOPLmmGFEoQRVM8sHA();

    private /* synthetic */ $$Lambda$VoIPService$hZeqNNNLFOPLmmGFEoQRVM8sHA() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$callFailed$78(tLObject, tLRPC$TL_error);
    }
}
