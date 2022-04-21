package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda54 implements Runnable {
    public final /* synthetic */ PassportActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ PassportActivity$$ExternalSyntheticLambda54(PassportActivity passportActivity, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = passportActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m2743x412ada1(this.f$1, this.f$2);
    }
}
