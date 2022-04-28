package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda94 implements RequestDelegate {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda94(VoIPService voIPService, boolean z) {
        this.f$0 = voIPService;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$acknowledgeCall$12(this.f$1, tLObject, tLRPC$TL_error);
    }
}
