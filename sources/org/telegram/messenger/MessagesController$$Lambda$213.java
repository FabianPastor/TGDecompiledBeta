package org.telegram.messenger;

import android.util.LongSparseArray;
import android.util.SparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.messages_Dialogs;

final /* synthetic */ class MessagesController$$Lambda$213 implements Runnable {
    private final MessagesController arg$1;
    private final boolean arg$10;
    private final int arg$11;
    private final ArrayList arg$12;
    private final int arg$2;
    private final messages_Dialogs arg$3;
    private final ArrayList arg$4;
    private final boolean arg$5;
    private final LongSparseArray arg$6;
    private final LongSparseArray arg$7;
    private final SparseArray arg$8;
    private final int arg$9;

    MessagesController$$Lambda$213(MessagesController messagesController, int i, messages_Dialogs messages_dialogs, ArrayList arrayList, boolean z, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, SparseArray sparseArray, int i2, boolean z2, int i3, ArrayList arrayList2) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = messages_dialogs;
        this.arg$4 = arrayList;
        this.arg$5 = z;
        this.arg$6 = longSparseArray;
        this.arg$7 = longSparseArray2;
        this.arg$8 = sparseArray;
        this.arg$9 = i2;
        this.arg$10 = z2;
        this.arg$11 = i3;
        this.arg$12 = arrayList2;
    }

    public void run() {
        this.arg$1.lambda$null$121$MessagesController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10, this.arg$11, this.arg$12);
    }
}
