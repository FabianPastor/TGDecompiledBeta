package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$u2MYlCdSqK8HK7SN4n3B6vSgLvc implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ TL_messages_sendMultiMedia f$1;
    private final /* synthetic */ DelayedMessage f$2;
    private final /* synthetic */ ArrayList f$3;
    private final /* synthetic */ boolean f$4;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$u2MYlCdSqK8HK7SN4n3B6vSgLvc(SendMessagesHelper sendMessagesHelper, TL_messages_sendMultiMedia tL_messages_sendMultiMedia, DelayedMessage delayedMessage, ArrayList arrayList, boolean z) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tL_messages_sendMultiMedia;
        this.f$2 = delayedMessage;
        this.f$3 = arrayList;
        this.f$4 = z;
    }

    public final void run() {
        this.f$0.lambda$null$31$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
