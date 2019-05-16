package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.AnonymousClass1;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$EmojiView$EmojiSearchAdapter$2$1$vESTLZoOoKr-laMmjTYW6gGhW9U implements RequestDelegate {
    private final /* synthetic */ AnonymousClass1 f$0;
    private final /* synthetic */ AlertDialog[] f$1;
    private final /* synthetic */ Builder f$2;

    public /* synthetic */ -$$Lambda$EmojiView$EmojiSearchAdapter$2$1$vESTLZoOoKr-laMmjTYW6gGhW9U(AnonymousClass1 anonymousClass1, AlertDialog[] alertDialogArr, Builder builder) {
        this.f$0 = anonymousClass1;
        this.f$1 = alertDialogArr;
        this.f$2 = builder;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$onClick$1$EmojiView$EmojiSearchAdapter$2$1(this.f$1, this.f$2, tLObject, tL_error);
    }
}
