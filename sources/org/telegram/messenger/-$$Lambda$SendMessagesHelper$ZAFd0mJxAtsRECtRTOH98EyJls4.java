package org.telegram.messenger;

import android.graphics.Bitmap;
import org.telegram.tgnet.TLRPC.Document;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$ZAFd0mJxAtsRECtRTOH98EyJls4 implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ Bitmap[] f$1;
    private final /* synthetic */ String[] f$2;
    private final /* synthetic */ Document f$3;
    private final /* synthetic */ long f$4;
    private final /* synthetic */ MessageObject f$5;
    private final /* synthetic */ boolean f$6;
    private final /* synthetic */ int f$7;
    private final /* synthetic */ Object f$8;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$ZAFd0mJxAtsRECtRTOH98EyJls4(SendMessagesHelper sendMessagesHelper, Bitmap[] bitmapArr, String[] strArr, Document document, long j, MessageObject messageObject, boolean z, int i, Object obj) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = bitmapArr;
        this.f$2 = strArr;
        this.f$3 = document;
        this.f$4 = j;
        this.f$5 = messageObject;
        this.f$6 = z;
        this.f$7 = i;
        this.f$8 = obj;
    }

    public final void run() {
        this.f$0.lambda$null$5$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
