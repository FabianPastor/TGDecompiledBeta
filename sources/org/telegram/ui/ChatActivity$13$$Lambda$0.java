package org.telegram.ui;

import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.ui.ChatActivity.AnonymousClass13;

final /* synthetic */ class ChatActivity$13$$Lambda$0 implements IntCallback {
    private final AnonymousClass13 arg$1;

    ChatActivity$13$$Lambda$0(AnonymousClass13 anonymousClass13) {
        this.arg$1 = anonymousClass13;
    }

    public void run(int i) {
        this.arg$1.lambda$loadLastUnreadMention$0$ChatActivity$13(i);
    }
}
