package org.telegram.ui.Components;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class EmojiPacksAlert$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ TLRPC$TL_messages_stickerSet f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ BaseFragment f$2;
    public final /* synthetic */ Utilities.Callback f$3;
    public final /* synthetic */ Runnable f$4;

    public /* synthetic */ EmojiPacksAlert$$ExternalSyntheticLambda7(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, boolean z, BaseFragment baseFragment, Utilities.Callback callback, Runnable runnable) {
        this.f$0 = tLRPC$TL_messages_stickerSet;
        this.f$1 = z;
        this.f$2 = baseFragment;
        this.f$3 = callback;
        this.f$4 = runnable;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new EmojiPacksAlert$$ExternalSyntheticLambda4(this.f$0, tLRPC$TL_error, this.f$1, this.f$2, tLObject, this.f$3, this.f$4));
    }
}
