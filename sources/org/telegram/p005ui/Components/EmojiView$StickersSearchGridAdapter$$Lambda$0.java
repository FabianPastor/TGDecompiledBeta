package org.telegram.p005ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.p005ui.Components.EmojiView.StickersSearchGridAdapter;

/* renamed from: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$$Lambda$0 */
final /* synthetic */ class EmojiView$StickersSearchGridAdapter$$Lambda$0 implements OnClickListener {
    private final StickersSearchGridAdapter arg$1;

    EmojiView$StickersSearchGridAdapter$$Lambda$0(StickersSearchGridAdapter stickersSearchGridAdapter) {
        this.arg$1 = stickersSearchGridAdapter;
    }

    public void onClick(View view) {
        this.arg$1.lambda$onCreateViewHolder$0$EmojiView$StickersSearchGridAdapter(view);
    }
}
