package org.telegram.ui.Components;

import android.view.View;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class EmojiPacksAlert$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ TLRPC$StickerSet f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ View f$3;
    public final /* synthetic */ BaseFragment f$4;
    public final /* synthetic */ TLRPC$TL_messages_stickerSet f$5;
    public final /* synthetic */ TLObject f$6;
    public final /* synthetic */ int f$7;
    public final /* synthetic */ Utilities.Callback f$8;
    public final /* synthetic */ Runnable f$9;

    public /* synthetic */ EmojiPacksAlert$$ExternalSyntheticLambda5(TLRPC$StickerSet tLRPC$StickerSet, TLRPC$TL_error tLRPC$TL_error, boolean z, View view, BaseFragment baseFragment, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, TLObject tLObject, int i, Utilities.Callback callback, Runnable runnable) {
        this.f$0 = tLRPC$StickerSet;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = z;
        this.f$3 = view;
        this.f$4 = baseFragment;
        this.f$5 = tLRPC$TL_messages_stickerSet;
        this.f$6 = tLObject;
        this.f$7 = i;
        this.f$8 = callback;
        this.f$9 = runnable;
    }

    public final void run() {
        EmojiPacksAlert.lambda$installSet$5(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
    }
}
