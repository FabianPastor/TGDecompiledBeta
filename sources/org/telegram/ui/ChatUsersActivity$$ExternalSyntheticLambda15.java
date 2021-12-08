package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatUsersActivity$$ExternalSyntheticLambda15 implements Runnable {
    public final /* synthetic */ ChatUsersActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TLRPC.TL_channels_getParticipants f$3;

    public /* synthetic */ ChatUsersActivity$$ExternalSyntheticLambda15(ChatUsersActivity chatUsersActivity, TLRPC.TL_error tL_error, TLObject tLObject, TLRPC.TL_channels_getParticipants tL_channels_getParticipants) {
        this.f$0 = chatUsersActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = tL_channels_getParticipants;
    }

    public final void run() {
        this.f$0.m1970lambda$loadChatParticipants$14$orgtelegramuiChatUsersActivity(this.f$1, this.f$2, this.f$3);
    }
}
