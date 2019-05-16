package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProfileActivity$0ctq_HMwIlt8ZVK21U6qfPQkrgk implements RequestDelegate {
    private final /* synthetic */ ProfileActivity f$0;
    private final /* synthetic */ TL_channels_getParticipants f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$ProfileActivity$0ctq_HMwIlt8ZVK21U6qfPQkrgk(ProfileActivity profileActivity, TL_channels_getParticipants tL_channels_getParticipants, int i) {
        this.f$0 = profileActivity;
        this.f$1 = tL_channels_getParticipants;
        this.f$2 = i;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$getChannelParticipants$19$ProfileActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
