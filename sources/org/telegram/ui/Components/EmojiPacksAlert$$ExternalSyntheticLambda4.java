package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class EmojiPacksAlert$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ TLRPC$TL_messages_stickerSet f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ BaseFragment f$3;
    public final /* synthetic */ TLObject f$4;
    public final /* synthetic */ Runnable f$5;

    public /* synthetic */ EmojiPacksAlert$$ExternalSyntheticLambda4(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, TLRPC$TL_error tLRPC$TL_error, boolean z, BaseFragment baseFragment, TLObject tLObject, Runnable runnable) {
        this.f$0 = tLRPC$TL_messages_stickerSet;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = z;
        this.f$3 = baseFragment;
        this.f$4 = tLObject;
        this.f$5 = runnable;
    }

    public final void run() {
        EmojiPacksAlert.lambda$installSet$2(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
