package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.GroupCreateActivity.ContactsAddActivityDelegate;
import org.telegram.ui.GroupCreateActivity.ContactsAddActivityDelegate.-CC;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProfileActivity$00s-Q5Ms7AV0v3XsAsD6561decA implements ContactsAddActivityDelegate {
    private final /* synthetic */ ProfileActivity f$0;

    public /* synthetic */ -$$Lambda$ProfileActivity$00s-Q5Ms7AV0v3XsAsD6561decA(ProfileActivity profileActivity) {
        this.f$0 = profileActivity;
    }

    public final void didSelectUsers(ArrayList arrayList, int i) {
        this.f$0.lambda$openAddMember$20$ProfileActivity(arrayList, i);
    }

    public /* synthetic */ void needAddBot(User user) {
        -CC.$default$needAddBot(this, user);
    }
}
