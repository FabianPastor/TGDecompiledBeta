package org.telegram.ui;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.PhonebookSelectActivity.PhonebookSelectActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$Uzy9GRHh4_9QiCM66VVA1Ftqfj0 implements PhonebookSelectActivityDelegate {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ ChatActivity f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ long f$3;

    public /* synthetic */ -$$Lambda$LaunchActivity$Uzy9GRHh4_9QiCM66VVA1Ftqfj0(LaunchActivity launchActivity, ChatActivity chatActivity, int i, long j) {
        this.f$0 = launchActivity;
        this.f$1 = chatActivity;
        this.f$2 = i;
        this.f$3 = j;
    }

    public final void didSelectContact(User user) {
        this.f$0.lambda$didSelectDialogs$36$LaunchActivity(this.f$1, this.f$2, this.f$3, user);
    }
}
