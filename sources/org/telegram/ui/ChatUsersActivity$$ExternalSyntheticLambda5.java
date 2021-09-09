package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;

public final /* synthetic */ class ChatUsersActivity$$ExternalSyntheticLambda5 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatUsersActivity f$0;
    public final /* synthetic */ CharSequence[] f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ TLRPC$TL_chatAdminRights f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ TLObject f$5;
    public final /* synthetic */ TLRPC$TL_chatBannedRights f$6;

    public /* synthetic */ ChatUsersActivity$$ExternalSyntheticLambda5(ChatUsersActivity chatUsersActivity, CharSequence[] charSequenceArr, long j, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, String str, TLObject tLObject, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights) {
        this.f$0 = chatUsersActivity;
        this.f$1 = charSequenceArr;
        this.f$2 = j;
        this.f$3 = tLRPC$TL_chatAdminRights;
        this.f$4 = str;
        this.f$5 = tLObject;
        this.f$6 = tLRPC$TL_chatBannedRights;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$createMenuForParticipant$9(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, dialogInterface, i);
    }
}
