package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.AnonymousClass1;

final /* synthetic */ class EmojiView$EmojiSearchAdapter$2$1$$Lambda$3 implements Runnable {
    private final AnonymousClass1 arg$1;
    private final AlertDialog[] arg$2;
    private final TLObject arg$3;
    private final Builder arg$4;

    EmojiView$EmojiSearchAdapter$2$1$$Lambda$3(AnonymousClass1 anonymousClass1, AlertDialog[] alertDialogArr, TLObject tLObject, Builder builder) {
        this.arg$1 = anonymousClass1;
        this.arg$2 = alertDialogArr;
        this.arg$3 = tLObject;
        this.arg$4 = builder;
    }

    public void run() {
        this.arg$1.lambda$null$0$EmojiView$EmojiSearchAdapter$2$1(this.arg$2, this.arg$3, this.arg$4);
    }
}
