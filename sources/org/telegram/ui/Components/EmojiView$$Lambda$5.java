package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

final /* synthetic */ class EmojiView$$Lambda$5 implements OnItemLongClickListener {
    private final EmojiView arg$1;

    EmojiView$$Lambda$5(EmojiView emojiView) {
        this.arg$1 = emojiView;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$new$7$EmojiView(view, i);
    }
}
