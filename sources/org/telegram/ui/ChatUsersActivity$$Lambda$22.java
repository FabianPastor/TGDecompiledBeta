package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate;

final /* synthetic */ class ChatUsersActivity$$Lambda$22 implements ChatRightsEditActivityDelegate {
    private final ChatUsersActivity arg$1;
    private final TLObject arg$2;

    ChatUsersActivity$$Lambda$22(ChatUsersActivity chatUsersActivity, TLObject tLObject) {
        this.arg$1 = chatUsersActivity;
        this.arg$2 = tLObject;
    }

    public void didSetRights(int i, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights) {
        this.arg$1.lambda$null$4$ChatUsersActivity(this.arg$2, i, tL_chatAdminRights, tL_chatBannedRights);
    }
}
