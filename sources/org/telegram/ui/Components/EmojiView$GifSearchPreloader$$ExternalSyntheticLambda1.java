package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.Components.EmojiView;

public final /* synthetic */ class EmojiView$GifSearchPreloader$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ EmojiView.GifSearchPreloader f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ String f$4;

    public /* synthetic */ EmojiView$GifSearchPreloader$$ExternalSyntheticLambda1(EmojiView.GifSearchPreloader gifSearchPreloader, String str, String str2, boolean z, String str3) {
        this.f$0 = gifSearchPreloader;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = z;
        this.f$4 = str3;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$preload$1(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
    }
}
