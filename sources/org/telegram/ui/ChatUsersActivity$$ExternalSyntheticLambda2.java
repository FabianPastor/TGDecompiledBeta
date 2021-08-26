package org.telegram.ui;

import android.content.DialogInterface;
import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;

public final /* synthetic */ class ChatUsersActivity$$ExternalSyntheticLambda2 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatUsersActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ TLRPC$TL_chatAdminRights f$4;
    public final /* synthetic */ TLRPC$TL_chatBannedRights f$5;
    public final /* synthetic */ String f$6;
    public final /* synthetic */ boolean f$7;
    public final /* synthetic */ ArrayList f$8;
    public final /* synthetic */ int f$9;

    public /* synthetic */ ChatUsersActivity$$ExternalSyntheticLambda2(ChatUsersActivity chatUsersActivity, int i, int i2, TLObject tLObject, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, boolean z, ArrayList arrayList, int i3) {
        this.f$0 = chatUsersActivity;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = tLObject;
        this.f$4 = tLRPC$TL_chatAdminRights;
        this.f$5 = tLRPC$TL_chatBannedRights;
        this.f$6 = str;
        this.f$7 = z;
        this.f$8 = arrayList;
        this.f$9 = i3;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$createMenuForParticipant$5(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, dialogInterface, i);
    }
}
