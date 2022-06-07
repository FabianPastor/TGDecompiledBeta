package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda129 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ ArrayList f$1;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda129(MessagesStorage messagesStorage, ArrayList arrayList) {
        this.f$0 = messagesStorage;
        this.f$1 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$markMessagesAsDeletedInternal$169(this.f$1);
    }
}
