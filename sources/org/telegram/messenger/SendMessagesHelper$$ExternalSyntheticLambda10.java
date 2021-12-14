package org.telegram.messenger;

import androidx.core.view.inputmethod.InputContentInfoCompat;
import java.util.ArrayList;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ ArrayList f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ InputContentInfoCompat f$10;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ AccountInstance f$4;
    public final /* synthetic */ MessageObject f$5;
    public final /* synthetic */ MessageObject f$6;
    public final /* synthetic */ MessageObject f$7;
    public final /* synthetic */ boolean f$8;
    public final /* synthetic */ int f$9;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda10(ArrayList arrayList, long j, boolean z, boolean z2, AccountInstance accountInstance, MessageObject messageObject, MessageObject messageObject2, MessageObject messageObject3, boolean z3, int i, InputContentInfoCompat inputContentInfoCompat) {
        this.f$0 = arrayList;
        this.f$1 = j;
        this.f$2 = z;
        this.f$3 = z2;
        this.f$4 = accountInstance;
        this.f$5 = messageObject;
        this.f$6 = messageObject2;
        this.f$7 = messageObject3;
        this.f$8 = z3;
        this.f$9 = i;
        this.f$10 = inputContentInfoCompat;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingMedia$89(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10);
    }
}
