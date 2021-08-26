package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda103 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda103(MessagesController messagesController, ArrayList arrayList, int i, boolean z, int i2) {
        this.f$0 = messagesController;
        this.f$1 = arrayList;
        this.f$2 = i;
        this.f$3 = z;
        this.f$4 = i2;
    }

    public final void run() {
        this.f$0.lambda$processLoadedDeleteTask$59(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
