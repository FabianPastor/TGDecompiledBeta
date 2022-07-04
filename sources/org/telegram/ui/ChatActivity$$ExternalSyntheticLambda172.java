package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.GroupCreateActivity;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda172 implements GroupCreateActivity.ContactsAddActivityDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda172(ChatActivity chatActivity, long j) {
        this.f$0 = chatActivity;
        this.f$1 = j;
    }

    public final void didSelectUsers(ArrayList arrayList, int i) {
        this.f$0.m2954lambda$createView$34$orgtelegramuiChatActivity(this.f$1, arrayList, i);
    }

    public /* synthetic */ void needAddBot(TLRPC.User user) {
        GroupCreateActivity.ContactsAddActivityDelegate.CC.$default$needAddBot(this, user);
    }
}
