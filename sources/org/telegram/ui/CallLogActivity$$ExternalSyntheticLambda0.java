package org.telegram.ui;

import android.content.DialogInterface;

public final /* synthetic */ class CallLogActivity$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ CallLogActivity f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ boolean[] f$2;

    public /* synthetic */ CallLogActivity$$ExternalSyntheticLambda0(CallLogActivity callLogActivity, boolean z, boolean[] zArr) {
        this.f$0 = callLogActivity;
        this.f$1 = z;
        this.f$2 = zArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m2751lambda$showDeleteAlert$5$orgtelegramuiCallLogActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}
