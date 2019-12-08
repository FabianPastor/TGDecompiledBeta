package org.telegram.messenger;

import org.telegram.tgnet.QuickAckDelegate;
import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$zDWuXZ4QQhTdHBax3KvIHfigGOU implements QuickAckDelegate {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ Message f$1;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$zDWuXZ4QQhTdHBax3KvIHfigGOU(SendMessagesHelper sendMessagesHelper, Message message) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
    }

    public final void run() {
        this.f$0.lambda$performSendMessageRequest$44$SendMessagesHelper(this.f$1);
    }
}
