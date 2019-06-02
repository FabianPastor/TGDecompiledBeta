package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatUsersActivity$xwU0MxcdyPOafR1XH_TLaX0p0yc implements OnClickListener {
    private final /* synthetic */ ChatUsersActivity f$0;
    private final /* synthetic */ User f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ TL_chatAdminRights f$3;
    private final /* synthetic */ TL_chatBannedRights f$4;
    private final /* synthetic */ boolean f$5;

    public /* synthetic */ -$$Lambda$ChatUsersActivity$xwU0MxcdyPOafR1XH_TLaX0p0yc(ChatUsersActivity chatUsersActivity, User user, TLObject tLObject, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights, boolean z) {
        this.f$0 = chatUsersActivity;
        this.f$1 = user;
        this.f$2 = tLObject;
        this.f$3 = tL_chatAdminRights;
        this.f$4 = tL_chatBannedRights;
        this.f$5 = z;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$2$ChatUsersActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, dialogInterface, i);
    }
}
