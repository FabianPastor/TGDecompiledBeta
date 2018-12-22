package org.telegram.messenger;

import java.util.HashMap;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.tgnet.TLRPC.TL_document;

final /* synthetic */ class SendMessagesHelper$$Lambda$24 implements Runnable {
    private final MessageObject arg$1;
    private final int arg$2;
    private final TL_document arg$3;
    private final String arg$4;
    private final HashMap arg$5;
    private final String arg$6;
    private final long arg$7;
    private final MessageObject arg$8;
    private final SendingMediaInfo arg$9;

    SendMessagesHelper$$Lambda$24(MessageObject messageObject, int i, TL_document tL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo) {
        this.arg$1 = messageObject;
        this.arg$2 = i;
        this.arg$3 = tL_document;
        this.arg$4 = str;
        this.arg$5 = hashMap;
        this.arg$6 = str2;
        this.arg$7 = j;
        this.arg$8 = messageObject2;
        this.arg$9 = sendingMediaInfo;
    }

    public void run() {
        SendMessagesHelper.lambda$null$55$SendMessagesHelper(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9);
    }
}
