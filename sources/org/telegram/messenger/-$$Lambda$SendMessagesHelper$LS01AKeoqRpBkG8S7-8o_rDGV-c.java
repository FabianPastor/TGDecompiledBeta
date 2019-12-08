package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$LS01AKeoqRpBkG8S7-8o_rDGV-c implements Runnable {
    private final /* synthetic */ ArrayList f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ AccountInstance f$2;
    private final /* synthetic */ String f$3;
    private final /* synthetic */ MessageObject f$4;
    private final /* synthetic */ MessageObject f$5;
    private final /* synthetic */ boolean f$6;
    private final /* synthetic */ int f$7;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$LS01AKeoqRpBkG8S7-8o_rDGV-c(ArrayList arrayList, long j, AccountInstance accountInstance, String str, MessageObject messageObject, MessageObject messageObject2, boolean z, int i) {
        this.f$0 = arrayList;
        this.f$1 = j;
        this.f$2 = accountInstance;
        this.f$3 = str;
        this.f$4 = messageObject;
        this.f$5 = messageObject2;
        this.f$6 = z;
        this.f$7 = i;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingAudioDocuments$54(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
