package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatUsersActivity$U2a642fHVniMc_2qJcp8WAu1rnM implements Runnable {
    private final /* synthetic */ ChatUsersActivity f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ TL_channels_getParticipants f$3;

    public /* synthetic */ -$$Lambda$ChatUsersActivity$U2a642fHVniMc_2qJcp8WAu1rnM(ChatUsersActivity chatUsersActivity, TL_error tL_error, TLObject tLObject, TL_channels_getParticipants tL_channels_getParticipants) {
        this.f$0 = chatUsersActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = tL_channels_getParticipants;
    }

    public final void run() {
        this.f$0.lambda$null$16$ChatUsersActivity(this.f$1, this.f$2, this.f$3);
    }
}
