package org.telegram.ui.Components;

import android.widget.TextView;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class StickersAlert$$ExternalSyntheticLambda20 implements RequestDelegate {
    public final /* synthetic */ StickersAlert f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ TextView f$2;

    public /* synthetic */ StickersAlert$$ExternalSyntheticLambda20(StickersAlert stickersAlert, String str, TextView textView) {
        this.f$0 = stickersAlert;
        this.f$1 = str;
        this.f$2 = textView;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2631xb15ab52d(this.f$1, this.f$2, tLObject, tL_error);
    }
}
