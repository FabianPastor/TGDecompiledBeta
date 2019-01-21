package org.telegram.ui;

import java.util.Comparator;
import org.telegram.tgnet.TLObject;

final /* synthetic */ class ChatUsersActivity$$Lambda$22 implements Comparator {
    private final ChatUsersActivity arg$1;

    ChatUsersActivity$$Lambda$22(ChatUsersActivity chatUsersActivity) {
        this.arg$1 = chatUsersActivity;
    }

    public int compare(Object obj, Object obj2) {
        return this.arg$1.lambda$null$0$ChatUsersActivity((TLObject) obj, (TLObject) obj2);
    }
}
