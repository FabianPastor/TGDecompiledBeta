package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$Updates;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda210 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ TLRPC$Updates f$2;
    public final /* synthetic */ ArrayList f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda210(MessagesController messagesController, boolean z, TLRPC$Updates tLRPC$Updates, ArrayList arrayList) {
        this.f$0 = messagesController;
        this.f$1 = z;
        this.f$2 = tLRPC$Updates;
        this.f$3 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$processUpdates$303(this.f$1, this.f$2, this.f$3);
    }
}
