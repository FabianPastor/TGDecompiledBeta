package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class ChatUsersActivity$$Lambda$4 implements OnClickListener {
    private final ChatUsersActivity arg$1;
    private final ArrayList arg$2;
    private final User arg$3;
    private final int arg$4;
    private final boolean arg$5;
    private final TLObject arg$6;
    private final int arg$7;
    private final TL_chatAdminRights arg$8;
    private final TL_chatBannedRights arg$9;

    ChatUsersActivity$$Lambda$4(ChatUsersActivity chatUsersActivity, ArrayList arrayList, User user, int i, boolean z, TLObject tLObject, int i2, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights) {
        this.arg$1 = chatUsersActivity;
        this.arg$2 = arrayList;
        this.arg$3 = user;
        this.arg$4 = i;
        this.arg$5 = z;
        this.arg$6 = tLObject;
        this.arg$7 = i2;
        this.arg$8 = tL_chatAdminRights;
        this.arg$9 = tL_chatBannedRights;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$createMenuForParticipant$10$ChatUsersActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, dialogInterface, i);
    }
}
