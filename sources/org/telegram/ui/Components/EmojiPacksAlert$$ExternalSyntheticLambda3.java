package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class EmojiPacksAlert$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ EmojiPacksAlert f$0;
    public final /* synthetic */ TLRPC$TL_messages_stickerSet f$1;
    public final /* synthetic */ TLRPC$TL_error f$2;
    public final /* synthetic */ BaseFragment f$3;
    public final /* synthetic */ TLObject f$4;

    public /* synthetic */ EmojiPacksAlert$$ExternalSyntheticLambda3(EmojiPacksAlert emojiPacksAlert, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLObject tLObject) {
        this.f$0 = emojiPacksAlert;
        this.f$1 = tLRPC$TL_messages_stickerSet;
        this.f$2 = tLRPC$TL_error;
        this.f$3 = baseFragment;
        this.f$4 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$installSet$0(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
