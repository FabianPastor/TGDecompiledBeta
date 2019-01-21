package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class ChatUsersActivity$$Lambda$21 implements OnClickListener {
    private final ChatUsersActivity arg$1;
    private final User arg$2;
    private final TLObject arg$3;
    private final TL_chatAdminRights arg$4;
    private final TL_chatBannedRights arg$5;
    private final boolean arg$6;

    ChatUsersActivity$$Lambda$21(ChatUsersActivity chatUsersActivity, User user, TLObject tLObject, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights, boolean z) {
        this.arg$1 = chatUsersActivity;
        this.arg$2 = user;
        this.arg$3 = tLObject;
        this.arg$4 = tL_chatAdminRights;
        this.arg$5 = tL_chatBannedRights;
        this.arg$6 = z;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$3$ChatUsersActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, dialogInterface, i);
    }
}
