package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.Message;

final /* synthetic */ class SendMessagesHelper$$Lambda$41 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final boolean arg$2;
    private final ArrayList arg$3;
    private final Message arg$4;
    private final int arg$5;

    SendMessagesHelper$$Lambda$41(SendMessagesHelper sendMessagesHelper, boolean z, ArrayList arrayList, Message message, int i) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = z;
        this.arg$3 = arrayList;
        this.arg$4 = message;
        this.arg$5 = i;
    }

    public void run() {
        this.arg$1.lambda$null$36$SendMessagesHelper(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
