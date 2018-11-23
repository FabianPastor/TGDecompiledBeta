package org.telegram.p005ui;

import android.view.View;
import android.view.View.OnClickListener;

/* renamed from: org.telegram.ui.PassportActivity$$Lambda$26 */
final /* synthetic */ class PassportActivity$$Lambda$26 implements OnClickListener {
    private final boolean[] arg$1;

    PassportActivity$$Lambda$26(boolean[] zArr) {
        this.arg$1 = zArr;
    }

    public void onClick(View view) {
        PassportActivity.lambda$createDocumentDeleteAlert$39$PassportActivity(this.arg$1, view);
    }
}
