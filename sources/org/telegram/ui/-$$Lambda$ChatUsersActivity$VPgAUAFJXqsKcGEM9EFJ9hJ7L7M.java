package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatUsersActivity$VPgAUAFJXqsKcGEM9EFJ9hJ7L7M implements RequestDelegate {
    private final /* synthetic */ ChatUsersActivity f$0;
    private final /* synthetic */ TL_channels_getParticipants f$1;

    public /* synthetic */ -$$Lambda$ChatUsersActivity$VPgAUAFJXqsKcGEM9EFJ9hJ7L7M(ChatUsersActivity chatUsersActivity, TL_channels_getParticipants tL_channels_getParticipants) {
        this.f$0 = chatUsersActivity;
        this.f$1 = tL_channels_getParticipants;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadChatParticipants$21$ChatUsersActivity(this.f$1, tLObject, tL_error);
    }
}
