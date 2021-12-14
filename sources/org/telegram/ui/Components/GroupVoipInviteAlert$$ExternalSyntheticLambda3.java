package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class GroupVoipInviteAlert$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ GroupVoipInviteAlert f$0;
    public final /* synthetic */ TLRPC$TL_channels_getParticipants f$1;

    public /* synthetic */ GroupVoipInviteAlert$$ExternalSyntheticLambda3(GroupVoipInviteAlert groupVoipInviteAlert, TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants) {
        this.f$0 = groupVoipInviteAlert;
        this.f$1 = tLRPC$TL_channels_getParticipants;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadChatParticipants$4(this.f$1, tLObject, tLRPC$TL_error);
    }
}
