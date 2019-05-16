package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatUsersActivity$Y--8f3xmJeRa38Ol4oqrzxeP_cE implements RequestDelegate {
    private final /* synthetic */ ChatUsersActivity f$0;
    private final /* synthetic */ TL_channels_getParticipants f$1;

    public /* synthetic */ -$$Lambda$ChatUsersActivity$Y--8f3xmJeRa38Ol4oqrzxeP_cE(ChatUsersActivity chatUsersActivity, TL_channels_getParticipants tL_channels_getParticipants) {
        this.f$0 = chatUsersActivity;
        this.f$1 = tL_channels_getParticipants;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadChatParticipants$22$ChatUsersActivity(this.f$1, tLObject, tL_error);
    }
}
