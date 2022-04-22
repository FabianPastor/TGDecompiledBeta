package org.telegram.messenger.voip;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda52 implements Runnable {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda52(VoIPService voIPService, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        this.f$0 = voIPService;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$acceptIncomingCall$68(this.f$1, this.f$2);
    }
}
