package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChangeUsernameActivity$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ ChangeUsernameActivity f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ TLRPC.TL_error f$2;
    public final /* synthetic */ TLObject f$3;

    public /* synthetic */ ChangeUsernameActivity$$ExternalSyntheticLambda4(ChangeUsernameActivity changeUsernameActivity, String str, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = changeUsernameActivity;
        this.f$1 = str;
        this.f$2 = tL_error;
        this.f$3 = tLObject;
    }

    public final void run() {
        this.f$0.m2847lambda$checkUserName$2$orgtelegramuiChangeUsernameActivity(this.f$1, this.f$2, this.f$3);
    }
}
