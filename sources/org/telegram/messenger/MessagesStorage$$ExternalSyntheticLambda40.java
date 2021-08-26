package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda40 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ ArrayList f$4;
    public final /* synthetic */ int f$5;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda40(MessagesStorage messagesStorage, int i, int i2, int i3, ArrayList arrayList, int i4) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = i3;
        this.f$4 = arrayList;
        this.f$5 = i4;
    }

    public final void run() {
        this.f$0.lambda$updateRepliesCount$143(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
