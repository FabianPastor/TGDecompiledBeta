package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.ChangeBioActivity$$Lambda$3 */
final /* synthetic */ class ChangeBioActivity$$Lambda$3 implements OnClickListener {
    private final ChangeBioActivity arg$1;
    private final int arg$2;

    ChangeBioActivity$$Lambda$3(ChangeBioActivity changeBioActivity, int i) {
        this.arg$1 = changeBioActivity;
        this.arg$2 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$saveName$5$ChangeBioActivity(this.arg$2, dialogInterface, i);
    }
}
