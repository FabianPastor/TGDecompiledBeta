package org.telegram.ui;

import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.ui.ChatActivity.AnonymousClass14;

final /* synthetic */ class ChatActivity$14$$Lambda$0 implements IntCallback {
    private final AnonymousClass14 arg$1;

    ChatActivity$14$$Lambda$0(AnonymousClass14 anonymousClass14) {
        this.arg$1 = anonymousClass14;
    }

    public void run(int i) {
        this.arg$1.lambda$loadLastUnreadMention$0$ChatActivity$14(i);
    }
}
