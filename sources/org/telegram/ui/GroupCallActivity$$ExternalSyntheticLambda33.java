package org.telegram.ui;

import android.content.DialogInterface;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda33 implements DialogInterface.OnCancelListener {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda33(GroupCallActivity groupCallActivity, int i) {
        this.f$0 = groupCallActivity;
        this.f$1 = i;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.m2955lambda$inviteUserToCall$47$orgtelegramuiGroupCallActivity(this.f$1, dialogInterface);
    }
}
