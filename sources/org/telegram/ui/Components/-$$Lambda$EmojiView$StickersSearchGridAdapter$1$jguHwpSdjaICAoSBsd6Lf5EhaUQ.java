package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_messages_searchStickerSets;
import org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.AnonymousClass1;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$EmojiView$StickersSearchGridAdapter$1$jguHwpSdjaICAoSBsd6Lf5EhaUQ implements Runnable {
    private final /* synthetic */ AnonymousClass1 f$0;
    private final /* synthetic */ TL_messages_searchStickerSets f$1;
    private final /* synthetic */ TLObject f$2;

    public /* synthetic */ -$$Lambda$EmojiView$StickersSearchGridAdapter$1$jguHwpSdjaICAoSBsd6Lf5EhaUQ(AnonymousClass1 anonymousClass1, TL_messages_searchStickerSets tL_messages_searchStickerSets, TLObject tLObject) {
        this.f$0 = anonymousClass1;
        this.f$1 = tL_messages_searchStickerSets;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$null$0$EmojiView$StickersSearchGridAdapter$1(this.f$1, this.f$2);
    }
}
