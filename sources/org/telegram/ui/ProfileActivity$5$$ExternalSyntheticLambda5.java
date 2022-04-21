package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ProfileActivity;

public final /* synthetic */ class ProfileActivity$5$$ExternalSyntheticLambda5 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ProfileActivity.AnonymousClass5 f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ DialogsActivity f$2;
    public final /* synthetic */ TLRPC.User f$3;

    public /* synthetic */ ProfileActivity$5$$ExternalSyntheticLambda5(ProfileActivity.AnonymousClass5 r1, long j, DialogsActivity dialogsActivity, TLRPC.User user) {
        this.f$0 = r1;
        this.f$1 = j;
        this.f$2 = dialogsActivity;
        this.f$3 = user;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3082lambda$onItemClick$5$orgtelegramuiProfileActivity$5(this.f$1, this.f$2, this.f$3, dialogInterface, i);
    }
}
