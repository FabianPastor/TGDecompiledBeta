package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class GroupVoipInviteAlert$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ GroupVoipInviteAlert f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TLRPC.TL_channels_getParticipants f$3;

    public /* synthetic */ GroupVoipInviteAlert$$ExternalSyntheticLambda0(GroupVoipInviteAlert groupVoipInviteAlert, TLRPC.TL_error tL_error, TLObject tLObject, TLRPC.TL_channels_getParticipants tL_channels_getParticipants) {
        this.f$0 = groupVoipInviteAlert;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = tL_channels_getParticipants;
    }

    public final void run() {
        this.f$0.m4048x919919b(this.f$1, this.f$2, this.f$3);
    }
}
