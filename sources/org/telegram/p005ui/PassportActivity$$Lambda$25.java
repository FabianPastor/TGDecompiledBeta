package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.PassportActivity$$Lambda$25 */
final /* synthetic */ class PassportActivity$$Lambda$25 implements OnClickListener {
    private final PassportActivity arg$1;
    private final boolean[] arg$2;

    PassportActivity$$Lambda$25(PassportActivity passportActivity, boolean[] zArr) {
        this.arg$1 = passportActivity;
        this.arg$2 = zArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$createDocumentDeleteAlert$38$PassportActivity(this.arg$2, dialogInterface, i);
    }
}
