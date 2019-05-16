package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CallLogActivity$H8-jHw_nBqLvayxPjMSmdGRTSFk implements OnClickListener {
    private final /* synthetic */ CallLogActivity f$0;
    private final /* synthetic */ CallLogRow f$1;

    public /* synthetic */ -$$Lambda$CallLogActivity$H8-jHw_nBqLvayxPjMSmdGRTSFk(CallLogActivity callLogActivity, CallLogRow callLogRow) {
        this.f$0 = callLogActivity;
        this.f$1 = callLogRow;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$confirmAndDelete$7$CallLogActivity(this.f$1, dialogInterface, i);
    }
}
