package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.GroupCreateActivity;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda35 implements GroupCreateActivity.ContactsAddActivityDelegate {
    public final /* synthetic */ ProfileActivity f$0;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda35(ProfileActivity profileActivity) {
        this.f$0 = profileActivity;
    }

    public final void didSelectUsers(ArrayList arrayList, int i) {
        this.f$0.lambda$openAddMember$24(arrayList, i);
    }

    public /* synthetic */ void needAddBot(TLRPC$User tLRPC$User) {
        GroupCreateActivity.ContactsAddActivityDelegate.CC.$default$needAddBot(this, tLRPC$User);
    }
}