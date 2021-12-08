package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ProfileActivity;

public final /* synthetic */ class ProfileActivity$4$$ExternalSyntheticLambda3 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ProfileActivity.AnonymousClass4 f$0;
    public final /* synthetic */ TLRPC.User f$1;

    public /* synthetic */ ProfileActivity$4$$ExternalSyntheticLambda3(ProfileActivity.AnonymousClass4 r1, TLRPC.User user) {
        this.f$0 = r1;
        this.f$1 = user;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3723lambda$onItemClick$2$orgtelegramuiProfileActivity$4(this.f$1, dialogInterface, i);
    }
}
