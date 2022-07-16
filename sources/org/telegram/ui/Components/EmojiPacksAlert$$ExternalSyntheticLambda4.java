package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class EmojiPacksAlert$$ExternalSyntheticLambda4 implements RequestDelegate {
    public final /* synthetic */ EmojiPacksAlert f$0;
    public final /* synthetic */ TLRPC$TL_messages_stickerSet f$1;
    public final /* synthetic */ BaseFragment f$2;

    public /* synthetic */ EmojiPacksAlert$$ExternalSyntheticLambda4(EmojiPacksAlert emojiPacksAlert, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, BaseFragment baseFragment) {
        this.f$0 = emojiPacksAlert;
        this.f$1 = tLRPC$TL_messages_stickerSet;
        this.f$2 = baseFragment;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$installSet$1(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
