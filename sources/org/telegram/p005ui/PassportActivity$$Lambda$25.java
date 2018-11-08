package org.telegram.p005ui;

import android.view.View;
import android.view.View.OnClickListener;

/* renamed from: org.telegram.ui.PassportActivity$$Lambda$25 */
final /* synthetic */ class PassportActivity$$Lambda$25 implements OnClickListener {
    private final boolean[] arg$1;

    PassportActivity$$Lambda$25(boolean[] zArr) {
        this.arg$1 = zArr;
    }

    public void onClick(View view) {
        PassportActivity.lambda$createDocumentDeleteAlert$38$PassportActivity(this.arg$1, view);
    }
}
