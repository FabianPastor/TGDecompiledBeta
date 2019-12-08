package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_messages_chatFull;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$ABRsa8DnNQCAwWkneP-WMpsWO-4 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ TL_messages_chatFull f$2;
    private final /* synthetic */ int f$3;

    public /* synthetic */ -$$Lambda$MessagesController$ABRsa8DnNQCAwWkneP-WMpsWO-4(MessagesController messagesController, int i, TL_messages_chatFull tL_messages_chatFull, int i2) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = tL_messages_chatFull;
        this.f$3 = i2;
    }

    public final void run() {
        this.f$0.lambda$null$25$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}
