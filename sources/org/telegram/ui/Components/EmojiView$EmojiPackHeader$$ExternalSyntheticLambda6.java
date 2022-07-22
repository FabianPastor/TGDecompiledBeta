package org.telegram.ui.Components;

import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.Components.EmojiView;

public final /* synthetic */ class EmojiView$EmojiPackHeader$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ EmojiView.EmojiPackHeader f$0;
    public final /* synthetic */ TLRPC$TL_messages_stickerSet f$1;

    public /* synthetic */ EmojiView$EmojiPackHeader$$ExternalSyntheticLambda6(EmojiView.EmojiPackHeader emojiPackHeader, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        this.f$0 = emojiPackHeader;
        this.f$1 = tLRPC$TL_messages_stickerSet;
    }

    public final void run() {
        this.f$0.lambda$uninstall$6(this.f$1);
    }
}
