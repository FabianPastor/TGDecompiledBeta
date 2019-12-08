package org.telegram.ui;

import org.telegram.tgnet.TLRPC.Chat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PeopleNearbyActivity$l7oXjY0b-wLmYvYWbRJsU21AcTA implements Runnable {
    private final /* synthetic */ PeopleNearbyActivity f$0;
    private final /* synthetic */ Chat f$1;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ boolean f$3;

    public /* synthetic */ -$$Lambda$PeopleNearbyActivity$l7oXjY0b-wLmYvYWbRJsU21AcTA(PeopleNearbyActivity peopleNearbyActivity, Chat chat, long j, boolean z) {
        this.f$0 = peopleNearbyActivity;
        this.f$1 = chat;
        this.f$2 = j;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.lambda$didReceivedNotification$7$PeopleNearbyActivity(this.f$1, this.f$2, this.f$3);
    }
}
