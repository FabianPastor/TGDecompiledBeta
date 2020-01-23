package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.HashMap;
import org.telegram.tgnet.TLObject;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$j4pncmCsbD35mWHgyMJum7o7XvI implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ HashMap f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ TLObject f$3;
    private final /* synthetic */ LongSparseArray f$4;
    private final /* synthetic */ long f$5;
    private final /* synthetic */ boolean f$6;

    public /* synthetic */ -$$Lambda$MessagesController$j4pncmCsbD35mWHgyMJum7o7XvI(MessagesController messagesController, HashMap hashMap, String str, TLObject tLObject, LongSparseArray longSparseArray, long j, boolean z) {
        this.f$0 = messagesController;
        this.f$1 = hashMap;
        this.f$2 = str;
        this.f$3 = tLObject;
        this.f$4 = longSparseArray;
        this.f$5 = j;
        this.f$6 = z;
    }

    public final void run() {
        this.f$0.lambda$null$118$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
