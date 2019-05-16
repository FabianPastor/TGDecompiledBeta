package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Dialog;
import org.telegram.tgnet.TLRPC.InputPeer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$uwK8wtI_v95kHeLcf4gXEkrDfLc implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ Dialog f$1;
    private final /* synthetic */ InputPeer f$2;
    private final /* synthetic */ long f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$uwK8wtI_v95kHeLcf4gXEkrDfLc(MessagesStorage messagesStorage, Dialog dialog, InputPeer inputPeer, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = dialog;
        this.f$2 = inputPeer;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.lambda$null$10$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}
