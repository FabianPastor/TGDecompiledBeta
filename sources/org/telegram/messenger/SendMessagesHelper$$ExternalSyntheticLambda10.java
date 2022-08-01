package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ ArrayList f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ AccountInstance f$2;
    public final /* synthetic */ CharSequence f$3;
    public final /* synthetic */ MessageObject f$4;
    public final /* synthetic */ MessageObject f$5;
    public final /* synthetic */ MessageObject f$6;
    public final /* synthetic */ boolean f$7;
    public final /* synthetic */ int f$8;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda10(ArrayList arrayList, long j, AccountInstance accountInstance, CharSequence charSequence, MessageObject messageObject, MessageObject messageObject2, MessageObject messageObject3, boolean z, int i) {
        this.f$0 = arrayList;
        this.f$1 = j;
        this.f$2 = accountInstance;
        this.f$3 = charSequence;
        this.f$4 = messageObject;
        this.f$5 = messageObject2;
        this.f$6 = messageObject3;
        this.f$7 = z;
        this.f$8 = i;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingAudioDocuments$75(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
