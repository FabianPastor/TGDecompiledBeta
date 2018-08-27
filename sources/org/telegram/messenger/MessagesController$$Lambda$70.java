package org.telegram.messenger;

import android.util.LongSparseArray;
import org.telegram.tgnet.TLRPC.messages_Dialogs;

final /* synthetic */ class MessagesController$$Lambda$70 implements Runnable {
    private final MessagesController arg$1;
    private final int arg$2;
    private final int arg$3;
    private final int arg$4;
    private final messages_Dialogs arg$5;
    private final LongSparseArray arg$6;
    private final LongSparseArray arg$7;

    MessagesController$$Lambda$70(MessagesController messagesController, int i, int i2, int i3, messages_Dialogs messages_dialogs, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = i2;
        this.arg$4 = i3;
        this.arg$5 = messages_dialogs;
        this.arg$6 = longSparseArray;
        this.arg$7 = longSparseArray2;
    }

    public void run() {
        this.arg$1.lambda$completeDialogsReset$101$MessagesController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7);
    }
}
