package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda49 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ArrayList f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda49(MessagesStorage messagesStorage, int i, ArrayList arrayList) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$putWallpapers$48(this.f$1, this.f$2);
    }
}