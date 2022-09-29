package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_account_emojiStatuses;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda32 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC$TL_account_emojiStatuses f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda32(MediaDataController mediaDataController, int i, TLRPC$TL_account_emojiStatuses tLRPC$TL_account_emojiStatuses) {
        this.f$0 = mediaDataController;
        this.f$1 = i;
        this.f$2 = tLRPC$TL_account_emojiStatuses;
    }

    public final void run() {
        this.f$0.lambda$updateEmojiStatuses$205(this.f$1, this.f$2);
    }
}
