package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.Components.EmojiView;

public final /* synthetic */ class EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1 f$0;
    public final /* synthetic */ AlertDialog[] f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ BottomSheet.Builder f$3;

    public /* synthetic */ EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda2(EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1 r1, AlertDialog[] alertDialogArr, TLObject tLObject, BottomSheet.Builder builder) {
        this.f$0 = r1;
        this.f$1 = alertDialogArr;
        this.f$2 = tLObject;
        this.f$3 = builder;
    }

    public final void run() {
        this.f$0.lambda$onClick$0(this.f$1, this.f$2, this.f$3);
    }
}
