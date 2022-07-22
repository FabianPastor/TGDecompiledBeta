package org.telegram.ui.Components;

import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.Components.EmojiView;

public final /* synthetic */ class EmojiView$EmojiGridAdapter$$ExternalSyntheticLambda3 implements Utilities.Callback {
    public final /* synthetic */ EmojiView.EmojiGridAdapter f$0;
    public final /* synthetic */ EmojiView.EmojiPackButton f$1;

    public /* synthetic */ EmojiView$EmojiGridAdapter$$ExternalSyntheticLambda3(EmojiView.EmojiGridAdapter emojiGridAdapter, EmojiView.EmojiPackButton emojiPackButton) {
        this.f$0 = emojiGridAdapter;
        this.f$1 = emojiPackButton;
    }

    public final void run(Object obj) {
        this.f$0.lambda$onBindViewHolder$2(this.f$1, (TLRPC$TL_messages_stickerSet) obj);
    }
}
