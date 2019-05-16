package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_searchStickerSets;
import org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.AnonymousClass1;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$EmojiView$StickersSearchGridAdapter$1$PMXaBA9vcwjG4TQSEF8AuHh_dEU implements RequestDelegate {
    private final /* synthetic */ AnonymousClass1 f$0;
    private final /* synthetic */ TL_messages_searchStickerSets f$1;

    public /* synthetic */ -$$Lambda$EmojiView$StickersSearchGridAdapter$1$PMXaBA9vcwjG4TQSEF8AuHh_dEU(AnonymousClass1 anonymousClass1, TL_messages_searchStickerSets tL_messages_searchStickerSets) {
        this.f$0 = anonymousClass1;
        this.f$1 = tL_messages_searchStickerSets;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$run$1$EmojiView$StickersSearchGridAdapter$1(this.f$1, tLObject, tL_error);
    }
}
