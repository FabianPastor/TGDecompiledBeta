package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda91 implements RequestDelegate {
    public static final /* synthetic */ VoIPService$$ExternalSyntheticLambda91 INSTANCE = new VoIPService$$ExternalSyntheticLambda91();

    private /* synthetic */ VoIPService$$ExternalSyntheticLambda91() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        VoIPService.lambda$callFailed$78(tLObject, tL_error);
    }
}
