package org.telegram.messenger.voip;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda48 implements Runnable {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda48(VoIPService voIPService, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = voIPService;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m2486xda5CLASSNAME(this.f$1, this.f$2);
    }
}
