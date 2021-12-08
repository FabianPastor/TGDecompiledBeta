package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.GroupCreateActivity;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda89 implements GroupCreateActivity.ContactsAddActivityDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda89(ChatActivity chatActivity, long j) {
        this.f$0 = chatActivity;
        this.f$1 = j;
    }

    public final void didSelectUsers(ArrayList arrayList, int i) {
        this.f$0.m1685lambda$createView$29$orgtelegramuiChatActivity(this.f$1, arrayList, i);
    }

    public /* synthetic */ void needAddBot(TLRPC.User user) {
        GroupCreateActivity.ContactsAddActivityDelegate.CC.$default$needAddBot(this, user);
    }
}
