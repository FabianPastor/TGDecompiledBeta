package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class GroupCreateActivity$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ GroupCreateActivity f$0;
    public final /* synthetic */ TLRPC$User f$1;

    public /* synthetic */ GroupCreateActivity$$ExternalSyntheticLambda0(GroupCreateActivity groupCreateActivity, TLRPC$User tLRPC$User) {
        this.f$0 = groupCreateActivity;
        this.f$1 = tLRPC$User;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$createView$2(this.f$1, dialogInterface, i);
    }
}
