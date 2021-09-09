package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.GroupCreateActivity;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda174 implements GroupCreateActivity.ContactsAddActivityDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda174(ChatActivity chatActivity, long j) {
        this.f$0 = chatActivity;
        this.f$1 = j;
    }

    public final void didSelectUsers(ArrayList arrayList, int i) {
        this.f$0.lambda$createView$21(this.f$1, arrayList, i);
    }

    public /* synthetic */ void needAddBot(TLRPC$User tLRPC$User) {
        GroupCreateActivity.ContactsAddActivityDelegate.CC.$default$needAddBot(this, tLRPC$User);
    }
}
