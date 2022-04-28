package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda97 implements RequestDelegate {
    public static final /* synthetic */ VoIPService$$ExternalSyntheticLambda97 INSTANCE = new VoIPService$$ExternalSyntheticLambda97();

    private /* synthetic */ VoIPService$$ExternalSyntheticLambda97() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onSignalingData$60(tLObject, tLRPC$TL_error);
    }
}
