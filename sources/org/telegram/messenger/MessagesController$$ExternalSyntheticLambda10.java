package org.telegram.messenger;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ MessagesController.ErrorDelegate f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda10(MessagesController.ErrorDelegate errorDelegate, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = errorDelegate;
        this.f$1 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.run(this.f$1);
    }
}
