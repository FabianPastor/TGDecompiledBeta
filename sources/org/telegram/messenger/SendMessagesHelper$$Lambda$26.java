package org.telegram.messenger;

import android.graphics.Bitmap;
import java.util.HashMap;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.tgnet.TLRPC.TL_document;

final /* synthetic */ class SendMessagesHelper$$Lambda$26 implements Runnable {
    private final Bitmap arg$1;
    private final long arg$10;
    private final MessageObject arg$11;
    private final SendingMediaInfo arg$12;
    private final String arg$2;
    private final MessageObject arg$3;
    private final int arg$4;
    private final VideoEditedInfo arg$5;
    private final TL_document arg$6;
    private final String arg$7;
    private final HashMap arg$8;
    private final String arg$9;

    SendMessagesHelper$$Lambda$26(Bitmap bitmap, String str, MessageObject messageObject, int i, VideoEditedInfo videoEditedInfo, TL_document tL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo) {
        this.arg$1 = bitmap;
        this.arg$2 = str;
        this.arg$3 = messageObject;
        this.arg$4 = i;
        this.arg$5 = videoEditedInfo;
        this.arg$6 = tL_document;
        this.arg$7 = str2;
        this.arg$8 = hashMap;
        this.arg$9 = str3;
        this.arg$10 = j;
        this.arg$11 = messageObject2;
        this.arg$12 = sendingMediaInfo;
    }

    public void run() {
        SendMessagesHelper.lambda$null$58$SendMessagesHelper(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10, this.arg$11, this.arg$12);
    }
}
