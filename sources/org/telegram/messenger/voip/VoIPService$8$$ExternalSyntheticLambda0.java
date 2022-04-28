package org.telegram.messenger.voip;

import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class VoIPService$8$$ExternalSyntheticLambda0 implements RequestDelegate {
    public static final /* synthetic */ VoIPService$8$$ExternalSyntheticLambda0 INSTANCE = new VoIPService$8$$ExternalSyntheticLambda0();

    private /* synthetic */ VoIPService$8$$ExternalSyntheticLambda0() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.AnonymousClass8.lambda$didReceivedNotification$0(tLObject, tLRPC$TL_error);
    }
}
