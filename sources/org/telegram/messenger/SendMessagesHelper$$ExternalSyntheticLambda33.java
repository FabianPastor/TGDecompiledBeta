package org.telegram.messenger;

import androidx.core.view.inputmethod.InputContentInfoCompat;
import java.util.ArrayList;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda33 implements Runnable {
    public final /* synthetic */ long f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ InputContentInfoCompat f$10;
    public final /* synthetic */ boolean f$11;
    public final /* synthetic */ ArrayList f$12;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ AccountInstance f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ ArrayList f$5;
    public final /* synthetic */ String f$6;
    public final /* synthetic */ MessageObject f$7;
    public final /* synthetic */ MessageObject f$8;
    public final /* synthetic */ MessageObject f$9;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda33(long j, ArrayList arrayList, String str, AccountInstance accountInstance, int i, ArrayList arrayList2, String str2, MessageObject messageObject, MessageObject messageObject2, MessageObject messageObject3, InputContentInfoCompat inputContentInfoCompat, boolean z, ArrayList arrayList3) {
        this.f$0 = j;
        this.f$1 = arrayList;
        this.f$2 = str;
        this.f$3 = accountInstance;
        this.f$4 = i;
        this.f$5 = arrayList2;
        this.f$6 = str2;
        this.f$7 = messageObject;
        this.f$8 = messageObject2;
        this.f$9 = messageObject3;
        this.f$10 = inputContentInfoCompat;
        this.f$11 = z;
        this.f$12 = arrayList3;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingDocuments$77(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12);
    }
}
