package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate;

final /* synthetic */ class ChatUsersActivity$$Lambda$17 implements ChatRightsEditActivityDelegate {
    private final ChatUsersActivity arg$1;
    private final ArrayList arg$2;
    private final int arg$3;
    private final int arg$4;
    private final int arg$5;

    ChatUsersActivity$$Lambda$17(ChatUsersActivity chatUsersActivity, ArrayList arrayList, int i, int i2, int i3) {
        this.arg$1 = chatUsersActivity;
        this.arg$2 = arrayList;
        this.arg$3 = i;
        this.arg$4 = i2;
        this.arg$5 = i3;
    }

    public void didSetRights(int i, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights) {
        this.arg$1.lambda$null$8$ChatUsersActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, i, tL_chatAdminRights, tL_chatBannedRights);
    }
}
