package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.ui.DialogsActivity.C13365;

final /* synthetic */ class DialogsActivity$5$$Lambda$2 implements OnClickListener {
    private final C13365 arg$1;
    private final boolean arg$2;
    private final boolean arg$3;
    private final TL_dialog arg$4;
    private final boolean arg$5;
    private final boolean arg$6;

    DialogsActivity$5$$Lambda$2(C13365 c13365, boolean z, boolean z2, TL_dialog tL_dialog, boolean z3, boolean z4) {
        this.arg$1 = c13365;
        this.arg$2 = z;
        this.arg$3 = z2;
        this.arg$4 = tL_dialog;
        this.arg$5 = z3;
        this.arg$6 = z4;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$5$DialogsActivity$5(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, dialogInterface, i);
    }
}
