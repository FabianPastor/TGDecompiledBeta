package org.telegram.ui.Components;

import android.widget.TextView;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class StickersAlert$$ExternalSyntheticLambda32 implements RequestDelegate {
    public final /* synthetic */ StickersAlert f$0;
    public final /* synthetic */ EditTextBoldCursor f$1;
    public final /* synthetic */ TextView f$2;
    public final /* synthetic */ TextView f$3;
    public final /* synthetic */ int[] f$4;

    public /* synthetic */ StickersAlert$$ExternalSyntheticLambda32(StickersAlert stickersAlert, EditTextBoldCursor editTextBoldCursor, TextView textView, TextView textView2, int[] iArr) {
        this.f$0 = stickersAlert;
        this.f$1 = editTextBoldCursor;
        this.f$2 = textView;
        this.f$3 = textView2;
        this.f$4 = iArr;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$showNameEnterAlert$27(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
    }
}
