package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda148 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda148(MessagesStorage messagesStorage, ArrayList arrayList, boolean z, boolean z2) {
        this.f$0 = messagesStorage;
        this.f$1 = arrayList;
        this.f$2 = z;
        this.f$3 = z2;
    }

    public final void run() {
        this.f$0.lambda$updateUsers$167(this.f$1, this.f$2, this.f$3);
    }
}
