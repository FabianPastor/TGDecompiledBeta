package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatUsersActivity$Eu-obL29OUnjUXgxBF5NZEiw6cM implements OnClickListener {
    private final /* synthetic */ ChatUsersActivity f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ TLObject f$3;
    private final /* synthetic */ TL_chatAdminRights f$4;
    private final /* synthetic */ TL_chatBannedRights f$5;
    private final /* synthetic */ String f$6;
    private final /* synthetic */ boolean f$7;
    private final /* synthetic */ ArrayList f$8;
    private final /* synthetic */ int f$9;

    public /* synthetic */ -$$Lambda$ChatUsersActivity$Eu-obL29OUnjUXgxBF5NZEiw6cM(ChatUsersActivity chatUsersActivity, int i, int i2, TLObject tLObject, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights, String str, boolean z, ArrayList arrayList, int i3) {
        this.f$0 = chatUsersActivity;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = tLObject;
        this.f$4 = tL_chatAdminRights;
        this.f$5 = tL_chatBannedRights;
        this.f$6 = str;
        this.f$7 = z;
        this.f$8 = arrayList;
        this.f$9 = i3;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$5$ChatUsersActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, dialogInterface, i);
    }
}
