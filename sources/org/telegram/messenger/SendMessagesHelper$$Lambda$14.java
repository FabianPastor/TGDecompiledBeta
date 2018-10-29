package org.telegram.messenger;

import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.tgnet.TLRPC.TL_document;

final /* synthetic */ class SendMessagesHelper$$Lambda$14 implements Runnable {
    private final MessageObject arg$1;
    private final int arg$2;
    private final TL_document arg$3;
    private final String arg$4;
    private final HashMap arg$5;
    private final long arg$6;
    private final MessageObject arg$7;
    private final String arg$8;
    private final ArrayList arg$9;

    SendMessagesHelper$$Lambda$14(MessageObject messageObject, int i, TL_document tL_document, String str, HashMap hashMap, long j, MessageObject messageObject2, String str2, ArrayList arrayList) {
        this.arg$1 = messageObject;
        this.arg$2 = i;
        this.arg$3 = tL_document;
        this.arg$4 = str;
        this.arg$5 = hashMap;
        this.arg$6 = j;
        this.arg$7 = messageObject2;
        this.arg$8 = str2;
        this.arg$9 = arrayList;
    }

    public void run() {
        SendMessagesHelper.lambda$prepareSendingDocumentInternal$43$SendMessagesHelper(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9);
    }
}
