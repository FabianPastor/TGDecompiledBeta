package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_importChatInvite;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$JoinGroupAlert$IWx-squ4_I5Qyjo-N06x9CUQNvc implements Runnable {
    private final /* synthetic */ JoinGroupAlert f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ TL_messages_importChatInvite f$3;

    public /* synthetic */ -$$Lambda$JoinGroupAlert$IWx-squ4_I5Qyjo-N06x9CUQNvc(JoinGroupAlert joinGroupAlert, TL_error tL_error, TLObject tLObject, TL_messages_importChatInvite tL_messages_importChatInvite) {
        this.f$0 = joinGroupAlert;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = tL_messages_importChatInvite;
    }

    public final void run() {
        this.f$0.lambda$null$1$JoinGroupAlert(this.f$1, this.f$2, this.f$3);
    }
}
