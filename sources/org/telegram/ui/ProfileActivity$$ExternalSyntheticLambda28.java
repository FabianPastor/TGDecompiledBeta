package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.GroupCreateActivity;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda28 implements GroupCreateActivity.ContactsAddActivityDelegate {
    public final /* synthetic */ ProfileActivity f$0;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda28(ProfileActivity profileActivity) {
        this.f$0 = profileActivity;
    }

    public final void didSelectUsers(ArrayList arrayList, int i) {
        this.f$0.m3712lambda$openAddMember$24$orgtelegramuiProfileActivity(arrayList, i);
    }

    public /* synthetic */ void needAddBot(TLRPC.User user) {
        GroupCreateActivity.ContactsAddActivityDelegate.CC.$default$needAddBot(this, user);
    }
}
