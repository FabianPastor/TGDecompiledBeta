package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatUsersActivity$HYUTGOVW4TI14K8sWX3THB9cD78 implements OnClickListener {
    private final /* synthetic */ ChatUsersActivity f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ User f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ boolean f$4;
    private final /* synthetic */ TLObject f$5;
    private final /* synthetic */ int f$6;
    private final /* synthetic */ TL_chatAdminRights f$7;
    private final /* synthetic */ TL_chatBannedRights f$8;

    public /* synthetic */ -$$Lambda$ChatUsersActivity$HYUTGOVW4TI14K8sWX3THB9cD78(ChatUsersActivity chatUsersActivity, ArrayList arrayList, User user, int i, boolean z, TLObject tLObject, int i2, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights) {
        this.f$0 = chatUsersActivity;
        this.f$1 = arrayList;
        this.f$2 = user;
        this.f$3 = i;
        this.f$4 = z;
        this.f$5 = tLObject;
        this.f$6 = i2;
        this.f$7 = tL_chatAdminRights;
        this.f$8 = tL_chatBannedRights;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$createMenuForParticipant$6$ChatUsersActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, dialogInterface, i);
    }
}
