package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.TL_secureRequiredType;

final /* synthetic */ class PassportActivity$$Lambda$41 implements OnClickListener {
    private final PassportActivity arg$1;
    private final ArrayList arg$2;
    private final TL_secureRequiredType arg$3;
    private final boolean arg$4;

    PassportActivity$$Lambda$41(PassportActivity passportActivity, ArrayList arrayList, TL_secureRequiredType tL_secureRequiredType, boolean z) {
        this.arg$1 = passportActivity;
        this.arg$2 = arrayList;
        this.arg$3 = tL_secureRequiredType;
        this.arg$4 = z;
    }

    public void onClick(View view) {
        this.arg$1.lambda$addField$64$PassportActivity(this.arg$2, this.arg$3, this.arg$4, view);
    }
}
