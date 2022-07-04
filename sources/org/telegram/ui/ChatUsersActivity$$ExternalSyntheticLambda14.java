package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatUsersActivity$$ExternalSyntheticLambda14 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatUsersActivity f$0;
    public final /* synthetic */ CharSequence[] f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ TLRPC.TL_chatAdminRights f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ TLObject f$5;
    public final /* synthetic */ TLRPC.TL_chatBannedRights f$6;

    public /* synthetic */ ChatUsersActivity$$ExternalSyntheticLambda14(ChatUsersActivity chatUsersActivity, CharSequence[] charSequenceArr, long j, TLRPC.TL_chatAdminRights tL_chatAdminRights, String str, TLObject tLObject, TLRPC.TL_chatBannedRights tL_chatBannedRights) {
        this.f$0 = chatUsersActivity;
        this.f$1 = charSequenceArr;
        this.f$2 = j;
        this.f$3 = tL_chatAdminRights;
        this.f$4 = str;
        this.f$5 = tLObject;
        this.f$6 = tL_chatBannedRights;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3303x78b746bc(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, dialogInterface, i);
    }
}
