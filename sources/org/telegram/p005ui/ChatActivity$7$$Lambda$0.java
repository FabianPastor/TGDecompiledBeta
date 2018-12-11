package org.telegram.p005ui;

import org.telegram.p005ui.ChatActivity.CLASSNAME;

/* renamed from: org.telegram.ui.ChatActivity$7$$Lambda$0 */
final /* synthetic */ class ChatActivity$7$$Lambda$0 implements Runnable {
    private final CLASSNAME arg$1;
    private final int arg$2;

    ChatActivity$7$$Lambda$0(CLASSNAME CLASSNAME, int i) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = i;
    }

    public void run() {
        this.arg$1.lambda$onMeasure$0$ChatActivity$7(this.arg$2);
    }
}
