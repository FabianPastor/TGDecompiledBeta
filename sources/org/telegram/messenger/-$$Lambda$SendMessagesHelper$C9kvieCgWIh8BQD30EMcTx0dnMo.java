package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$C9kvieCgWIh8BQD30EMcTx0dnMo implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ Message f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ ArrayList f$3;
    private final /* synthetic */ long f$4;
    private final /* synthetic */ int f$5;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$C9kvieCgWIh8BQD30EMcTx0dnMo(SendMessagesHelper sendMessagesHelper, Message message, int i, ArrayList arrayList, long j, int i2) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
        this.f$2 = i;
        this.f$3 = arrayList;
        this.f$4 = j;
        this.f$5 = i2;
    }

    public final void run() {
        this.f$0.lambda$null$27$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
