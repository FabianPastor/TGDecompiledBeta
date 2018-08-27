package org.telegram.messenger;

import java.util.HashMap;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.tgnet.TLRPC.TL_photo;

final /* synthetic */ class SendMessagesHelper$$Lambda$24 implements Runnable {
    private final MessageObject arg$1;
    private final int arg$2;
    private final TL_photo arg$3;
    private final boolean arg$4;
    private final SendingMediaInfo arg$5;
    private final HashMap arg$6;
    private final long arg$7;
    private final MessageObject arg$8;

    SendMessagesHelper$$Lambda$24(MessageObject messageObject, int i, TL_photo tL_photo, boolean z, SendingMediaInfo sendingMediaInfo, HashMap hashMap, long j, MessageObject messageObject2) {
        this.arg$1 = messageObject;
        this.arg$2 = i;
        this.arg$3 = tL_photo;
        this.arg$4 = z;
        this.arg$5 = sendingMediaInfo;
        this.arg$6 = hashMap;
        this.arg$7 = j;
        this.arg$8 = messageObject2;
    }

    public void run() {
        SendMessagesHelper.lambda$null$55$SendMessagesHelper(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8);
    }
}
