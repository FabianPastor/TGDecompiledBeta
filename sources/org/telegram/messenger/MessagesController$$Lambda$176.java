package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.updates_Difference;

final /* synthetic */ class MessagesController$$Lambda$176 implements Runnable {
    private final MessagesController arg$1;
    private final ArrayList arg$2;
    private final updates_Difference arg$3;

    MessagesController$$Lambda$176(MessagesController messagesController, ArrayList arrayList, updates_Difference updates_difference) {
        this.arg$1 = messagesController;
        this.arg$2 = arrayList;
        this.arg$3 = updates_difference;
    }

    public void run() {
        this.arg$1.lambda$null$197$MessagesController(this.arg$2, this.arg$3);
    }
}
