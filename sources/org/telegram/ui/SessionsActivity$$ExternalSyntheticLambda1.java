package org.telegram.ui;

import android.content.DialogInterface;

public final /* synthetic */ class SessionsActivity$$ExternalSyntheticLambda1 implements DialogInterface.OnClickListener {
    public final /* synthetic */ SessionsActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ boolean[] f$2;

    public /* synthetic */ SessionsActivity$$ExternalSyntheticLambda1(SessionsActivity sessionsActivity, int i, boolean[] zArr) {
        this.f$0 = sessionsActivity;
        this.f$1 = i;
        this.f$2 = zArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$createView$12(this.f$1, this.f$2, dialogInterface, i);
    }
}
