package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class MessagesController$$Lambda$128 implements Runnable {
    private final MessagesController arg$1;
    private final boolean arg$2;
    private final int arg$3;
    private final ArrayList arg$4;

    MessagesController$$Lambda$128(MessagesController messagesController, boolean z, int i, ArrayList arrayList) {
        this.arg$1 = messagesController;
        this.arg$2 = z;
        this.arg$3 = i;
        this.arg$4 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$processUpdates$213$MessagesController(this.arg$2, this.arg$3, this.arg$4);
    }
}
