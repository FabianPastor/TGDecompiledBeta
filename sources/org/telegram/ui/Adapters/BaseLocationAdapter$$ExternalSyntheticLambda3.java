package org.telegram.ui.Adapters;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class BaseLocationAdapter$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ BaseLocationAdapter f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ TLObject f$3;

    public /* synthetic */ BaseLocationAdapter$$ExternalSyntheticLambda3(BaseLocationAdapter baseLocationAdapter, TLRPC.TL_error tL_error, String str, TLObject tLObject) {
        this.f$0 = baseLocationAdapter;
        this.f$1 = tL_error;
        this.f$2 = str;
        this.f$3 = tLObject;
    }

    public final void run() {
        this.f$0.m2597x1e458db9(this.f$1, this.f$2, this.f$3);
    }
}
