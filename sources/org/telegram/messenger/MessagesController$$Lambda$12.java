package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class MessagesController$$Lambda$12 implements Runnable {
    private final MessagesController arg$1;
    private final int arg$2;
    private final ArrayList arg$3;
    private final boolean arg$4;

    MessagesController$$Lambda$12(MessagesController messagesController, int i, ArrayList arrayList, boolean z) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = arrayList;
        this.arg$4 = z;
    }

    public void run() {
        this.arg$1.lambda$processLoadedChannelAdmins$12$MessagesController(this.arg$2, this.arg$3, this.arg$4);
    }
}
