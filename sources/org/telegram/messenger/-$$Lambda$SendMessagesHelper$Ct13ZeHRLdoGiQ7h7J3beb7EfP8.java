package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$Ct13ZeHRLdoGiQ7h7J3beb7EfP8 implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ MessageObject f$2;
    private final /* synthetic */ Message f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ int f$5;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$Ct13ZeHRLdoGiQ7h7J3beb7EfP8(SendMessagesHelper sendMessagesHelper, ArrayList arrayList, MessageObject messageObject, Message message, int i, int i2) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = arrayList;
        this.f$2 = messageObject;
        this.f$3 = message;
        this.f$4 = i;
        this.f$5 = i2;
    }

    public final void run() {
        this.f$0.lambda$null$8$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
