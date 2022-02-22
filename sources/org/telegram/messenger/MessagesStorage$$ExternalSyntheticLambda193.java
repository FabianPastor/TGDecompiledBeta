package org.telegram.messenger;

import java.util.ArrayList;
import java.util.HashMap;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda193 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ HashMap f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ long f$4;
    public final /* synthetic */ ArrayList f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ boolean f$7;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda193(MessagesStorage messagesStorage, boolean z, HashMap hashMap, int i, long j, ArrayList arrayList, int i2, boolean z2) {
        this.f$0 = messagesStorage;
        this.f$1 = z;
        this.f$2 = hashMap;
        this.f$3 = i;
        this.f$4 = j;
        this.f$5 = arrayList;
        this.f$6 = i2;
        this.f$7 = z2;
    }

    public final void run() {
        this.f$0.lambda$updatePinnedMessages$106(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
