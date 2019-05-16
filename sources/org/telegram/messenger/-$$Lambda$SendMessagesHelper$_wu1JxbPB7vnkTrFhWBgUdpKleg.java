package org.telegram.messenger;

import org.telegram.tgnet.QuickAckDelegate;
import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$_wu1JxbPB7vnkTrFhWBgUdpKleg implements QuickAckDelegate {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ Message f$1;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$_wu1JxbPB7vnkTrFhWBgUdpKleg(SendMessagesHelper sendMessagesHelper, Message message) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
    }

    public final void run() {
        this.f$0.lambda$performSendMessageRequest$42$SendMessagesHelper(this.f$1);
    }
}
