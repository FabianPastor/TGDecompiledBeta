package org.telegram.p005ui;

import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.p005ui.ChatActivity.CLASSNAME;

/* renamed from: org.telegram.ui.ChatActivity$13$$Lambda$0 */
final /* synthetic */ class ChatActivity$13$$Lambda$0 implements IntCallback {
    private final CLASSNAME arg$1;

    ChatActivity$13$$Lambda$0(CLASSNAME CLASSNAME) {
        this.arg$1 = CLASSNAME;
    }

    public void run(int i) {
        this.arg$1.lambda$loadLastUnreadMention$0$ChatActivity$13(i);
    }
}
