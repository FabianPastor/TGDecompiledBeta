package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class GcmPushListenerService$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ TLRPC.TL_error f$0;

    public /* synthetic */ GcmPushListenerService$$ExternalSyntheticLambda8(TLRPC.TL_error tL_error) {
        this.f$0 = tL_error;
    }

    public final void run() {
        GcmPushListenerService.lambda$sendRegistrationToServer$6(this.f$0);
    }
}
