package org.telegram.ui;

import android.content.DialogInterface;

public final /* synthetic */ class ChangeUsernameActivity$$ExternalSyntheticLambda0 implements DialogInterface.OnCancelListener {
    public final /* synthetic */ ChangeUsernameActivity f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ ChangeUsernameActivity$$ExternalSyntheticLambda0(ChangeUsernameActivity changeUsernameActivity, int i) {
        this.f$0 = changeUsernameActivity;
        this.f$1 = i;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.lambda$saveName$8(this.f$1, dialogInterface);
    }
}
