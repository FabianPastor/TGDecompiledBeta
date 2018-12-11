package org.telegram.p005ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.Document;

/* renamed from: org.telegram.ui.Components.EmojiView$$Lambda$10 */
final /* synthetic */ class EmojiView$$Lambda$10 implements OnClickListener {
    private final EmojiView arg$1;
    private final Document arg$2;

    EmojiView$$Lambda$10(EmojiView emojiView, Document document) {
        this.arg$1 = emojiView;
        this.arg$2 = document;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$6$EmojiView(this.arg$2, dialogInterface, i);
    }
}
