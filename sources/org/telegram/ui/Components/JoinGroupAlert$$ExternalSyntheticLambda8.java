package org.telegram.ui.Components;

import android.content.Context;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class JoinGroupAlert$$ExternalSyntheticLambda8 implements RequestDelegate {
    public final /* synthetic */ JoinGroupAlert f$0;
    public final /* synthetic */ Context f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ TLRPC.TL_messages_importChatInvite f$3;

    public /* synthetic */ JoinGroupAlert$$ExternalSyntheticLambda8(JoinGroupAlert joinGroupAlert, Context context, boolean z, TLRPC.TL_messages_importChatInvite tL_messages_importChatInvite) {
        this.f$0 = joinGroupAlert;
        this.f$1 = context;
        this.f$2 = z;
        this.f$3 = tL_messages_importChatInvite;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2391lambda$new$4$orgtelegramuiComponentsJoinGroupAlert(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
