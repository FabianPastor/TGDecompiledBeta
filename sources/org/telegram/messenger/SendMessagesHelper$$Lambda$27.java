package org.telegram.messenger;

import android.graphics.Bitmap;
import java.util.HashMap;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.tgnet.TLRPC.TL_photo;

final /* synthetic */ class SendMessagesHelper$$Lambda$27 implements Runnable {
    private final Bitmap[] arg$1;
    private final SendingMediaInfo arg$10;
    private final String[] arg$2;
    private final MessageObject arg$3;
    private final int arg$4;
    private final TL_photo arg$5;
    private final HashMap arg$6;
    private final String arg$7;
    private final long arg$8;
    private final MessageObject arg$9;

    SendMessagesHelper$$Lambda$27(Bitmap[] bitmapArr, String[] strArr, MessageObject messageObject, int i, TL_photo tL_photo, HashMap hashMap, String str, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo) {
        this.arg$1 = bitmapArr;
        this.arg$2 = strArr;
        this.arg$3 = messageObject;
        this.arg$4 = i;
        this.arg$5 = tL_photo;
        this.arg$6 = hashMap;
        this.arg$7 = str;
        this.arg$8 = j;
        this.arg$9 = messageObject2;
        this.arg$10 = sendingMediaInfo;
    }

    public void run() {
        SendMessagesHelper.lambda$null$59$SendMessagesHelper(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10);
    }
}
