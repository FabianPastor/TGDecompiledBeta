package org.telegram.messenger;

import android.support.v13.view.inputmethod.InputContentInfoCompat;
import java.util.ArrayList;

final /* synthetic */ class SendMessagesHelper$$Lambda$19 implements Runnable {
    private final ArrayList arg$1;
    private final long arg$2;
    private final int arg$3;
    private final boolean arg$4;
    private final boolean arg$5;
    private final MessageObject arg$6;
    private final MessageObject arg$7;
    private final InputContentInfoCompat arg$8;

    SendMessagesHelper$$Lambda$19(ArrayList arrayList, long j, int i, boolean z, boolean z2, MessageObject messageObject, MessageObject messageObject2, InputContentInfoCompat inputContentInfoCompat) {
        this.arg$1 = arrayList;
        this.arg$2 = j;
        this.arg$3 = i;
        this.arg$4 = z;
        this.arg$5 = z2;
        this.arg$6 = messageObject;
        this.arg$7 = messageObject2;
        this.arg$8 = inputContentInfoCompat;
    }

    public void run() {
        SendMessagesHelper.lambda$prepareSendingMedia$59$SendMessagesHelper(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8);
    }
}
