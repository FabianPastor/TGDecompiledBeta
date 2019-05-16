package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.ChatFull;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$aj9gT3RqpNCMmoGiSwlydWLbsP8 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ ChatFull f$3;
    private final /* synthetic */ boolean f$4;
    private final /* synthetic */ MessageObject f$5;

    public /* synthetic */ -$$Lambda$MessagesController$aj9gT3RqpNCMmoGiSwlydWLbsP8(MessagesController messagesController, ArrayList arrayList, boolean z, ChatFull chatFull, boolean z2, MessageObject messageObject) {
        this.f$0 = messagesController;
        this.f$1 = arrayList;
        this.f$2 = z;
        this.f$3 = chatFull;
        this.f$4 = z2;
        this.f$5 = messageObject;
    }

    public final void run() {
        this.f$0.lambda$processChatInfo$75$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
