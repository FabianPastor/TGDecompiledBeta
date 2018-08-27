package org.telegram.messenger;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC.TL_document;

final /* synthetic */ class SendMessagesHelper$$Lambda$32 implements Runnable {
    private final MessageObject arg$1;
    private final int arg$2;
    private final TL_document arg$3;
    private final MessageObject arg$4;
    private final HashMap arg$5;
    private final long arg$6;
    private final MessageObject arg$7;

    SendMessagesHelper$$Lambda$32(MessageObject messageObject, int i, TL_document tL_document, MessageObject messageObject2, HashMap hashMap, long j, MessageObject messageObject3) {
        this.arg$1 = messageObject;
        this.arg$2 = i;
        this.arg$3 = tL_document;
        this.arg$4 = messageObject2;
        this.arg$5 = hashMap;
        this.arg$6 = j;
        this.arg$7 = messageObject3;
    }

    public void run() {
        SendMessagesHelper.lambda$null$44$SendMessagesHelper(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7);
    }
}
