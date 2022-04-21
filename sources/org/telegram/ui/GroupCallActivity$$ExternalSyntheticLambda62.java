package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda62 implements DialogInterface.OnClickListener {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ TLRPC.User f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda62(GroupCallActivity groupCallActivity, TLRPC.User user, long j) {
        this.f$0 = groupCallActivity;
        this.f$1 = user;
        this.f$2 = j;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m2225x617bcb5(this.f$1, this.f$2, dialogInterface, i);
    }
}
