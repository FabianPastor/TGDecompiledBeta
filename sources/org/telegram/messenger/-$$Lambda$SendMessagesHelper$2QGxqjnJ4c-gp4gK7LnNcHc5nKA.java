package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$2QGxqjnJ4c-gp4gK7LnNcHc5nKA implements Runnable {
    private final /* synthetic */ ArrayList f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ AccountInstance f$2;
    private final /* synthetic */ MessageObject f$3;
    private final /* synthetic */ MessageObject f$4;
    private final /* synthetic */ boolean f$5;
    private final /* synthetic */ int f$6;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$2QGxqjnJ4c-gp4gK7LnNcHc5nKA(ArrayList arrayList, long j, AccountInstance accountInstance, MessageObject messageObject, MessageObject messageObject2, boolean z, int i) {
        this.f$0 = arrayList;
        this.f$1 = j;
        this.f$2 = accountInstance;
        this.f$3 = messageObject;
        this.f$4 = messageObject2;
        this.f$5 = z;
        this.f$6 = i;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingAudioDocuments$48(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
