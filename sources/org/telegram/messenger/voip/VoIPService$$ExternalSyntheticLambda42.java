package org.telegram.messenger.voip;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda42 implements Runnable {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC.TL_phone_checkGroupCall f$2;
    public final /* synthetic */ TLRPC.TL_error f$3;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda42(VoIPService voIPService, TLObject tLObject, TLRPC.TL_phone_checkGroupCall tL_phone_checkGroupCall, TLRPC.TL_error tL_error) {
        this.f$0 = voIPService;
        this.f$1 = tLObject;
        this.f$2 = tL_phone_checkGroupCall;
        this.f$3 = tL_error;
    }

    public final void run() {
        this.f$0.m1259xe7efab58(this.f$1, this.f$2, this.f$3);
    }
}
