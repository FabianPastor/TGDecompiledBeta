package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.Components.EmojiView;

public final /* synthetic */ class EmojiView$GifAdapter$$ExternalSyntheticLambda4 implements RequestDelegate {
    public final /* synthetic */ EmojiView.GifAdapter f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ String f$6;

    public /* synthetic */ EmojiView$GifAdapter$$ExternalSyntheticLambda4(EmojiView.GifAdapter gifAdapter, String str, String str2, boolean z, boolean z2, boolean z3, String str3) {
        this.f$0 = gifAdapter;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = z;
        this.f$4 = z2;
        this.f$5 = z3;
        this.f$6 = str3;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$search$4(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tLRPC$TL_error);
    }
}
