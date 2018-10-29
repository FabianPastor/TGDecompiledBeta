package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Peer;

final /* synthetic */ class SendMessagesHelper$$Lambda$56 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final Message arg$2;
    private final int arg$3;
    private final Peer arg$4;
    private final ArrayList arg$5;
    private final long arg$6;
    private final Message arg$7;

    SendMessagesHelper$$Lambda$56(SendMessagesHelper sendMessagesHelper, Message message, int i, Peer peer, ArrayList arrayList, long j, Message message2) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = message;
        this.arg$3 = i;
        this.arg$4 = peer;
        this.arg$5 = arrayList;
        this.arg$6 = j;
        this.arg$7 = message2;
    }

    public void run() {
        this.arg$1.lambda$null$6$SendMessagesHelper(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7);
    }
}
