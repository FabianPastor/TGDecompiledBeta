package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.Components.EmojiView;

public final /* synthetic */ class EmojiView$EmojiPackHeader$$ExternalSyntheticLambda3 implements View.OnClickListener {
    public final /* synthetic */ EmojiView.EmojiPackHeader f$0;
    public final /* synthetic */ EmojiView.EmojiPack f$1;

    public /* synthetic */ EmojiView$EmojiPackHeader$$ExternalSyntheticLambda3(EmojiView.EmojiPackHeader emojiPackHeader, EmojiView.EmojiPack emojiPack) {
        this.f$0 = emojiPackHeader;
        this.f$1 = emojiPack;
    }

    public final void onClick(View view) {
        this.f$0.lambda$setStickerSet$2(this.f$1, view);
    }
}
