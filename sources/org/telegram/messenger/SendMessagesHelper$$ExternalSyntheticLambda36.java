package org.telegram.messenger;

import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda36 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC.InputMedia f$2;
    public final /* synthetic */ SendMessagesHelper.DelayedMessage f$3;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda36(SendMessagesHelper sendMessagesHelper, TLObject tLObject, TLRPC.InputMedia inputMedia, SendMessagesHelper.DelayedMessage delayedMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tLObject;
        this.f$2 = inputMedia;
        this.f$3 = delayedMessage;
    }

    public final void run() {
        this.f$0.m496xda8697f7(this.f$1, this.f$2, this.f$3);
    }
}
