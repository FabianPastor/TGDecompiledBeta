package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda68 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ ArrayList f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda68(MessagesController messagesController, long j, int i, ArrayList arrayList) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$checkUnreadReactions$324(this.f$1, this.f$2, this.f$3);
    }
}
