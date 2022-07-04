package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatEditTypeActivity$$ExternalSyntheticLambda18 implements RequestDelegate {
    public final /* synthetic */ ChatEditTypeActivity f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ ChatEditTypeActivity$$ExternalSyntheticLambda18(ChatEditTypeActivity chatEditTypeActivity, String str) {
        this.f$0 = chatEditTypeActivity;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$checkUserName$16(this.f$1, tLObject, tLRPC$TL_error);
    }
}
