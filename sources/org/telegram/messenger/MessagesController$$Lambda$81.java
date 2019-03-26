package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.messages_Dialogs;

final /* synthetic */ class MessagesController$$Lambda$81 implements Runnable {
    private final MessagesController arg$1;
    private final int arg$2;
    private final messages_Dialogs arg$3;
    private final boolean arg$4;
    private final int arg$5;
    private final ArrayList arg$6;
    private final int arg$7;
    private final boolean arg$8;
    private final boolean arg$9;

    MessagesController$$Lambda$81(MessagesController messagesController, int i, messages_Dialogs messages_dialogs, boolean z, int i2, ArrayList arrayList, int i3, boolean z2, boolean z3) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = messages_dialogs;
        this.arg$4 = z;
        this.arg$5 = i2;
        this.arg$6 = arrayList;
        this.arg$7 = i3;
        this.arg$8 = z2;
        this.arg$9 = z3;
    }

    public void run() {
        this.arg$1.lambda$processLoadedDialogs$126$MessagesController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9);
    }
}
