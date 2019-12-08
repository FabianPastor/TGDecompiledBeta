package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_chatBannedRights;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$q8e3IRJkycqkklVOmKbVmcIbgbw implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ TL_chatBannedRights f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$q8e3IRJkycqkklVOmKbVmcIbgbw(MessagesStorage messagesStorage, int i, int i2, TL_chatBannedRights tL_chatBannedRights) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = tL_chatBannedRights;
    }

    public final void run() {
        this.f$0.lambda$updateChatDefaultBannedRights$109$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}
