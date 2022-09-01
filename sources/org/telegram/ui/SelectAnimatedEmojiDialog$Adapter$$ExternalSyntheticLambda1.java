package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.SelectAnimatedEmojiDialog;

public final /* synthetic */ class SelectAnimatedEmojiDialog$Adapter$$ExternalSyntheticLambda1 implements View.OnClickListener {
    public final /* synthetic */ SelectAnimatedEmojiDialog.Adapter f$0;
    public final /* synthetic */ EmojiView.EmojiPack f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ SelectAnimatedEmojiDialog$Adapter$$ExternalSyntheticLambda1(SelectAnimatedEmojiDialog.Adapter adapter, EmojiView.EmojiPack emojiPack, int i) {
        this.f$0 = adapter;
        this.f$1 = emojiPack;
        this.f$2 = i;
    }

    public final void onClick(View view) {
        this.f$0.lambda$onBindViewHolder$1(this.f$1, this.f$2, view);
    }
}
