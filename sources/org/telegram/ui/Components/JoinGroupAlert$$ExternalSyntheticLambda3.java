package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class JoinGroupAlert$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ JoinGroupAlert f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ TLRPC.TL_messages_importChatInvite f$2;

    public /* synthetic */ JoinGroupAlert$$ExternalSyntheticLambda3(JoinGroupAlert joinGroupAlert, boolean z, TLRPC.TL_messages_importChatInvite tL_messages_importChatInvite) {
        this.f$0 = joinGroupAlert;
        this.f$1 = z;
        this.f$2 = tL_messages_importChatInvite;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1084lambda$new$6$orgtelegramuiComponentsJoinGroupAlert(this.f$1, this.f$2, tLObject, tL_error);
    }
}
