package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatEditTypeActivity$$ExternalSyntheticLambda10 implements RequestDelegate {
    public final /* synthetic */ ChatEditTypeActivity f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ ChatEditTypeActivity$$ExternalSyntheticLambda10(ChatEditTypeActivity chatEditTypeActivity, boolean z) {
        this.f$0 = chatEditTypeActivity;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3217lambda$generateLink$19$orgtelegramuiChatEditTypeActivity(this.f$1, tLObject, tL_error);
    }
}
