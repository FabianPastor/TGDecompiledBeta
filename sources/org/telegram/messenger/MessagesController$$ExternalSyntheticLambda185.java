package org.telegram.messenger;

import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.tgnet.TLRPC$ChatFull;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda185 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ boolean f$10;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ TLRPC$ChatFull f$5;
    public final /* synthetic */ ArrayList f$6;
    public final /* synthetic */ ArrayList f$7;
    public final /* synthetic */ HashMap f$8;
    public final /* synthetic */ int f$9;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda185(MessagesController messagesController, boolean z, long j, boolean z2, boolean z3, TLRPC$ChatFull tLRPC$ChatFull, ArrayList arrayList, ArrayList arrayList2, HashMap hashMap, int i, boolean z4) {
        this.f$0 = messagesController;
        this.f$1 = z;
        this.f$2 = j;
        this.f$3 = z2;
        this.f$4 = z3;
        this.f$5 = tLRPC$ChatFull;
        this.f$6 = arrayList;
        this.f$7 = arrayList2;
        this.f$8 = hashMap;
        this.f$9 = i;
        this.f$10 = z4;
    }

    public final void run() {
        this.f$0.lambda$processChatInfo$110(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10);
    }
}
