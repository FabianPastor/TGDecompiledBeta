package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$qaVxMxZZgT13ZZ7VOgNBw4U51uM implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ boolean f$3;

    public /* synthetic */ -$$Lambda$MessagesController$qaVxMxZZgT13ZZ7VOgNBw4U51uM(MessagesController messagesController, int i, ArrayList arrayList, boolean z) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = arrayList;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.lambda$processLoadedChannelAdmins$14$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}
