package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

final /* synthetic */ class ChangeUsernameActivity$$Lambda$4 implements OnCancelListener {
    private final ChangeUsernameActivity arg$1;
    private final int arg$2;

    ChangeUsernameActivity$$Lambda$4(ChangeUsernameActivity changeUsernameActivity, int i) {
        this.arg$1 = changeUsernameActivity;
        this.arg$2 = i;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.arg$1.lambda$saveName$8$ChangeUsernameActivity(this.arg$2, dialogInterface);
    }
}
