package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.GroupCreateActivity.ContactsAddActivityDelegate;
import org.telegram.ui.GroupCreateActivity.ContactsAddActivityDelegate.-CC;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProfileActivity$kd4YT1P4AH1AD-EpsY7SnVeJBKE implements ContactsAddActivityDelegate {
    private final /* synthetic */ ProfileActivity f$0;

    public /* synthetic */ -$$Lambda$ProfileActivity$kd4YT1P4AH1AD-EpsY7SnVeJBKE(ProfileActivity profileActivity) {
        this.f$0 = profileActivity;
    }

    public final void didSelectUsers(ArrayList arrayList, int i) {
        this.f$0.lambda$openAddMember$18$ProfileActivity(arrayList, i);
    }

    public /* synthetic */ void needAddBot(User user) {
        -CC.$default$needAddBot(this, user);
    }
}
