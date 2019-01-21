package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate;

final /* synthetic */ class ChatUsersActivity$$Lambda$2 implements ChatRightsEditActivityDelegate {
    private final ChatUsersActivity arg$1;
    private final int arg$2;
    private final int arg$3;
    private final int arg$4;

    ChatUsersActivity$$Lambda$2(ChatUsersActivity chatUsersActivity, int i, int i2, int i3) {
        this.arg$1 = chatUsersActivity;
        this.arg$2 = i;
        this.arg$3 = i2;
        this.arg$4 = i3;
    }

    public void didSetRights(int i, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights) {
        this.arg$1.lambda$openRightsEdit2$7$ChatUsersActivity(this.arg$2, this.arg$3, this.arg$4, i, tL_chatAdminRights, tL_chatBannedRights);
    }
}
