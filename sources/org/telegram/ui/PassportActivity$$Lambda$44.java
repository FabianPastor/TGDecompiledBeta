package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class PassportActivity$$Lambda$44 implements OnClickListener {
    private final PassportActivity arg$1;

    PassportActivity$$Lambda$44(PassportActivity passportActivity) {
        this.arg$1 = passportActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onRequestPermissionsResultFragment$68$PassportActivity(dialogInterface, i);
    }
}
