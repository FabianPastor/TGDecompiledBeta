package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.Cells.StickerSetNameCell;
import org.telegram.ui.Components.EmojiView;

public final /* synthetic */ class EmojiView$StickersGridAdapter$$ExternalSyntheticLambda3 implements View.OnClickListener {
    public final /* synthetic */ EmojiView.StickersGridAdapter f$0;
    public final /* synthetic */ StickerSetNameCell f$1;

    public /* synthetic */ EmojiView$StickersGridAdapter$$ExternalSyntheticLambda3(EmojiView.StickersGridAdapter stickersGridAdapter, StickerSetNameCell stickerSetNameCell) {
        this.f$0 = stickersGridAdapter;
        this.f$1 = stickerSetNameCell;
    }

    public final void onClick(View view) {
        this.f$0.lambda$onCreateViewHolder$1(this.f$1, view);
    }
}
