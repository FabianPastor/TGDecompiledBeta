package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda93 implements RequestDelegate {
    public static final /* synthetic */ VoIPService$$ExternalSyntheticLambda93 INSTANCE = new VoIPService$$ExternalSyntheticLambda93();

    private /* synthetic */ VoIPService$$ExternalSyntheticLambda93() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onSignalingData$58(tLObject, tLRPC$TL_error);
    }
}
