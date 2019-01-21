package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class ChatActivity$$Lambda$41 implements Runnable {
    private final ChatActivity arg$1;
    private final BaseFragment arg$2;
    private final MessageObject arg$3;
    private final ActionBarLayout arg$4;

    ChatActivity$$Lambda$41(ChatActivity chatActivity, BaseFragment baseFragment, MessageObject messageObject, ActionBarLayout actionBarLayout) {
        this.arg$1 = chatActivity;
        this.arg$2 = baseFragment;
        this.arg$3 = messageObject;
        this.arg$4 = actionBarLayout;
    }

    public void run() {
        this.arg$1.lambda$migrateToNewChat$52$ChatActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
