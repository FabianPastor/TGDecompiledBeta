package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.Message;

final /* synthetic */ class SendMessagesHelper$$Lambda$47 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final Message arg$2;
    private final int arg$3;
    private final ArrayList arg$4;
    private final long arg$5;

    SendMessagesHelper$$Lambda$47(SendMessagesHelper sendMessagesHelper, Message message, int i, ArrayList arrayList, long j) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = message;
        this.arg$3 = i;
        this.arg$4 = arrayList;
        this.arg$5 = j;
    }

    public void run() {
        this.arg$1.lambda$null$25$SendMessagesHelper(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
