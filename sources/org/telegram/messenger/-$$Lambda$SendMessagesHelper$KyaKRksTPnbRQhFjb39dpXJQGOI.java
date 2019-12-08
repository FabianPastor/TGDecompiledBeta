package org.telegram.messenger;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC.TL_document;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$KyaKRksTPnbRQhFjb39dpXJQGOI implements Runnable {
    private final /* synthetic */ MessageObject f$0;
    private final /* synthetic */ AccountInstance f$1;
    private final /* synthetic */ int f$10;
    private final /* synthetic */ TL_document f$2;
    private final /* synthetic */ MessageObject f$3;
    private final /* synthetic */ HashMap f$4;
    private final /* synthetic */ String f$5;
    private final /* synthetic */ long f$6;
    private final /* synthetic */ MessageObject f$7;
    private final /* synthetic */ String f$8;
    private final /* synthetic */ boolean f$9;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$KyaKRksTPnbRQhFjb39dpXJQGOI(MessageObject messageObject, AccountInstance accountInstance, TL_document tL_document, MessageObject messageObject2, HashMap hashMap, String str, long j, MessageObject messageObject3, String str2, boolean z, int i) {
        this.f$0 = messageObject;
        this.f$1 = accountInstance;
        this.f$2 = tL_document;
        this.f$3 = messageObject2;
        this.f$4 = hashMap;
        this.f$5 = str;
        this.f$6 = j;
        this.f$7 = messageObject3;
        this.f$8 = str2;
        this.f$9 = z;
        this.f$10 = i;
    }

    public final void run() {
        SendMessagesHelper.lambda$null$47(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10);
    }
}
