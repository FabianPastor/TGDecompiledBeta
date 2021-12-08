package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegateTimestamp;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda95 implements RequestDelegateTimestamp {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ int f$5;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda95(VoIPService voIPService, String str, int i, long j, int i2, int i3) {
        this.f$0 = voIPService;
        this.f$1 = str;
        this.f$2 = i;
        this.f$3 = j;
        this.f$4 = i2;
        this.f$5 = i3;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error, long j) {
        this.f$0.m1215x614b3069(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tL_error, j);
    }
}
