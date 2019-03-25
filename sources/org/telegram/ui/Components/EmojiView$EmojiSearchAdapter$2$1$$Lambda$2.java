package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.AnonymousClass1;

final /* synthetic */ class EmojiView$EmojiSearchAdapter$2$1$$Lambda$2 implements OnCancelListener {
    private final AnonymousClass1 arg$1;
    private final int arg$2;

    EmojiView$EmojiSearchAdapter$2$1$$Lambda$2(AnonymousClass1 anonymousClass1, int i) {
        this.arg$1 = anonymousClass1;
        this.arg$2 = i;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.arg$1.lambda$null$2$EmojiView$EmojiSearchAdapter$2$1(this.arg$2, dialogInterface);
    }
}
