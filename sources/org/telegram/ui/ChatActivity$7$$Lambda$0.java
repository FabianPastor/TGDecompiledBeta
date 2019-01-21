package org.telegram.ui;

import org.telegram.ui.ChatActivity.AnonymousClass7;

final /* synthetic */ class ChatActivity$7$$Lambda$0 implements Runnable {
    private final AnonymousClass7 arg$1;
    private final int arg$2;

    ChatActivity$7$$Lambda$0(AnonymousClass7 anonymousClass7, int i) {
        this.arg$1 = anonymousClass7;
        this.arg$2 = i;
    }

    public void run() {
        this.arg$1.lambda$onMeasure$0$ChatActivity$7(this.arg$2);
    }
}
