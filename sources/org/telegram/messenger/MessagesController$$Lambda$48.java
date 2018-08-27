package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.ChatFull;

final /* synthetic */ class MessagesController$$Lambda$48 implements Runnable {
    private final MessagesController arg$1;
    private final ArrayList arg$2;
    private final boolean arg$3;
    private final ChatFull arg$4;
    private final boolean arg$5;
    private final MessageObject arg$6;

    MessagesController$$Lambda$48(MessagesController messagesController, ArrayList arrayList, boolean z, ChatFull chatFull, boolean z2, MessageObject messageObject) {
        this.arg$1 = messagesController;
        this.arg$2 = arrayList;
        this.arg$3 = z;
        this.arg$4 = chatFull;
        this.arg$5 = z2;
        this.arg$6 = messageObject;
    }

    public void run() {
        this.arg$1.lambda$processChatInfo$64$MessagesController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}
