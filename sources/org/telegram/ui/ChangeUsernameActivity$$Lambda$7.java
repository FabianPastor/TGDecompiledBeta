package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ChangeUsernameActivity$$Lambda$7 implements RequestDelegate {
    private final ChangeUsernameActivity arg$1;
    private final String arg$2;

    ChangeUsernameActivity$$Lambda$7(ChangeUsernameActivity changeUsernameActivity, String str) {
        this.arg$1 = changeUsernameActivity;
        this.arg$2 = str;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$3$ChangeUsernameActivity(this.arg$2, tLObject, tL_error);
    }
}
