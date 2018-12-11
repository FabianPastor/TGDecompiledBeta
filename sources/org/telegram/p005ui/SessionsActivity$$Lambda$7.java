package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.SessionsActivity$$Lambda$7 */
final /* synthetic */ class SessionsActivity$$Lambda$7 implements OnClickListener {
    private final SessionsActivity arg$1;
    private final int arg$2;
    private final boolean[] arg$3;

    SessionsActivity$$Lambda$7(SessionsActivity sessionsActivity, int i, boolean[] zArr) {
        this.arg$1 = sessionsActivity;
        this.arg$2 = i;
        this.arg$3 = zArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$10$SessionsActivity(this.arg$2, this.arg$3, dialogInterface, i);
    }
}
