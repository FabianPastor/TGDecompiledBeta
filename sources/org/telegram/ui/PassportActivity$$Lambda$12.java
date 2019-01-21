package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;

final /* synthetic */ class PassportActivity$$Lambda$12 implements OnClickListener {
    private final PassportActivity arg$1;
    private final ArrayList arg$2;

    PassportActivity$$Lambda$12(PassportActivity passportActivity, ArrayList arrayList) {
        this.arg$1 = passportActivity;
        this.arg$2 = arrayList;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$openAddDocumentAlert$23$PassportActivity(this.arg$2, dialogInterface, i);
    }
}
