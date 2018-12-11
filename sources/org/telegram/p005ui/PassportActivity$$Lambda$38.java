package org.telegram.p005ui;

import android.view.View;
import android.view.View.OnClickListener;

/* renamed from: org.telegram.ui.PassportActivity$$Lambda$38 */
final /* synthetic */ class PassportActivity$$Lambda$38 implements OnClickListener {
    private final PassportActivity arg$1;
    private final int arg$2;

    PassportActivity$$Lambda$38(PassportActivity passportActivity, int i) {
        this.arg$1 = passportActivity;
        this.arg$2 = i;
    }

    public void onClick(View view) {
        this.arg$1.lambda$addDocumentView$56$PassportActivity(this.arg$2, view);
    }
}
