package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ProfileActivity;

public final /* synthetic */ class ProfileActivity$6$$ExternalSyntheticLambda3 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ProfileActivity.AnonymousClass6 f$0;
    public final /* synthetic */ TLRPC$User f$1;

    public /* synthetic */ ProfileActivity$6$$ExternalSyntheticLambda3(ProfileActivity.AnonymousClass6 r1, TLRPC$User tLRPC$User) {
        this.f$0 = r1;
        this.f$1 = tLRPC$User;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onItemClick$2(this.f$1, dialogInterface, i);
    }
}
