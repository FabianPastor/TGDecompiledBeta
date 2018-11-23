package org.telegram.p005ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.p005ui.Components.EmojiView.StickersGridAdapter;

/* renamed from: org.telegram.ui.Components.EmojiView$StickersGridAdapter$$Lambda$0 */
final /* synthetic */ class EmojiView$StickersGridAdapter$$Lambda$0 implements OnClickListener {
    private final StickersGridAdapter arg$1;

    EmojiView$StickersGridAdapter$$Lambda$0(StickersGridAdapter stickersGridAdapter) {
        this.arg$1 = stickersGridAdapter;
    }

    public void onClick(View view) {
        this.arg$1.lambda$onCreateViewHolder$0$EmojiView$StickersGridAdapter(view);
    }
}
