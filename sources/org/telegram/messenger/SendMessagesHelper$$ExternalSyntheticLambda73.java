package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda73 implements Runnable {
    public final /* synthetic */ VideoEditedInfo f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ boolean f$10;
    public final /* synthetic */ int f$11;
    public final /* synthetic */ boolean f$12;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ AccountInstance f$4;
    public final /* synthetic */ CharSequence f$5;
    public final /* synthetic */ MessageObject f$6;
    public final /* synthetic */ MessageObject f$7;
    public final /* synthetic */ MessageObject f$8;
    public final /* synthetic */ ArrayList f$9;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda73(VideoEditedInfo videoEditedInfo, String str, long j, int i, AccountInstance accountInstance, CharSequence charSequence, MessageObject messageObject, MessageObject messageObject2, MessageObject messageObject3, ArrayList arrayList, boolean z, int i2, boolean z2) {
        this.f$0 = videoEditedInfo;
        this.f$1 = str;
        this.f$2 = j;
        this.f$3 = i;
        this.f$4 = accountInstance;
        this.f$5 = charSequence;
        this.f$6 = messageObject;
        this.f$7 = messageObject2;
        this.f$8 = messageObject3;
        this.f$9 = arrayList;
        this.f$10 = z;
        this.f$11 = i2;
        this.f$12 = z2;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingVideo$91(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12);
    }
}
