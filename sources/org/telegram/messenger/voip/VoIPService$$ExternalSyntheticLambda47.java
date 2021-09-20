package org.telegram.messenger.voip;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda47 implements Runnable {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC$TL_error f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda47(VoIPService voIPService, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, boolean z) {
        this.f$0 = voIPService;
        this.f$1 = tLObject;
        this.f$2 = tLRPC$TL_error;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.lambda$acknowledgeCall$11(this.f$1, this.f$2, this.f$3);
    }
}
