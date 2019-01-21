package org.telegram.ui;

import org.telegram.messenger.MessagesStorage.IntCallback;

final /* synthetic */ class ChatEditActivity$$Lambda$16 implements IntCallback {
    private final ChatEditActivity arg$1;

    ChatEditActivity$$Lambda$16(ChatEditActivity chatEditActivity) {
        this.arg$1 = chatEditActivity;
    }

    public void run(int i) {
        this.arg$1.lambda$processDone$19$ChatEditActivity(i);
    }
}
