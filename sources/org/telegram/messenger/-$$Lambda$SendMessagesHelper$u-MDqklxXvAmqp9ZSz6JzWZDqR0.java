package org.telegram.messenger;

import androidx.core.view.inputmethod.InputContentInfoCompat;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$u-MDqklxXvAmqp9ZSz6JzWZDqR0 implements Runnable {
    private final /* synthetic */ ArrayList f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ String f$3;
    private final /* synthetic */ long f$4;
    private final /* synthetic */ MessageObject f$5;
    private final /* synthetic */ String f$6;
    private final /* synthetic */ MessageObject f$7;
    private final /* synthetic */ ArrayList f$8;
    private final /* synthetic */ InputContentInfoCompat f$9;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$u-MDqklxXvAmqp9ZSz6JzWZDqR0(ArrayList arrayList, int i, ArrayList arrayList2, String str, long j, MessageObject messageObject, String str2, MessageObject messageObject2, ArrayList arrayList3, InputContentInfoCompat inputContentInfoCompat) {
        this.f$0 = arrayList;
        this.f$1 = i;
        this.f$2 = arrayList2;
        this.f$3 = str;
        this.f$4 = j;
        this.f$5 = messageObject;
        this.f$6 = str2;
        this.f$7 = messageObject2;
        this.f$8 = arrayList3;
        this.f$9 = inputContentInfoCompat;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingDocuments$48(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
    }
}
