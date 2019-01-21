package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class ChatUsersActivity$$Lambda$3 implements OnClickListener {
    private final ChatUsersActivity arg$1;
    private final ArrayList arg$2;
    private final User arg$3;
    private final int arg$4;
    private final TL_chatAdminRights arg$5;
    private final TL_chatBannedRights arg$6;
    private final int arg$7;

    ChatUsersActivity$$Lambda$3(ChatUsersActivity chatUsersActivity, ArrayList arrayList, User user, int i, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights, int i2) {
        this.arg$1 = chatUsersActivity;
        this.arg$2 = arrayList;
        this.arg$3 = user;
        this.arg$4 = i;
        this.arg$5 = tL_chatAdminRights;
        this.arg$6 = tL_chatBannedRights;
        this.arg$7 = i2;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$createMenuForParticipant$9$ChatUsersActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, dialogInterface, i);
    }
}
