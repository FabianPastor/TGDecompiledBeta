package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda87 implements RequestDelegate {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ TLRPC.TL_phone_checkGroupCall f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda87(VoIPService voIPService, TLRPC.TL_phone_checkGroupCall tL_phone_checkGroupCall) {
        this.f$0 = voIPService;
        this.f$1 = tL_phone_checkGroupCall;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1260x748fd659(this.f$1, tLObject, tL_error);
    }
}
