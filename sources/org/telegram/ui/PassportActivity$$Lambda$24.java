package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class PassportActivity$$Lambda$24 implements OnClickListener {
    private final PassportActivity arg$1;
    private final boolean[] arg$2;

    PassportActivity$$Lambda$24(PassportActivity passportActivity, boolean[] zArr) {
        this.arg$1 = passportActivity;
        this.arg$2 = zArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$createDocumentDeleteAlert$37$PassportActivity(this.arg$2, dialogInterface, i);
    }
}
