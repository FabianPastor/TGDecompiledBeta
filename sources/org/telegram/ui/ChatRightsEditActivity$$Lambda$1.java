package org.telegram.ui;

import org.telegram.messenger.MessagesStorage.IntCallback;

final /* synthetic */ class ChatRightsEditActivity$$Lambda$1 implements IntCallback {
    private final ChatRightsEditActivity arg$1;

    ChatRightsEditActivity$$Lambda$1(ChatRightsEditActivity chatRightsEditActivity) {
        this.arg$1 = chatRightsEditActivity;
    }

    public void run(int i) {
        this.arg$1.lambda$onDonePressed$7$ChatRightsEditActivity(i);
    }
}
