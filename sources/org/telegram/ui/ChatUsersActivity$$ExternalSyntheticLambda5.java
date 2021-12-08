package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatUsersActivity$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ ChatUsersActivity f$0;
    public final /* synthetic */ TLRPC.TL_channels_getParticipants f$1;

    public /* synthetic */ ChatUsersActivity$$ExternalSyntheticLambda5(ChatUsersActivity chatUsersActivity, TLRPC.TL_channels_getParticipants tL_channels_getParticipants) {
        this.f$0 = chatUsersActivity;
        this.f$1 = tL_channels_getParticipants;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1971lambda$loadChatParticipants$15$orgtelegramuiChatUsersActivity(this.f$1, tLObject, tL_error);
    }
}
