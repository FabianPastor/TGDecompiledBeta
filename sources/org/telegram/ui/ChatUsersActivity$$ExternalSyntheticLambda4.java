package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class ChatUsersActivity$$ExternalSyntheticLambda4 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatUsersActivity f$0;
    public final /* synthetic */ TLRPC$User f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TLRPC$TL_chatAdminRights f$3;
    public final /* synthetic */ TLRPC$TL_chatBannedRights f$4;
    public final /* synthetic */ String f$5;
    public final /* synthetic */ boolean f$6;

    public /* synthetic */ ChatUsersActivity$$ExternalSyntheticLambda4(ChatUsersActivity chatUsersActivity, TLRPC$User tLRPC$User, TLObject tLObject, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, boolean z) {
        this.f$0 = chatUsersActivity;
        this.f$1 = tLRPC$User;
        this.f$2 = tLObject;
        this.f$3 = tLRPC$TL_chatAdminRights;
        this.f$4 = tLRPC$TL_chatBannedRights;
        this.f$5 = str;
        this.f$6 = z;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$createView$0(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, dialogInterface, i);
    }
}
