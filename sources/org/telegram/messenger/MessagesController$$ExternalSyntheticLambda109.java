package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$updates_Difference;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda109 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ TLRPC$updates_Difference f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda109(MessagesController messagesController, ArrayList arrayList, TLRPC$updates_Difference tLRPC$updates_Difference) {
        this.f$0 = messagesController;
        this.f$1 = arrayList;
        this.f$2 = tLRPC$updates_Difference;
    }

    public final void run() {
        this.f$0.lambda$getDifference$259(this.f$1, this.f$2);
    }
}
