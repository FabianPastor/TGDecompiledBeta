package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$Lyh7x_fVJCtY78eBEW_nfqaECxE implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ Message f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ int f$5;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$Lyh7x_fVJCtY78eBEW_nfqaECxE(SendMessagesHelper sendMessagesHelper, boolean z, ArrayList arrayList, Message message, int i, int i2) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = z;
        this.f$2 = arrayList;
        this.f$3 = message;
        this.f$4 = i;
        this.f$5 = i2;
    }

    public final void run() {
        this.f$0.lambda$null$37$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
