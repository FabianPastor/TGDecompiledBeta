package org.telegram.messenger;

import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$InputMedia;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda42 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC$InputMedia f$2;
    public final /* synthetic */ SendMessagesHelper.DelayedMessage f$3;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda42(SendMessagesHelper sendMessagesHelper, TLObject tLObject, TLRPC$InputMedia tLRPC$InputMedia, SendMessagesHelper.DelayedMessage delayedMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tLObject;
        this.f$2 = tLRPC$InputMedia;
        this.f$3 = delayedMessage;
    }

    public final void run() {
        this.f$0.lambda$uploadMultiMedia$34(this.f$1, this.f$2, this.f$3);
    }
}
