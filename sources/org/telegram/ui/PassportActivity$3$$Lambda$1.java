package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.PassportActivity.AnonymousClass3;

final /* synthetic */ class PassportActivity$3$$Lambda$1 implements OnClickListener {
    private final AnonymousClass3 arg$1;
    private final int arg$2;

    PassportActivity$3$$Lambda$1(AnonymousClass3 anonymousClass3, int i) {
        this.arg$1 = anonymousClass3;
        this.arg$2 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onIdentityDone$1$PassportActivity$3(this.arg$2, dialogInterface, i);
    }
}
