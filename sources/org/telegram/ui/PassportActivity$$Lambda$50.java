package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.TL_secureRequiredType;

final /* synthetic */ class PassportActivity$$Lambda$50 implements OnClickListener {
    private final PassportActivity arg$1;
    private final TL_secureRequiredType arg$2;
    private final ArrayList arg$3;
    private final boolean arg$4;

    PassportActivity$$Lambda$50(PassportActivity passportActivity, TL_secureRequiredType tL_secureRequiredType, ArrayList arrayList, boolean z) {
        this.arg$1 = passportActivity;
        this.arg$2 = tL_secureRequiredType;
        this.arg$3 = arrayList;
        this.arg$4 = z;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$61$PassportActivity(this.arg$2, this.arg$3, this.arg$4, dialogInterface, i);
    }
}
