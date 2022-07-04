package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class GroupVoipInviteAlert$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ GroupVoipInviteAlert f$0;
    public final /* synthetic */ TLRPC.TL_channels_getParticipants f$1;

    public /* synthetic */ GroupVoipInviteAlert$$ExternalSyntheticLambda3(GroupVoipInviteAlert groupVoipInviteAlert, TLRPC.TL_channels_getParticipants tL_channels_getParticipants) {
        this.f$0 = groupVoipInviteAlert;
        this.f$1 = tL_channels_getParticipants;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1017x8a32b9c(this.f$1, tLObject, tL_error);
    }
}
