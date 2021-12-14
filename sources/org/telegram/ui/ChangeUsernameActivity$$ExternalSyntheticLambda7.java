package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChangeUsernameActivity$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ ChangeUsernameActivity f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ ChangeUsernameActivity$$ExternalSyntheticLambda7(ChangeUsernameActivity changeUsernameActivity, String str) {
        this.f$0 = changeUsernameActivity;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$checkUserName$3(this.f$1, tLObject, tLRPC$TL_error);
    }
}
