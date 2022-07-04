package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets;
import org.telegram.ui.Components.EmojiView;

public final /* synthetic */ class EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ EmojiView.StickersSearchGridAdapter.AnonymousClass1 f$0;
    public final /* synthetic */ TLRPC$TL_messages_searchStickerSets f$1;

    public /* synthetic */ EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda3(EmojiView.StickersSearchGridAdapter.AnonymousClass1 r1, TLRPC$TL_messages_searchStickerSets tLRPC$TL_messages_searchStickerSets) {
        this.f$0 = r1;
        this.f$1 = tLRPC$TL_messages_searchStickerSets;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$run$1(this.f$1, tLObject, tLRPC$TL_error);
    }
}
