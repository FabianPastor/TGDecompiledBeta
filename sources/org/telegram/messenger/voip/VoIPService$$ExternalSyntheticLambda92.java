package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda92 implements RequestDelegate {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ byte[] f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda92(VoIPService voIPService, byte[] bArr) {
        this.f$0 = voIPService;
        this.f$1 = bArr;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1219xad14a785(this.f$1, tLObject, tL_error);
    }
}
