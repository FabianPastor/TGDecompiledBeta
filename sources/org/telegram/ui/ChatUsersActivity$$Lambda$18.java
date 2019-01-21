package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;

final /* synthetic */ class ChatUsersActivity$$Lambda$18 implements OnClickListener {
    private final ChatUsersActivity arg$1;
    private final int arg$2;
    private final int arg$3;
    private final TLObject arg$4;
    private final TL_chatAdminRights arg$5;
    private final TL_chatBannedRights arg$6;
    private final boolean arg$7;
    private final ArrayList arg$8;
    private final int arg$9;

    ChatUsersActivity$$Lambda$18(ChatUsersActivity chatUsersActivity, int i, int i2, TLObject tLObject, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights, boolean z, ArrayList arrayList, int i3) {
        this.arg$1 = chatUsersActivity;
        this.arg$2 = i;
        this.arg$3 = i2;
        this.arg$4 = tLObject;
        this.arg$5 = tL_chatAdminRights;
        this.arg$6 = tL_chatBannedRights;
        this.arg$7 = z;
        this.arg$8 = arrayList;
        this.arg$9 = i3;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$9$ChatUsersActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, dialogInterface, i);
    }
}
