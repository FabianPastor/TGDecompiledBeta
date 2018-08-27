package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ProfileActivity$$Lambda$12 implements RequestDelegate {
    private final ProfileActivity arg$1;
    private final TL_channels_getParticipants arg$2;
    private final int arg$3;

    ProfileActivity$$Lambda$12(ProfileActivity profileActivity, TL_channels_getParticipants tL_channels_getParticipants, int i) {
        this.arg$1 = profileActivity;
        this.arg$2 = tL_channels_getParticipants;
        this.arg$3 = i;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$getChannelParticipants$21$ProfileActivity(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
