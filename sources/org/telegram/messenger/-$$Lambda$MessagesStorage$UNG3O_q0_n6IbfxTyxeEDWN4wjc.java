package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$UNG3O_q0_n6IbfxTyxeEDWN4wjc implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$UNG3O_q0_n6IbfxTyxeEDWN4wjc(MessagesStorage messagesStorage, ArrayList arrayList, int i) {
        this.f$0 = messagesStorage;
        this.f$1 = arrayList;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$unpinAllDialogsExceptNew$145$MessagesStorage(this.f$1, this.f$2);
    }
}
