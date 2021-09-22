package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda179 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ ArrayList f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda179(MessagesStorage messagesStorage, boolean z, ArrayList arrayList) {
        this.f$0 = messagesStorage;
        this.f$1 = z;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$putContacts$103(this.f$1, this.f$2);
    }
}
