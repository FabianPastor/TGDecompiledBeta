package org.telegram.messenger;

import android.graphics.Bitmap;
import java.util.HashMap;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.tgnet.TLRPC.TL_photo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$-ub-T3xUMNzwYnSdFhOd8tT6H1A implements Runnable {
    private final /* synthetic */ Bitmap[] f$0;
    private final /* synthetic */ String[] f$1;
    private final /* synthetic */ MessageObject f$2;
    private final /* synthetic */ AccountInstance f$3;
    private final /* synthetic */ TL_photo f$4;
    private final /* synthetic */ HashMap f$5;
    private final /* synthetic */ String f$6;
    private final /* synthetic */ long f$7;
    private final /* synthetic */ MessageObject f$8;
    private final /* synthetic */ SendingMediaInfo f$9;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$-ub-T3xUMNzwYnSdFhOd8tT6H1A(Bitmap[] bitmapArr, String[] strArr, MessageObject messageObject, AccountInstance accountInstance, TL_photo tL_photo, HashMap hashMap, String str, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo) {
        this.f$0 = bitmapArr;
        this.f$1 = strArr;
        this.f$2 = messageObject;
        this.f$3 = accountInstance;
        this.f$4 = tL_photo;
        this.f$5 = hashMap;
        this.f$6 = str;
        this.f$7 = j;
        this.f$8 = messageObject2;
        this.f$9 = sendingMediaInfo;
    }

    public final void run() {
        SendMessagesHelper.lambda$null$58(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
    }
}
