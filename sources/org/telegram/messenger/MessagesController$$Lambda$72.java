package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.messages_Dialogs;

final /* synthetic */ class MessagesController$$Lambda$72 implements Runnable {
    private final MessagesController arg$1;
    private final int arg$2;
    private final messages_Dialogs arg$3;
    private final boolean arg$4;
    private final int arg$5;
    private final int arg$6;
    private final boolean arg$7;
    private final boolean arg$8;
    private final ArrayList arg$9;

    MessagesController$$Lambda$72(MessagesController messagesController, int i, messages_Dialogs messages_dialogs, boolean z, int i2, int i3, boolean z2, boolean z3, ArrayList arrayList) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = messages_dialogs;
        this.arg$4 = z;
        this.arg$5 = i2;
        this.arg$6 = i3;
        this.arg$7 = z2;
        this.arg$8 = z3;
        this.arg$9 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$processLoadedDialogs$108$MessagesController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9);
    }
}
