package org.telegram.ui;

import org.telegram.ui.ChatActivity.AnonymousClass5;

final /* synthetic */ class ChatActivity$5$$Lambda$1 implements Runnable {
    private final AnonymousClass5 arg$1;
    private final int arg$2;
    private final boolean arg$3;
    private final boolean arg$4;

    ChatActivity$5$$Lambda$1(AnonymousClass5 anonymousClass5, int i, boolean z, boolean z2) {
        this.arg$1 = anonymousClass5;
        this.arg$2 = i;
        this.arg$3 = z;
        this.arg$4 = z2;
    }

    public void run() {
        this.arg$1.lambda$null$0$ChatActivity$5(this.arg$2, this.arg$3, this.arg$4);
    }
}
