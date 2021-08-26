package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda129 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda129(MessagesStorage messagesStorage, ArrayList arrayList, ArrayList arrayList2, int i) {
        this.f$0 = messagesStorage;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$updateDialogsWithDeletedMessages$158(this.f$1, this.f$2, this.f$3);
    }
}
