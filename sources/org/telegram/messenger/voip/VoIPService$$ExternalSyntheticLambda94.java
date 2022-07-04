package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda94 implements RequestDelegate {
    public static final /* synthetic */ VoIPService$$ExternalSyntheticLambda94 INSTANCE = new VoIPService$$ExternalSyntheticLambda94();

    private /* synthetic */ VoIPService$$ExternalSyntheticLambda94() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        VoIPService.lambda$createGroupInstance$37(tLObject, tL_error);
    }
}
