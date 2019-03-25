package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.AnonymousClass1;

final /* synthetic */ class EmojiView$EmojiSearchAdapter$2$1$$Lambda$0 implements RequestDelegate {
    private final AnonymousClass1 arg$1;
    private final AlertDialog[] arg$2;
    private final Builder arg$3;

    EmojiView$EmojiSearchAdapter$2$1$$Lambda$0(AnonymousClass1 anonymousClass1, AlertDialog[] alertDialogArr, Builder builder) {
        this.arg$1 = anonymousClass1;
        this.arg$2 = alertDialogArr;
        this.arg$3 = builder;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$onClick$1$EmojiView$EmojiSearchAdapter$2$1(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
