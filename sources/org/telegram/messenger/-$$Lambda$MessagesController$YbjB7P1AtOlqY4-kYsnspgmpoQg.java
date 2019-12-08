package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.ChatFull;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$YbjB7P1AtOlqY4-kYsnspgmpoQg implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ boolean f$4;
    private final /* synthetic */ ChatFull f$5;
    private final /* synthetic */ ArrayList f$6;
    private final /* synthetic */ MessageObject f$7;

    public /* synthetic */ -$$Lambda$MessagesController$YbjB7P1AtOlqY4-kYsnspgmpoQg(MessagesController messagesController, boolean z, int i, boolean z2, boolean z3, ChatFull chatFull, ArrayList arrayList, MessageObject messageObject) {
        this.f$0 = messagesController;
        this.f$1 = z;
        this.f$2 = i;
        this.f$3 = z2;
        this.f$4 = z3;
        this.f$5 = chatFull;
        this.f$6 = arrayList;
        this.f$7 = messageObject;
    }

    public final void run() {
        this.f$0.lambda$processChatInfo$91$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
