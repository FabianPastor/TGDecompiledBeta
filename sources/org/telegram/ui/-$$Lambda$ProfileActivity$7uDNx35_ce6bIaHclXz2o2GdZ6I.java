package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProfileActivity$7uDNx35_ce6bIaHclXz2o2GdZ6I implements Runnable {
    private final /* synthetic */ ProfileActivity f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ TL_channels_getParticipants f$3;

    public /* synthetic */ -$$Lambda$ProfileActivity$7uDNx35_ce6bIaHclXz2o2GdZ6I(ProfileActivity profileActivity, TL_error tL_error, TLObject tLObject, TL_channels_getParticipants tL_channels_getParticipants) {
        this.f$0 = profileActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = tL_channels_getParticipants;
    }

    public final void run() {
        this.f$0.lambda$null$18$ProfileActivity(this.f$1, this.f$2, this.f$3);
    }
}
