package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_importChatInvite;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$JoinGroupAlert$mdXFwxXoZ3zIuXYK2Qsi9b4N8JY implements RequestDelegate {
    private final /* synthetic */ JoinGroupAlert f$0;
    private final /* synthetic */ TL_messages_importChatInvite f$1;

    public /* synthetic */ -$$Lambda$JoinGroupAlert$mdXFwxXoZ3zIuXYK2Qsi9b4N8JY(JoinGroupAlert joinGroupAlert, TL_messages_importChatInvite tL_messages_importChatInvite) {
        this.f$0 = joinGroupAlert;
        this.f$1 = tL_messages_importChatInvite;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$2$JoinGroupAlert(this.f$1, tLObject, tL_error);
    }
}
