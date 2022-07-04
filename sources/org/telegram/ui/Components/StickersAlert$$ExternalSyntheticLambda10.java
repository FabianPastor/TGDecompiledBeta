package org.telegram.ui.Components;

import android.widget.TextView;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class StickersAlert$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ StickersAlert f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ TLRPC.TL_error f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ TextView f$4;

    public /* synthetic */ StickersAlert$$ExternalSyntheticLambda10(StickersAlert stickersAlert, String str, TLRPC.TL_error tL_error, TLObject tLObject, TextView textView) {
        this.f$0 = stickersAlert;
        this.f$1 = str;
        this.f$2 = tL_error;
        this.f$3 = tLObject;
        this.f$4 = textView;
    }

    public final void run() {
        this.f$0.m1434xb15ab52d(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
