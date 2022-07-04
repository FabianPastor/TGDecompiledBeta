package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets;
import org.telegram.ui.GroupStickersActivity;

public final /* synthetic */ class GroupStickersActivity$SearchAdapter$$ExternalSyntheticLambda2 implements RequestDelegate {
    public final /* synthetic */ GroupStickersActivity.SearchAdapter f$0;
    public final /* synthetic */ TLRPC$TL_messages_searchStickerSets f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ GroupStickersActivity$SearchAdapter$$ExternalSyntheticLambda2(GroupStickersActivity.SearchAdapter searchAdapter, TLRPC$TL_messages_searchStickerSets tLRPC$TL_messages_searchStickerSets, String str) {
        this.f$0 = searchAdapter;
        this.f$1 = tLRPC$TL_messages_searchStickerSets;
        this.f$2 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onSearchStickers$1(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
