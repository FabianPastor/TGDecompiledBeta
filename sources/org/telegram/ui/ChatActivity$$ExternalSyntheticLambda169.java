package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.GroupCreateActivity;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda169 implements GroupCreateActivity.ContactsAddActivityDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda169(ChatActivity chatActivity, int i) {
        this.f$0 = chatActivity;
        this.f$1 = i;
    }

    public final void didSelectUsers(ArrayList arrayList, int i) {
        this.f$0.lambda$createView$20(this.f$1, arrayList, i);
    }

    public /* synthetic */ void needAddBot(TLRPC$User tLRPC$User) {
        GroupCreateActivity.ContactsAddActivityDelegate.CC.$default$needAddBot(this, tLRPC$User);
    }
}
