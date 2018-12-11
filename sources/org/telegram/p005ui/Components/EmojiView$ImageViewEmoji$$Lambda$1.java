package org.telegram.p005ui.Components;

import android.view.View;
import android.view.View.OnLongClickListener;
import org.telegram.p005ui.Components.EmojiView.ImageViewEmoji;

/* renamed from: org.telegram.ui.Components.EmojiView$ImageViewEmoji$$Lambda$1 */
final /* synthetic */ class EmojiView$ImageViewEmoji$$Lambda$1 implements OnLongClickListener {
    private final ImageViewEmoji arg$1;

    EmojiView$ImageViewEmoji$$Lambda$1(ImageViewEmoji imageViewEmoji) {
        this.arg$1 = imageViewEmoji;
    }

    public boolean onLongClick(View view) {
        return this.arg$1.lambda$new$1$EmojiView$ImageViewEmoji(view);
    }
}
