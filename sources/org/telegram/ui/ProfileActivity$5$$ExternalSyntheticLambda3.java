package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ProfileActivity;

public final /* synthetic */ class ProfileActivity$5$$ExternalSyntheticLambda3 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ProfileActivity.AnonymousClass5 f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ DialogsActivity f$2;
    public final /* synthetic */ TLRPC$User f$3;

    public /* synthetic */ ProfileActivity$5$$ExternalSyntheticLambda3(ProfileActivity.AnonymousClass5 r1, long j, DialogsActivity dialogsActivity, TLRPC$User tLRPC$User) {
        this.f$0 = r1;
        this.f$1 = j;
        this.f$2 = dialogsActivity;
        this.f$3 = tLRPC$User;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onItemClick$5(this.f$1, this.f$2, this.f$3, dialogInterface, i);
    }
}
