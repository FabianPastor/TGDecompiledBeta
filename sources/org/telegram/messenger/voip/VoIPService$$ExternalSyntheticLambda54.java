package org.telegram.messenger.voip;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda54 implements Runnable {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ byte[] f$3;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda54(VoIPService voIPService, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, byte[] bArr) {
        this.f$0 = voIPService;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
        this.f$3 = bArr;
    }

    public final void run() {
        this.f$0.lambda$startOutgoingCall$8(this.f$1, this.f$2, this.f$3);
    }
}
