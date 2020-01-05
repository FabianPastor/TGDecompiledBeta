package org.telegram.messenger;

import androidx.core.view.inputmethod.InputContentInfoCompat;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$EHYR3cc2EDsREL-ScT4lVDi5pJQ implements Runnable {
    private final /* synthetic */ ArrayList f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ AccountInstance f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ boolean f$4;
    private final /* synthetic */ MessageObject f$5;
    private final /* synthetic */ MessageObject f$6;
    private final /* synthetic */ boolean f$7;
    private final /* synthetic */ int f$8;
    private final /* synthetic */ InputContentInfoCompat f$9;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$EHYR3cc2EDsREL-ScT4lVDi5pJQ(ArrayList arrayList, long j, AccountInstance accountInstance, boolean z, boolean z2, MessageObject messageObject, MessageObject messageObject2, boolean z3, int i, InputContentInfoCompat inputContentInfoCompat) {
        this.f$0 = arrayList;
        this.f$1 = j;
        this.f$2 = accountInstance;
        this.f$3 = z;
        this.f$4 = z2;
        this.f$5 = messageObject;
        this.f$6 = messageObject2;
        this.f$7 = z3;
        this.f$8 = i;
        this.f$9 = inputContentInfoCompat;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingMedia$72(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
    }
}
