package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.PassportActivity.C16473;

final /* synthetic */ class PassportActivity$3$$Lambda$1 implements OnClickListener {
    private final C16473 arg$1;
    private final int arg$2;

    PassportActivity$3$$Lambda$1(C16473 c16473, int i) {
        this.arg$1 = c16473;
        this.arg$2 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onIdentityDone$1$PassportActivity$3(this.arg$2, dialogInterface, i);
    }
}
