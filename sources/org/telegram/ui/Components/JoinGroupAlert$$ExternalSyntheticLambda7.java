package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class JoinGroupAlert$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ JoinGroupAlert f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TLRPC.TL_messages_importChatInvite f$3;

    public /* synthetic */ JoinGroupAlert$$ExternalSyntheticLambda7(JoinGroupAlert joinGroupAlert, TLRPC.TL_error tL_error, TLObject tLObject, TLRPC.TL_messages_importChatInvite tL_messages_importChatInvite) {
        this.f$0 = joinGroupAlert;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = tL_messages_importChatInvite;
    }

    public final void run() {
        this.f$0.m2394lambda$new$7$orgtelegramuiComponentsJoinGroupAlert(this.f$1, this.f$2, this.f$3);
    }
}
