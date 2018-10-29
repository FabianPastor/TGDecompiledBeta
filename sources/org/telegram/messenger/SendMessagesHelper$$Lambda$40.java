package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.Message;

final /* synthetic */ class SendMessagesHelper$$Lambda$40 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final Message arg$2;
    private final int arg$3;
    private final boolean arg$4;
    private final ArrayList arg$5;
    private final String arg$6;

    SendMessagesHelper$$Lambda$40(SendMessagesHelper sendMessagesHelper, Message message, int i, boolean z, ArrayList arrayList, String str) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = message;
        this.arg$3 = i;
        this.arg$4 = z;
        this.arg$5 = arrayList;
        this.arg$6 = str;
    }

    public void run() {
        this.arg$1.lambda$null$37$SendMessagesHelper(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}
