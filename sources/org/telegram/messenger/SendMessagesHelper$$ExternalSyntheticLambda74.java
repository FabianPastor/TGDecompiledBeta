package org.telegram.messenger;

import org.telegram.tgnet.QuickAckDelegate;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda74 implements QuickAckDelegate {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC.Message f$1;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda74(SendMessagesHelper sendMessagesHelper, TLRPC.Message message) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
    }

    public final void run() {
        this.f$0.m444x8efe81e7(this.f$1);
    }
}
