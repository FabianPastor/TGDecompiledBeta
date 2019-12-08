package org.telegram.messenger;

import androidx.core.view.inputmethod.InputContentInfoCompat;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$kLrbnFvOYkrPvx2qKGH-RA8s0F0 implements Runnable {
    private final /* synthetic */ ArrayList f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ ArrayList f$10;
    private final /* synthetic */ InputContentInfoCompat f$11;
    private final /* synthetic */ AccountInstance f$2;
    private final /* synthetic */ ArrayList f$3;
    private final /* synthetic */ String f$4;
    private final /* synthetic */ long f$5;
    private final /* synthetic */ MessageObject f$6;
    private final /* synthetic */ MessageObject f$7;
    private final /* synthetic */ boolean f$8;
    private final /* synthetic */ int f$9;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$kLrbnFvOYkrPvx2qKGH-RA8s0F0(ArrayList arrayList, String str, AccountInstance accountInstance, ArrayList arrayList2, String str2, long j, MessageObject messageObject, MessageObject messageObject2, boolean z, int i, ArrayList arrayList3, InputContentInfoCompat inputContentInfoCompat) {
        this.f$0 = arrayList;
        this.f$1 = str;
        this.f$2 = accountInstance;
        this.f$3 = arrayList2;
        this.f$4 = str2;
        this.f$5 = j;
        this.f$6 = messageObject;
        this.f$7 = messageObject2;
        this.f$8 = z;
        this.f$9 = i;
        this.f$10 = arrayList3;
        this.f$11 = inputContentInfoCompat;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingDocuments$54(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11);
    }
}
