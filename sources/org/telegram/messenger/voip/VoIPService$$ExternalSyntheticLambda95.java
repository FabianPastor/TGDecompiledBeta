package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda95 implements RequestDelegate {
    public static final /* synthetic */ VoIPService$$ExternalSyntheticLambda95 INSTANCE = new VoIPService$$ExternalSyntheticLambda95();

    private /* synthetic */ VoIPService$$ExternalSyntheticLambda95() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$createGroupInstance$37(tLObject, tLRPC$TL_error);
    }
}
