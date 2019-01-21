package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate;

final /* synthetic */ class ChatUsersActivity$$Lambda$13 implements ChatRightsEditActivityDelegate {
    private final ChatUsersActivity arg$1;
    private final TLObject arg$2;
    private final int arg$3;

    ChatUsersActivity$$Lambda$13(ChatUsersActivity chatUsersActivity, TLObject tLObject, int i) {
        this.arg$1 = chatUsersActivity;
        this.arg$2 = tLObject;
        this.arg$3 = i;
    }

    public void didSetRights(int i, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights) {
        this.arg$1.lambda$null$10$ChatUsersActivity(this.arg$2, this.arg$3, i, tL_chatAdminRights, tL_chatBannedRights);
    }
}
