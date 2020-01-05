package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$_gHTxqQKXqvVN-u6Eg96RvX1uR8 implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ Message f$2;
    private final /* synthetic */ ArrayList f$3;
    private final /* synthetic */ MessageObject f$4;
    private final /* synthetic */ int f$5;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$_gHTxqQKXqvVN-u6Eg96RvX1uR8(SendMessagesHelper sendMessagesHelper, int i, Message message, ArrayList arrayList, MessageObject messageObject, int i2) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = i;
        this.f$2 = message;
        this.f$3 = arrayList;
        this.f$4 = messageObject;
        this.f$5 = i2;
    }

    public final void run() {
        this.f$0.lambda$null$9$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
