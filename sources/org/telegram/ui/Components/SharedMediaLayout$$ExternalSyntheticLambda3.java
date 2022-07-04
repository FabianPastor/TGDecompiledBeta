package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SharedMediaLayout$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ SharedMediaLayout f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ SharedMediaLayout$$ExternalSyntheticLambda3(SharedMediaLayout sharedMediaLayout, int i, int i2) {
        this.f$0 = sharedMediaLayout;
        this.f$1 = i;
        this.f$2 = i2;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1378x343dedc(this.f$1, this.f$2, tLObject, tL_error);
    }
}
