package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.Updates;

final /* synthetic */ class MessagesController$$Lambda$129 implements Runnable {
    private final MessagesController arg$1;
    private final boolean arg$2;
    private final Updates arg$3;
    private final ArrayList arg$4;

    MessagesController$$Lambda$129(MessagesController messagesController, boolean z, Updates updates, ArrayList arrayList) {
        this.arg$1 = messagesController;
        this.arg$2 = z;
        this.arg$3 = updates;
        this.arg$4 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$processUpdates$214$MessagesController(this.arg$2, this.arg$3, this.arg$4);
    }
}
