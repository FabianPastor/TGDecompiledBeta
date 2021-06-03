package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$Uw3-pAoUc3gVKRnwCdggFRxpK7o  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$Uw3pAoUc3gVKRnwCdggFRxpK7o implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$Uw3pAoUc3gVKRnwCdggFRxpK7o INSTANCE = new $$Lambda$VoIPService$Uw3pAoUc3gVKRnwCdggFRxpK7o();

    private /* synthetic */ $$Lambda$VoIPService$Uw3pAoUc3gVKRnwCdggFRxpK7o() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$callFailed$72(tLObject, tLRPC$TL_error);
    }
}
