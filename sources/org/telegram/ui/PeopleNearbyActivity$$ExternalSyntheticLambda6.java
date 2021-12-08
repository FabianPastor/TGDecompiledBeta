package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PeopleNearbyActivity$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ PeopleNearbyActivity f$0;
    public final /* synthetic */ TLRPC.Chat f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ PeopleNearbyActivity$$ExternalSyntheticLambda6(PeopleNearbyActivity peopleNearbyActivity, TLRPC.Chat chat, long j, boolean z) {
        this.f$0 = peopleNearbyActivity;
        this.f$1 = chat;
        this.f$2 = j;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.m3529xa0217175(this.f$1, this.f$2, this.f$3);
    }
}
