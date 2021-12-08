package org.telegram.ui;

import java.util.Comparator;
import org.telegram.tgnet.TLObject;

public final /* synthetic */ class ChatUsersActivity$$ExternalSyntheticLambda2 implements Comparator {
    public final /* synthetic */ ChatUsersActivity f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ ChatUsersActivity$$ExternalSyntheticLambda2(ChatUsersActivity chatUsersActivity, int i) {
        this.f$0 = chatUsersActivity;
        this.f$1 = i;
    }

    public final int compare(Object obj, Object obj2) {
        return this.f$0.m1975lambda$sortUsers$16$orgtelegramuiChatUsersActivity(this.f$1, (TLObject) obj, (TLObject) obj2);
    }
}
