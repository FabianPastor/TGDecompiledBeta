package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$pND7SeCnHWGSkCmeT8Uq6NOWqtE implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ Message f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ ArrayList f$3;
    private final /* synthetic */ ArrayList f$4;
    private final /* synthetic */ int f$5;

    public /* synthetic */ -$$Lambda$MessagesStorage$pND7SeCnHWGSkCmeT8Uq6NOWqtE(MessagesStorage messagesStorage, Message message, boolean z, ArrayList arrayList, ArrayList arrayList2, int i) {
        this.f$0 = messagesStorage;
        this.f$1 = message;
        this.f$2 = z;
        this.f$3 = arrayList;
        this.f$4 = arrayList2;
        this.f$5 = i;
    }

    public final void run() {
        this.f$0.lambda$replaceMessageIfExists$137$MessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
