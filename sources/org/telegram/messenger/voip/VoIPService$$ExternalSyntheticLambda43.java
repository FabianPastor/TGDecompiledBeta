package org.telegram.messenger.voip;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda43 implements Runnable {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda43(VoIPService voIPService, TLRPC.TL_error tL_error) {
        this.f$0 = voIPService;
        this.f$1 = tL_error;
    }

    public final void run() {
        this.f$0.m1252lambda$startGroupCall$21$orgtelegrammessengervoipVoIPService(this.f$1);
    }
}
