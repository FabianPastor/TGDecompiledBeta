package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.updates_ChannelDifference;

final /* synthetic */ class MessagesController$$Lambda$187 implements Runnable {
    private final MessagesController arg$1;
    private final ArrayList arg$2;
    private final updates_ChannelDifference arg$3;

    MessagesController$$Lambda$187(MessagesController messagesController, ArrayList arrayList, updates_ChannelDifference updates_channeldifference) {
        this.arg$1 = messagesController;
        this.arg$2 = arrayList;
        this.arg$3 = updates_channeldifference;
    }

    public void run() {
        this.arg$1.lambda$null$199$MessagesController(this.arg$2, this.arg$3);
    }
}
