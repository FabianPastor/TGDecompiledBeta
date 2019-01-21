package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_searchStickerSets;
import org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.AnonymousClass1;

final /* synthetic */ class EmojiView$StickersSearchGridAdapter$1$$Lambda$0 implements RequestDelegate {
    private final AnonymousClass1 arg$1;
    private final TL_messages_searchStickerSets arg$2;

    EmojiView$StickersSearchGridAdapter$1$$Lambda$0(AnonymousClass1 anonymousClass1, TL_messages_searchStickerSets tL_messages_searchStickerSets) {
        this.arg$1 = anonymousClass1;
        this.arg$2 = tL_messages_searchStickerSets;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$run$1$EmojiView$StickersSearchGridAdapter$1(this.arg$2, tLObject, tL_error);
    }
}
