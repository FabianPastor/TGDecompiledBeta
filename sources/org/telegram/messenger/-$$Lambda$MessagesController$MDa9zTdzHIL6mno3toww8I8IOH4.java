package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.HashMap;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$MDa9zTdzHIL6mno3toww8I8IOH4 implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ HashMap f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ LongSparseArray f$3;
    private final /* synthetic */ long f$4;
    private final /* synthetic */ boolean f$5;

    public /* synthetic */ -$$Lambda$MessagesController$MDa9zTdzHIL6mno3toww8I8IOH4(MessagesController messagesController, HashMap hashMap, String str, LongSparseArray longSparseArray, long j, boolean z) {
        this.f$0 = messagesController;
        this.f$1 = hashMap;
        this.f$2 = str;
        this.f$3 = longSparseArray;
        this.f$4 = j;
        this.f$5 = z;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$reloadWebPages$119$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tL_error);
    }
}
