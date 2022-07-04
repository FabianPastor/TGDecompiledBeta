package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SharedMediaLayout$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ SharedMediaLayout f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ TLObject f$4;

    public /* synthetic */ SharedMediaLayout$$ExternalSyntheticLambda1(SharedMediaLayout sharedMediaLayout, TLRPC.TL_error tL_error, int i, int i2, TLObject tLObject) {
        this.f$0 = sharedMediaLayout;
        this.f$1 = tL_error;
        this.f$2 = i;
        this.f$3 = i2;
        this.f$4 = tLObject;
    }

    public final void run() {
        this.f$0.m1377x49cCLASSNAMEd(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
