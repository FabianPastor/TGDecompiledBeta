package org.telegram.messenger;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda11 implements Runnable {
    public final /* synthetic */ MessagesController.ErrorDelegate f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda11(MessagesController.ErrorDelegate errorDelegate, TLRPC.TL_error tL_error) {
        this.f$0 = errorDelegate;
        this.f$1 = tL_error;
    }

    public final void run() {
        this.f$0.run(this.f$1);
    }
}
