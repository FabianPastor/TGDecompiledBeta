package org.telegram.ui;

import android.content.DialogInterface;
import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class ChatUsersActivity$$ExternalSyntheticLambda3 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatUsersActivity f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ TLRPC$User f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ TLObject f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ TLRPC$TL_chatAdminRights f$7;
    public final /* synthetic */ TLRPC$TL_chatBannedRights f$8;
    public final /* synthetic */ String f$9;

    public /* synthetic */ ChatUsersActivity$$ExternalSyntheticLambda3(ChatUsersActivity chatUsersActivity, ArrayList arrayList, TLRPC$User tLRPC$User, int i, boolean z, TLObject tLObject, int i2, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str) {
        this.f$0 = chatUsersActivity;
        this.f$1 = arrayList;
        this.f$2 = tLRPC$User;
        this.f$3 = i;
        this.f$4 = z;
        this.f$5 = tLObject;
        this.f$6 = i2;
        this.f$7 = tLRPC$TL_chatAdminRights;
        this.f$8 = tLRPC$TL_chatBannedRights;
        this.f$9 = str;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$createMenuForParticipant$6(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, dialogInterface, i);
    }
}
