package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$46vRdz1bQM_EHE2DXn2q36kGyZo implements OnClickListener {
    private final /* synthetic */ LoginActivity f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ String f$2;

    public /* synthetic */ -$$Lambda$LoginActivity$46vRdz1bQM_EHE2DXn2q36kGyZo(LoginActivity loginActivity, boolean z, String str) {
        this.f$0 = loginActivity;
        this.f$1 = z;
        this.f$2 = str;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$needShowInvalidAlert$1$LoginActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}
