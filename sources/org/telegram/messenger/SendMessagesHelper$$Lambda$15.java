package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class SendMessagesHelper$$Lambda$15 implements Runnable {
    private final ArrayList arg$1;
    private final long arg$2;
    private final int arg$3;
    private final MessageObject arg$4;
    private final MessageObject arg$5;

    SendMessagesHelper$$Lambda$15(ArrayList arrayList, long j, int i, MessageObject messageObject, MessageObject messageObject2) {
        this.arg$1 = arrayList;
        this.arg$2 = j;
        this.arg$3 = i;
        this.arg$4 = messageObject;
        this.arg$5 = messageObject2;
    }

    public void run() {
        SendMessagesHelper.lambda$prepareSendingAudioDocuments$45$SendMessagesHelper(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
