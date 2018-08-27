package org.telegram.ui;

import android.view.View;
import android.view.View.OnLongClickListener;
import org.telegram.messenger.SecureDocument;
import org.telegram.ui.PassportActivity.SecureDocumentCell;

final /* synthetic */ class PassportActivity$$Lambda$38 implements OnLongClickListener {
    private final PassportActivity arg$1;
    private final int arg$2;
    private final SecureDocument arg$3;
    private final SecureDocumentCell arg$4;
    private final String arg$5;

    PassportActivity$$Lambda$38(PassportActivity passportActivity, int i, SecureDocument secureDocument, SecureDocumentCell secureDocumentCell, String str) {
        this.arg$1 = passportActivity;
        this.arg$2 = i;
        this.arg$3 = secureDocument;
        this.arg$4 = secureDocumentCell;
        this.arg$5 = str;
    }

    public boolean onLongClick(View view) {
        return this.arg$1.lambda$addDocumentView$57$PassportActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, view);
    }
}
