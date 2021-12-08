package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.EmojiView;

public final /* synthetic */ class EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ EmojiView.StickersSearchGridAdapter.AnonymousClass1 f$0;
    public final /* synthetic */ TLRPC.TL_messages_searchStickerSets f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda1(EmojiView.StickersSearchGridAdapter.AnonymousClass1 r1, TLRPC.TL_messages_searchStickerSets tL_messages_searchStickerSets, TLObject tLObject) {
        this.f$0 = r1;
        this.f$1 = tL_messages_searchStickerSets;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m2268xebc7bd08(this.f$1, this.f$2);
    }
}
