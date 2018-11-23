package org.telegram.p005ui;

import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.p005ui.ChatActivity.C056914;

/* renamed from: org.telegram.ui.ChatActivity$14$$Lambda$0 */
final /* synthetic */ class ChatActivity$14$$Lambda$0 implements IntCallback {
    private final C056914 arg$1;

    ChatActivity$14$$Lambda$0(C056914 c056914) {
        this.arg$1 = c056914;
    }

    public void run(int i) {
        this.arg$1.lambda$loadLastUnreadMention$0$ChatActivity$14(i);
    }
}
