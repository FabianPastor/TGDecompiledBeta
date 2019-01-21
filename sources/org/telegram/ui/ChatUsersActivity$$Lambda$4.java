package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;

final /* synthetic */ class ChatUsersActivity$$Lambda$4 implements OnClickListener {
    private final ChatUsersActivity arg$1;
    private final CharSequence[] arg$2;
    private final int arg$3;
    private final TL_chatAdminRights arg$4;
    private final TLObject arg$5;
    private final TL_chatBannedRights arg$6;

    ChatUsersActivity$$Lambda$4(ChatUsersActivity chatUsersActivity, CharSequence[] charSequenceArr, int i, TL_chatAdminRights tL_chatAdminRights, TLObject tLObject, TL_chatBannedRights tL_chatBannedRights) {
        this.arg$1 = chatUsersActivity;
        this.arg$2 = charSequenceArr;
        this.arg$3 = i;
        this.arg$4 = tL_chatAdminRights;
        this.arg$5 = tLObject;
        this.arg$6 = tL_chatBannedRights;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$createMenuForParticipant$14$ChatUsersActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, dialogInterface, i);
    }
}
