package org.telegram.ui;

import android.content.DialogInterface;
import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatUsersActivity$$ExternalSyntheticLambda11 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatUsersActivity f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ TLRPC.TL_chatAdminRights f$4;
    public final /* synthetic */ TLRPC.TL_chatBannedRights f$5;
    public final /* synthetic */ String f$6;
    public final /* synthetic */ boolean f$7;
    public final /* synthetic */ ArrayList f$8;
    public final /* synthetic */ int f$9;

    public /* synthetic */ ChatUsersActivity$$ExternalSyntheticLambda11(ChatUsersActivity chatUsersActivity, long j, int i, TLObject tLObject, TLRPC.TL_chatAdminRights tL_chatAdminRights, TLRPC.TL_chatBannedRights tL_chatBannedRights, String str, boolean z, ArrayList arrayList, int i2) {
        this.f$0 = chatUsersActivity;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = tLObject;
        this.f$4 = tL_chatAdminRights;
        this.f$5 = tL_chatBannedRights;
        this.f$6 = str;
        this.f$7 = z;
        this.f$8 = arrayList;
        this.f$9 = i2;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3299xdd5447b8(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, dialogInterface, i);
    }
}
