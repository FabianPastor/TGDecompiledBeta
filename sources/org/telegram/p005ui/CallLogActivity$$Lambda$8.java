package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.CallLogActivity.CallLogRow;

/* renamed from: org.telegram.ui.CallLogActivity$$Lambda$8 */
final /* synthetic */ class CallLogActivity$$Lambda$8 implements OnClickListener {
    private final CallLogActivity arg$1;
    private final CallLogRow arg$2;

    CallLogActivity$$Lambda$8(CallLogActivity callLogActivity, CallLogRow callLogRow) {
        this.arg$1 = callLogActivity;
        this.arg$2 = callLogRow;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$1$CallLogActivity(this.arg$2, dialogInterface, i);
    }
}
