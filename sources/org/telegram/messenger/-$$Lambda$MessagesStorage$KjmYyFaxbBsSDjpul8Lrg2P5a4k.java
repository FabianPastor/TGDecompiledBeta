package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$KjmYyFaxbBsSDjpul8Lrg2P5a4k implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ boolean f$5;
    private final /* synthetic */ boolean f$6;

    public /* synthetic */ -$$Lambda$MessagesStorage$KjmYyFaxbBsSDjpul8Lrg2P5a4k(MessagesStorage messagesStorage, ArrayList arrayList, boolean z, boolean z2, int i, boolean z3, boolean z4) {
        this.f$0 = messagesStorage;
        this.f$1 = arrayList;
        this.f$2 = z;
        this.f$3 = z2;
        this.f$4 = i;
        this.f$5 = z3;
        this.f$6 = z4;
    }

    public final void run() {
        this.f$0.lambda$putMessages$120$MessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
