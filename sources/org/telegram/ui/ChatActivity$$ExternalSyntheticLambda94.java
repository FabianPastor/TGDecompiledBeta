package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda94 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ BaseFragment f$1;
    public final /* synthetic */ MessageObject f$2;
    public final /* synthetic */ ActionBarLayout f$3;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda94(ChatActivity chatActivity, BaseFragment baseFragment, MessageObject messageObject, ActionBarLayout actionBarLayout) {
        this.f$0 = chatActivity;
        this.f$1 = baseFragment;
        this.f$2 = messageObject;
        this.f$3 = actionBarLayout;
    }

    public final void run() {
        this.f$0.m1735lambda$migrateToNewChat$124$orgtelegramuiChatActivity(this.f$1, this.f$2, this.f$3);
    }
}
