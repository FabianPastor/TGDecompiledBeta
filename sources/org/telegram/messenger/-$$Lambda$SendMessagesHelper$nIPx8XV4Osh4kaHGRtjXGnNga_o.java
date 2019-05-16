package org.telegram.messenger;

import androidx.core.view.inputmethod.InputContentInfoCompat;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$nIPx8XV4Osh4kaHGRtjXGnNga_o implements Runnable {
    private final /* synthetic */ ArrayList f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ boolean f$4;
    private final /* synthetic */ MessageObject f$5;
    private final /* synthetic */ MessageObject f$6;
    private final /* synthetic */ InputContentInfoCompat f$7;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$nIPx8XV4Osh4kaHGRtjXGnNga_o(ArrayList arrayList, long j, int i, boolean z, boolean z2, MessageObject messageObject, MessageObject messageObject2, InputContentInfoCompat inputContentInfoCompat) {
        this.f$0 = arrayList;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = z;
        this.f$4 = z2;
        this.f$5 = messageObject;
        this.f$6 = messageObject2;
        this.f$7 = inputContentInfoCompat;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingMedia$60(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
