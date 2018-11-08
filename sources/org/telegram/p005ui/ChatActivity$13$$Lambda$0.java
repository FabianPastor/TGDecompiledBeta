package org.telegram.p005ui;

import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.p005ui.ChatActivity.C094413;

/* renamed from: org.telegram.ui.ChatActivity$13$$Lambda$0 */
final /* synthetic */ class ChatActivity$13$$Lambda$0 implements IntCallback {
    private final C094413 arg$1;

    ChatActivity$13$$Lambda$0(C094413 c094413) {
        this.arg$1 = c094413;
    }

    public void run(int i) {
        this.arg$1.lambda$loadLastUnreadMention$0$ChatActivity$13(i);
    }
}
