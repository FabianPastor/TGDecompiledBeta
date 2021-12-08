package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda23 implements RequestDelegate {
    public final /* synthetic */ ProfileActivity f$0;
    public final /* synthetic */ TLRPC.TL_channels_getParticipants f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda23(ProfileActivity profileActivity, TLRPC.TL_channels_getParticipants tL_channels_getParticipants, int i) {
        this.f$0 = profileActivity;
        this.f$1 = tL_channels_getParticipants;
        this.f$2 = i;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3703lambda$getChannelParticipants$23$orgtelegramuiProfileActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
