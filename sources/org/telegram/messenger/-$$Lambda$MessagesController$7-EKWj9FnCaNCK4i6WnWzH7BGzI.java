package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$7-EKWj9FnCaNCK4i6WnWzH7BGzI implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ ArrayList f$1;

    public /* synthetic */ -$$Lambda$MessagesController$7-EKWj9FnCaNCK4i6WnWzH7BGzI(MessagesController messagesController, ArrayList arrayList) {
        this.f$0 = messagesController;
        this.f$1 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$processUpdates$239$MessagesController(this.f$1);
    }
}