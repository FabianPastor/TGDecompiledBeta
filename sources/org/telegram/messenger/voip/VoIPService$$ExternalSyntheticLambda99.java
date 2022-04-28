package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegateTimestamp;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda99 implements RequestDelegateTimestamp {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda99(VoIPService voIPService, int i, long j) {
        this.f$0 = voIPService;
        this.f$1 = i;
        this.f$2 = j;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, long j) {
        this.f$0.lambda$createGroupInstance$48(this.f$1, this.f$2, tLObject, tLRPC$TL_error, j);
    }
}
