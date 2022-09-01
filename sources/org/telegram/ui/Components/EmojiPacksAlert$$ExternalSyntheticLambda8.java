package org.telegram.ui.Components;

import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class EmojiPacksAlert$$ExternalSyntheticLambda8 implements RequestDelegate {
    public final /* synthetic */ TLRPC$StickerSet f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ View f$2;
    public final /* synthetic */ BaseFragment f$3;
    public final /* synthetic */ TLRPC$TL_messages_stickerSet f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ Utilities.Callback f$6;
    public final /* synthetic */ Runnable f$7;

    public /* synthetic */ EmojiPacksAlert$$ExternalSyntheticLambda8(TLRPC$StickerSet tLRPC$StickerSet, boolean z, View view, BaseFragment baseFragment, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, int i, Utilities.Callback callback, Runnable runnable) {
        this.f$0 = tLRPC$StickerSet;
        this.f$1 = z;
        this.f$2 = view;
        this.f$3 = baseFragment;
        this.f$4 = tLRPC$TL_messages_stickerSet;
        this.f$5 = i;
        this.f$6 = callback;
        this.f$7 = runnable;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new EmojiPacksAlert$$ExternalSyntheticLambda5(this.f$0, tLRPC$TL_error, this.f$1, this.f$2, this.f$3, this.f$4, tLObject, this.f$5, this.f$6, this.f$7));
    }
}
