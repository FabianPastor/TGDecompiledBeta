package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChangeUsernameActivity$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ ChangeUsernameActivity f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ ChangeUsernameActivity$$ExternalSyntheticLambda7(ChangeUsernameActivity changeUsernameActivity, String str) {
        this.f$0 = changeUsernameActivity;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2848lambda$checkUserName$3$orgtelegramuiChangeUsernameActivity(this.f$1, tLObject, tL_error);
    }
}
