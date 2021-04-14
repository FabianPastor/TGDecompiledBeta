package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$WMZK72erMIrcdd14_ySFO2ejqYY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$WMZK72erMIrcdd14_ySFO2ejqYY implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$WMZK72erMIrcdd14_ySFO2ejqYY INSTANCE = new $$Lambda$VoIPService$WMZK72erMIrcdd14_ySFO2ejqYY();

    private /* synthetic */ $$Lambda$VoIPService$WMZK72erMIrcdd14_ySFO2ejqYY() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onTgVoipStop$4(tLObject, tLRPC$TL_error);
    }
}
