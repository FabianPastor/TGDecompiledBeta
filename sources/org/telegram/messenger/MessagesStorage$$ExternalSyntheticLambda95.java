package org.telegram.messenger;

import java.util.ArrayList;
import java.util.HashMap;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda95 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ HashMap f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ boolean f$6;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda95(MessagesStorage messagesStorage, long j, ArrayList arrayList, HashMap hashMap, int i, int i2, boolean z) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = arrayList;
        this.f$3 = hashMap;
        this.f$4 = i;
        this.f$5 = i2;
        this.f$6 = z;
    }

    public final void run() {
        this.f$0.lambda$updatePinnedMessages$98(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
