package org.telegram.messenger;

import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLObject;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda42 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ SendMessagesHelper.DelayedMessage f$2;
    public final /* synthetic */ String f$3;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda42(SendMessagesHelper sendMessagesHelper, TLObject tLObject, SendMessagesHelper.DelayedMessage delayedMessage, String str) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tLObject;
        this.f$2 = delayedMessage;
        this.f$3 = str;
    }

    public final void run() {
        this.f$0.lambda$performSendDelayedMessage$32(this.f$1, this.f$2, this.f$3);
    }
}
