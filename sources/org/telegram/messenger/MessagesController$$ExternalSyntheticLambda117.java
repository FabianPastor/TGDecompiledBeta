package org.telegram.messenger;

import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda117 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ TLRPC.User f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ TLRPC.UserFull f$5;
    public final /* synthetic */ ArrayList f$6;
    public final /* synthetic */ HashMap f$7;
    public final /* synthetic */ int f$8;
    public final /* synthetic */ boolean f$9;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda117(MessagesController messagesController, boolean z, TLRPC.User user, int i, boolean z2, TLRPC.UserFull userFull, ArrayList arrayList, HashMap hashMap, int i2, boolean z3) {
        this.f$0 = messagesController;
        this.f$1 = z;
        this.f$2 = user;
        this.f$3 = i;
        this.f$4 = z2;
        this.f$5 = userFull;
        this.f$6 = arrayList;
        this.f$7 = hashMap;
        this.f$8 = i2;
        this.f$9 = z3;
    }

    public final void run() {
        this.f$0.m358x5ba17fa0(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
    }
}
