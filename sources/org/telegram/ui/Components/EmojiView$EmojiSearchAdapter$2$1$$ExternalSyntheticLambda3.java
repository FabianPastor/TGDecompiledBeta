package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.Components.EmojiView;

public final /* synthetic */ class EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1 f$0;
    public final /* synthetic */ AlertDialog[] f$1;
    public final /* synthetic */ BottomSheet.Builder f$2;

    public /* synthetic */ EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda3(EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1 r1, AlertDialog[] alertDialogArr, BottomSheet.Builder builder) {
        this.f$0 = r1;
        this.f$1 = alertDialogArr;
        this.f$2 = builder;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2253x6d790acb(this.f$1, this.f$2, tLObject, tL_error);
    }
}
