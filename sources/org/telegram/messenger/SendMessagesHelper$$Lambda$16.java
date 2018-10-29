package org.telegram.messenger;

import android.support.v13.view.inputmethod.InputContentInfoCompat;
import java.util.ArrayList;

final /* synthetic */ class SendMessagesHelper$$Lambda$16 implements Runnable {
    private final ArrayList arg$1;
    private final int arg$2;
    private final ArrayList arg$3;
    private final String arg$4;
    private final long arg$5;
    private final MessageObject arg$6;
    private final MessageObject arg$7;
    private final ArrayList arg$8;
    private final InputContentInfoCompat arg$9;

    SendMessagesHelper$$Lambda$16(ArrayList arrayList, int i, ArrayList arrayList2, String str, long j, MessageObject messageObject, MessageObject messageObject2, ArrayList arrayList3, InputContentInfoCompat inputContentInfoCompat) {
        this.arg$1 = arrayList;
        this.arg$2 = i;
        this.arg$3 = arrayList2;
        this.arg$4 = str;
        this.arg$5 = j;
        this.arg$6 = messageObject;
        this.arg$7 = messageObject2;
        this.arg$8 = arrayList3;
        this.arg$9 = inputContentInfoCompat;
    }

    public void run() {
        SendMessagesHelper.lambda$prepareSendingDocuments$47$SendMessagesHelper(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9);
    }
}
