package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda58 implements DialogInterface.OnClickListener {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ TLRPC.User f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda58(GroupCallActivity groupCallActivity, TLRPC.User user, long j) {
        this.f$0 = groupCallActivity;
        this.f$1 = user;
        this.f$2 = j;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m2985x38663d33(this.f$1, this.f$2, dialogInterface, i);
    }
}
