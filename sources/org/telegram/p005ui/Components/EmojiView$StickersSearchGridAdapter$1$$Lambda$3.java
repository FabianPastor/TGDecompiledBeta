package org.telegram.p005ui.Components;

import org.telegram.p005ui.Components.EmojiView.StickersSearchGridAdapter.C07041;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_messages_searchStickerSets;

/* renamed from: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1$$Lambda$3 */
final /* synthetic */ class EmojiView$StickersSearchGridAdapter$1$$Lambda$3 implements Runnable {
    private final C07041 arg$1;
    private final TL_messages_searchStickerSets arg$2;
    private final TLObject arg$3;

    EmojiView$StickersSearchGridAdapter$1$$Lambda$3(C07041 c07041, TL_messages_searchStickerSets tL_messages_searchStickerSets, TLObject tLObject) {
        this.arg$1 = c07041;
        this.arg$2 = tL_messages_searchStickerSets;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$0$EmojiView$StickersSearchGridAdapter$1(this.arg$2, this.arg$3);
    }
}
