package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda96 implements RequestDelegate {
    public static final /* synthetic */ VoIPService$$ExternalSyntheticLambda96 INSTANCE = new VoIPService$$ExternalSyntheticLambda96();

    private /* synthetic */ VoIPService$$ExternalSyntheticLambda96() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onTgVoipStop$75(tLObject, tLRPC$TL_error);
    }
}
