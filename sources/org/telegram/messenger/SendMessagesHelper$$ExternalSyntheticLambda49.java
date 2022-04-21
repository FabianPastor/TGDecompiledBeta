package org.telegram.messenger;

import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda49 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC.Message f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ SendMessagesHelper.DelayedMessage f$4;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda49(SendMessagesHelper sendMessagesHelper, TLRPC.Message message, boolean z, TLObject tLObject, SendMessagesHelper.DelayedMessage delayedMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
        this.f$2 = z;
        this.f$3 = tLObject;
        this.f$4 = delayedMessage;
    }

    public final void run() {
        this.f$0.m440x836cd8ee(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
