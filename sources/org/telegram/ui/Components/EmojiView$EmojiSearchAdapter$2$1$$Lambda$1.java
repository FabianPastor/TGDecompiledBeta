package org.telegram.ui.Components;

import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.AnonymousClass1;

final /* synthetic */ class EmojiView$EmojiSearchAdapter$2$1$$Lambda$1 implements Runnable {
    private final AnonymousClass1 arg$1;
    private final AlertDialog[] arg$2;
    private final int arg$3;

    EmojiView$EmojiSearchAdapter$2$1$$Lambda$1(AnonymousClass1 anonymousClass1, AlertDialog[] alertDialogArr, int i) {
        this.arg$1 = anonymousClass1;
        this.arg$2 = alertDialogArr;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$onClick$3$EmojiView$EmojiSearchAdapter$2$1(this.arg$2, this.arg$3);
    }
}
