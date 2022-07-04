package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ProfileActivity;

public final /* synthetic */ class ProfileActivity$5$$ExternalSyntheticLambda6 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ProfileActivity.AnonymousClass5 f$0;
    public final /* synthetic */ TLRPC.User f$1;

    public /* synthetic */ ProfileActivity$5$$ExternalSyntheticLambda6(ProfileActivity.AnonymousClass5 r1, TLRPC.User user) {
        this.f$0 = r1;
        this.f$1 = user;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m4435lambda$onItemClick$2$orgtelegramuiProfileActivity$5(this.f$1, dialogInterface, i);
    }
}
