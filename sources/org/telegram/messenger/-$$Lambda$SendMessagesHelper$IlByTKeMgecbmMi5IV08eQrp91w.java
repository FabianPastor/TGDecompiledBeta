package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$IlByTKeMgecbmMi5IV08eQrp91w implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ Message f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ ArrayList f$4;
    private final /* synthetic */ long f$5;
    private final /* synthetic */ int f$6;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$IlByTKeMgecbmMi5IV08eQrp91w(SendMessagesHelper sendMessagesHelper, Message message, int i, boolean z, ArrayList arrayList, long j, int i2) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
        this.f$2 = i;
        this.f$3 = z;
        this.f$4 = arrayList;
        this.f$5 = j;
        this.f$6 = i2;
    }

    public final void run() {
        this.f$0.lambda$null$32$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
