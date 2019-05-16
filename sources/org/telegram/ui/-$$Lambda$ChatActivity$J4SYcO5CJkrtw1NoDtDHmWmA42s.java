package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$J4SYcO5CJkrtw1NoDtDHmWmA42s implements Runnable {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ BaseFragment f$1;
    private final /* synthetic */ MessageObject f$2;
    private final /* synthetic */ ActionBarLayout f$3;

    public /* synthetic */ -$$Lambda$ChatActivity$J4SYcO5CJkrtw1NoDtDHmWmA42s(ChatActivity chatActivity, BaseFragment baseFragment, MessageObject messageObject, ActionBarLayout actionBarLayout) {
        this.f$0 = chatActivity;
        this.f$1 = baseFragment;
        this.f$2 = messageObject;
        this.f$3 = actionBarLayout;
    }

    public final void run() {
        this.f$0.lambda$migrateToNewChat$56$ChatActivity(this.f$1, this.f$2, this.f$3);
    }
}
