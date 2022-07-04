package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatEditTypeActivity$$ExternalSyntheticLambda9 implements RequestDelegate {
    public final /* synthetic */ ChatEditTypeActivity f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ ChatEditTypeActivity$$ExternalSyntheticLambda9(ChatEditTypeActivity chatEditTypeActivity, String str) {
        this.f$0 = chatEditTypeActivity;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3210lambda$checkUserName$16$orgtelegramuiChatEditTypeActivity(this.f$1, tLObject, tL_error);
    }
}
