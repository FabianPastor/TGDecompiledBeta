package org.telegram.messenger;

import android.util.LongSparseArray;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.messages_Dialogs;

final /* synthetic */ class MessagesStorage$$Lambda$27 implements Runnable {
    private final MessagesStorage arg$1;
    private final LongSparseArray arg$10;
    private final LongSparseArray arg$11;
    private final messages_Dialogs arg$2;
    private final int arg$3;
    private final int arg$4;
    private final int arg$5;
    private final int arg$6;
    private final int arg$7;
    private final Message arg$8;
    private final int arg$9;

    MessagesStorage$$Lambda$27(MessagesStorage messagesStorage, messages_Dialogs messages_dialogs, int i, int i2, int i3, int i4, int i5, Message message, int i6, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.arg$1 = messagesStorage;
        this.arg$2 = messages_dialogs;
        this.arg$3 = i;
        this.arg$4 = i2;
        this.arg$5 = i3;
        this.arg$6 = i4;
        this.arg$7 = i5;
        this.arg$8 = message;
        this.arg$9 = i6;
        this.arg$10 = longSparseArray;
        this.arg$11 = longSparseArray2;
    }

    public void run() {
        this.arg$1.lambda$resetDialogs$46$MessagesStorage(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10, this.arg$11);
    }
}
