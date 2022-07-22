package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.Components.EmojiView;

public final /* synthetic */ class EmojiView$EmojiGridAdapter$$ExternalSyntheticLambda0 implements View.OnClickListener {
    public final /* synthetic */ EmojiView.EmojiGridAdapter f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ EmojiView.EmojiPack f$2;
    public final /* synthetic */ EmojiView.EmojiPackButton f$3;

    public /* synthetic */ EmojiView$EmojiGridAdapter$$ExternalSyntheticLambda0(EmojiView.EmojiGridAdapter emojiGridAdapter, boolean z, EmojiView.EmojiPack emojiPack, EmojiView.EmojiPackButton emojiPackButton) {
        this.f$0 = emojiGridAdapter;
        this.f$1 = z;
        this.f$2 = emojiPack;
        this.f$3 = emojiPackButton;
    }

    public final void onClick(View view) {
        this.f$0.lambda$onBindViewHolder$4(this.f$1, this.f$2, this.f$3, view);
    }
}
