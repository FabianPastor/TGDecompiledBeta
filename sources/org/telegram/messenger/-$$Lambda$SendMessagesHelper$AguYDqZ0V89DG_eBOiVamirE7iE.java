package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Peer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$AguYDqZ0V89DG_eBOiVamirE7iE implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ Message f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ Peer f$3;
    private final /* synthetic */ ArrayList f$4;
    private final /* synthetic */ long f$5;
    private final /* synthetic */ Message f$6;
    private final /* synthetic */ int f$7;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$AguYDqZ0V89DG_eBOiVamirE7iE(SendMessagesHelper sendMessagesHelper, Message message, int i, Peer peer, ArrayList arrayList, long j, Message message2, int i2) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
        this.f$2 = i;
        this.f$3 = peer;
        this.f$4 = arrayList;
        this.f$5 = j;
        this.f$6 = message2;
        this.f$7 = i2;
    }

    public final void run() {
        this.f$0.lambda$null$6$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
