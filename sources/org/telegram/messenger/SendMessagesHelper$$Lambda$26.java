package org.telegram.messenger;

import java.util.HashMap;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.tgnet.TLRPC.TL_photo;

final /* synthetic */ class SendMessagesHelper$$Lambda$26 implements Runnable {
    private final MessageObject arg$1;
    private final int arg$2;
    private final TL_photo arg$3;
    private final HashMap arg$4;
    private final long arg$5;
    private final MessageObject arg$6;
    private final SendingMediaInfo arg$7;

    SendMessagesHelper$$Lambda$26(MessageObject messageObject, int i, TL_photo tL_photo, HashMap hashMap, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo) {
        this.arg$1 = messageObject;
        this.arg$2 = i;
        this.arg$3 = tL_photo;
        this.arg$4 = hashMap;
        this.arg$5 = j;
        this.arg$6 = messageObject2;
        this.arg$7 = sendingMediaInfo;
    }

    public void run() {
        SendMessagesHelper.lambda$null$57$SendMessagesHelper(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7);
    }
}
