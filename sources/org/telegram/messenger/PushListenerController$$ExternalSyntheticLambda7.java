package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PushListenerController$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ TLRPC$TL_error f$0;

    public /* synthetic */ PushListenerController$$ExternalSyntheticLambda7(TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = tLRPC$TL_error;
    }

    public final void run() {
        PushListenerController.lambda$sendRegistrationToServer$0(this.f$0);
    }
}
