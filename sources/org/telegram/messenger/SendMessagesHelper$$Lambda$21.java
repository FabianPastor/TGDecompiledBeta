package org.telegram.messenger;

import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.tgnet.TLRPC.TL_document;

final /* synthetic */ class SendMessagesHelper$$Lambda$21 implements Runnable {
    private final Bitmap arg$1;
    private final MessageObject arg$10;
    private final String arg$11;
    private final ArrayList arg$12;
    private final int arg$13;
    private final String arg$2;
    private final MessageObject arg$3;
    private final int arg$4;
    private final VideoEditedInfo arg$5;
    private final TL_document arg$6;
    private final String arg$7;
    private final HashMap arg$8;
    private final long arg$9;

    SendMessagesHelper$$Lambda$21(Bitmap bitmap, String str, MessageObject messageObject, int i, VideoEditedInfo videoEditedInfo, TL_document tL_document, String str2, HashMap hashMap, long j, MessageObject messageObject2, String str3, ArrayList arrayList, int i2) {
        this.arg$1 = bitmap;
        this.arg$2 = str;
        this.arg$3 = messageObject;
        this.arg$4 = i;
        this.arg$5 = videoEditedInfo;
        this.arg$6 = tL_document;
        this.arg$7 = str2;
        this.arg$8 = hashMap;
        this.arg$9 = j;
        this.arg$10 = messageObject2;
        this.arg$11 = str3;
        this.arg$12 = arrayList;
        this.arg$13 = i2;
    }

    public void run() {
        SendMessagesHelper.lambda$null$60$SendMessagesHelper(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10, this.arg$11, this.arg$12, this.arg$13);
    }
}
