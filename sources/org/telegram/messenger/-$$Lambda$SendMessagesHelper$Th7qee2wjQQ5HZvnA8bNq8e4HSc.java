package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$Th7qee2wjQQ5HZvnA8bNq8e4HSc implements Runnable {
    private final /* synthetic */ ArrayList f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ AccountInstance f$2;
    private final /* synthetic */ MessageObject f$3;
    private final /* synthetic */ MessageObject f$4;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$Th7qee2wjQQ5HZvnA8bNq8e4HSc(ArrayList arrayList, long j, AccountInstance accountInstance, MessageObject messageObject, MessageObject messageObject2) {
        this.f$0 = arrayList;
        this.f$1 = j;
        this.f$2 = accountInstance;
        this.f$3 = messageObject;
        this.f$4 = messageObject2;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingAudioDocuments$46(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
