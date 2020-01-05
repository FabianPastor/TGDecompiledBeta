package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_messageReactions;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$7NdoJnf6C2xsW-CLASSNAMEBc6nDcAwX4 implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ TL_messageReactions f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$7NdoJnf6C2xsW-CLASSNAMEBc6nDcAwX4(MessagesStorage messagesStorage, int i, int i2, TL_messageReactions tL_messageReactions) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = tL_messageReactions;
    }

    public final void run() {
        this.f$0.lambda$updateMessageReactions$51$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}
