package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class JoinGroupAlert$$ExternalSyntheticLambda9 implements RequestDelegate {
    public final /* synthetic */ JoinGroupAlert f$0;
    public final /* synthetic */ TLRPC.TL_messages_importChatInvite f$1;

    public /* synthetic */ JoinGroupAlert$$ExternalSyntheticLambda9(JoinGroupAlert joinGroupAlert, TLRPC.TL_messages_importChatInvite tL_messages_importChatInvite) {
        this.f$0 = joinGroupAlert;
        this.f$1 = tL_messages_importChatInvite;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4116lambda$new$8$orgtelegramuiComponentsJoinGroupAlert(this.f$1, tLObject, tL_error);
    }
}
