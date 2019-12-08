package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Peer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$VlNACebdgd1B51pcJPLYFJVMR40 implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ Message f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ Peer f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ ArrayList f$5;
    private final /* synthetic */ long f$6;
    private final /* synthetic */ Message f$7;
    private final /* synthetic */ int f$8;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$VlNACebdgd1B51pcJPLYFJVMR40(SendMessagesHelper sendMessagesHelper, Message message, int i, Peer peer, int i2, ArrayList arrayList, long j, Message message2, int i3) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
        this.f$2 = i;
        this.f$3 = peer;
        this.f$4 = i2;
        this.f$5 = arrayList;
        this.f$6 = j;
        this.f$7 = message2;
        this.f$8 = i3;
    }

    public final void run() {
        this.f$0.lambda$null$10$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
