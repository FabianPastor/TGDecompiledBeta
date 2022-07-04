package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class GroupCreateActivity$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ GroupCreateActivity f$0;
    public final /* synthetic */ TLRPC.User f$1;

    public /* synthetic */ GroupCreateActivity$$ExternalSyntheticLambda0(GroupCreateActivity groupCreateActivity, TLRPC.User user) {
        this.f$0 = groupCreateActivity;
        this.f$1 = user;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3571lambda$createView$2$orgtelegramuiGroupCreateActivity(this.f$1, dialogInterface, i);
    }
}
