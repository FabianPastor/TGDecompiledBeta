package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.SecureDocument;
import org.telegram.ui.PassportActivity.SecureDocumentCell;

final /* synthetic */ class PassportActivity$$Lambda$55 implements OnClickListener {
    private final PassportActivity arg$1;
    private final SecureDocument arg$2;
    private final int arg$3;
    private final SecureDocumentCell arg$4;
    private final String arg$5;

    PassportActivity$$Lambda$55(PassportActivity passportActivity, SecureDocument secureDocument, int i, SecureDocumentCell secureDocumentCell, String str) {
        this.arg$1 = passportActivity;
        this.arg$2 = secureDocument;
        this.arg$3 = i;
        this.arg$4 = secureDocumentCell;
        this.arg$5 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$56$PassportActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, dialogInterface, i);
    }
}
