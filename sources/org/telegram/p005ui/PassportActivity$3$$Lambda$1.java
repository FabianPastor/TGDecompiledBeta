package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.PassportActivity.C21963;

/* renamed from: org.telegram.ui.PassportActivity$3$$Lambda$1 */
final /* synthetic */ class PassportActivity$3$$Lambda$1 implements OnClickListener {
    private final C21963 arg$1;
    private final int arg$2;

    PassportActivity$3$$Lambda$1(C21963 c21963, int i) {
        this.arg$1 = c21963;
        this.arg$2 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onIdentityDone$1$PassportActivity$3(this.arg$2, dialogInterface, i);
    }
}
